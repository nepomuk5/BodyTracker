package com.lifebits.app

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate

/**
 * eventDate = the day the event occurred (editable)
 * eventTime = optional actual time of the event (never inferred from a tap)
 * recordedAt = immutable system timestamp of the tap or later manual entry
 */
data class LifeTracker(val id: Long, val name: String, val icon: String, val active: Boolean, val sortOrder: Int)
data class LifeEntry(val id: Long, val trackerId: Long, val eventDate: String, val eventTime: String?, val recordedAt: Long)

/** A read-only preview; inspecting a backup must never mutate the database. */
data class BackupSummary(
    val trackerCount: Int,
    val eventCount: Int,
    val earliestDate: String?,
    val latestDate: String?
)

class LifeDatabase(context: Context, databaseName: String = "life_bits.db") :
    SQLiteOpenHelper(context.applicationContext, databaseName, null, 2) {
    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""CREATE TABLE trackers (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            name TEXT NOT NULL, icon TEXT NOT NULL,
            active INTEGER NOT NULL DEFAULT 1,
            sort_order INTEGER NOT NULL DEFAULT 0
        )""")
        db.execSQL("""CREATE TABLE events (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            tracker_id INTEGER NOT NULL REFERENCES trackers(id),
            event_date TEXT NOT NULL,
            event_time TEXT,
            recorded_at INTEGER NOT NULL
        )""")
        db.execSQL("CREATE INDEX event_date_idx ON events(event_date)")
        val defaults = listOf(
            "Niesen" to "sneeze", "Kacka" to "poop", "Migräne" to "migraine",
            "Kotzen" to "vomit", "Schluckauf" to "hiccup",
            "Halsschmerzen" to "throat", "Alkohol" to "alcohol"
        )
        defaults.forEachIndexed { index, (name, icon) ->
            db.insert("trackers", null, ContentValues().apply {
                put("name", name); put("icon", icon); put("active", 1); put("sort_order", index)
            })
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            // Existing event histories are never deleted. Rename the prior starter
            // "Schluckauf" icon and add the four new starter categories.
            db.execSQL("UPDATE trackers SET icon='hiccup' WHERE name='Schluckauf' AND icon='star'")
            val newDefaults = listOf(
                "Migräne" to "migraine", "Kotzen" to "vomit",
                "Halsschmerzen" to "throat", "Alkohol" to "alcohol"
            )
            newDefaults.forEach { (name, icon) ->
                db.rawQuery("SELECT COUNT(*) FROM trackers WHERE name=?", arrayOf(name)).use { c ->
                    c.moveToFirst()
                    if (c.getInt(0) == 0) {
                        db.insertOrThrow("trackers", null, ContentValues().apply {
                            put("name", name); put("icon", icon); put("active", 1)
                            put("sort_order", when(name) {
                                "Migräne" -> 2; "Kotzen" -> 3;
                                "Halsschmerzen" -> 5; else -> 6
                            })
                        })
                    }
                }
            }
            // Only old untouched starter categories are archived. This retains
            // history, custom trackers and any prior edits/renames.
            listOf("Gähnen" to "yawn", "Ohrwurm" to "music", "Pupsen" to "cloud").forEach { (name, icon) ->
                db.execSQL("UPDATE trackers SET active=0 WHERE name=? AND icon=?", arrayOf(name, icon))
            }
            db.execSQL("UPDATE trackers SET sort_order=4 WHERE name='Schluckauf' AND icon='hiccup'")
        }
    }

    @Synchronized fun trackers(includeArchived: Boolean = false): List<LifeTracker> {
        val result = mutableListOf<LifeTracker>()
        val where = if (includeArchived) null else "active = 1"
        readableDatabase.query("trackers", null, where, null, null, null, "sort_order, id").use { c ->
            while (c.moveToNext()) result.add(LifeTracker(
                c.getLong(c.getColumnIndexOrThrow("id")),
                c.getString(c.getColumnIndexOrThrow("name")),
                c.getString(c.getColumnIndexOrThrow("icon")),
                c.getInt(c.getColumnIndexOrThrow("active")) == 1,
                c.getInt(c.getColumnIndexOrThrow("sort_order"))
            ))
        }
        return result
    }

    @Synchronized fun addTracker(name: String, icon: String): Long {
        val order = trackers(true).maxOfOrNull { it.sortOrder }?.plus(1) ?: 0
        return writableDatabase.insertOrThrow("trackers", null, ContentValues().apply {
            put("name", name.trim()); put("icon", icon); put("active", 1); put("sort_order", order)
        })
    }

    @Synchronized fun updateTracker(id: Long, name: String, icon: String, active: Boolean) {
        writableDatabase.update("trackers", ContentValues().apply {
            put("name", name.trim()); put("icon", icon); put("active", if (active) 1 else 0)
        }, "id=?", arrayOf(id.toString()))
    }

    @Synchronized fun addEvent(trackerId: Long, day: String = LocalDate.now().toString()): Long {
        // Actual event time deliberately stays NULL even if logged immediately.
        return writableDatabase.insertOrThrow("events", null, ContentValues().apply {
            put("tracker_id", trackerId)
            put("event_date", day)
            putNull("event_time")
            put("recorded_at", System.currentTimeMillis())
        })
    }

    @Synchronized fun updateEvent(id: Long, date: String, eventTime: String?) {
        writableDatabase.update("events", ContentValues().apply {
            put("event_date", date)
            if (eventTime.isNullOrBlank()) putNull("event_time") else put("event_time", eventTime)
        }, "id=?", arrayOf(id.toString()))
    }

    /** Restore the original row for quick-delete undo, preserving recordedAt. */
    @Synchronized fun restoreEvent(entry: LifeEntry) {
        writableDatabase.insertOrThrow("events", null, ContentValues().apply {
            put("id", entry.id)
            put("tracker_id", entry.trackerId)
            put("event_date", entry.eventDate)
            if (entry.eventTime == null) putNull("event_time") else put("event_time", entry.eventTime)
            put("recorded_at", entry.recordedAt)
        })
    }

    @Synchronized fun deleteEvent(id: Long) {
        writableDatabase.delete("events", "id=?", arrayOf(id.toString()))
    }

    @Synchronized fun events(date: String? = null): List<LifeEntry> {
        val list = mutableListOf<LifeEntry>()
        readableDatabase.query(
            "events", null, if (date == null) null else "event_date=?",
            if (date == null) null else arrayOf(date), null, null,
            "event_date DESC, recorded_at DESC, id DESC"
        ).use { c ->
            while (c.moveToNext()) list.add(LifeEntry(
                c.getLong(c.getColumnIndexOrThrow("id")),
                c.getLong(c.getColumnIndexOrThrow("tracker_id")),
                c.getString(c.getColumnIndexOrThrow("event_date")),
                c.getString(c.getColumnIndexOrThrow("event_time")),
                c.getLong(c.getColumnIndexOrThrow("recorded_at"))
            ))
        }
        return list
    }

    @Synchronized fun countForDay(date: String): Map<Long, Int> {
        val counts = mutableMapOf<Long, Int>()
        readableDatabase.rawQuery(
            "SELECT tracker_id, COUNT(*) FROM events WHERE event_date=? GROUP BY tracker_id", arrayOf(date)
        ).use { c -> while (c.moveToNext()) counts[c.getLong(0)] = c.getInt(1) }
        return counts
    }

    /** Counts for a date interval [start, endExclusive); ISO dates sort lexically. */
    @Synchronized fun countsBetween(start: String, endExclusive: String): Map<Long, Int> {
        val result = mutableMapOf<Long, Int>()
        readableDatabase.rawQuery(
            "SELECT tracker_id, COUNT(*) FROM events WHERE event_date >= ? AND event_date < ? GROUP BY tracker_id",
            arrayOf(start, endExclusive)
        ).use { c ->
            while (c.moveToNext()) result[c.getLong(0)] = c.getInt(1)
        }
        return result
    }

    @Synchronized fun daysWithEventsBetween(start: String, endExclusive: String): Int =
        readableDatabase.rawQuery(
            "SELECT COUNT(DISTINCT event_date) FROM events WHERE event_date >= ? AND event_date < ?",
            arrayOf(start, endExclusive)
        ).use { c -> if (c.moveToFirst()) c.getInt(0) else 0 }

    @Synchronized fun monthlyCounts(year: Int): Map<Int, Int> {
        val result = mutableMapOf<Int, Int>()
        readableDatabase.rawQuery(
            "SELECT CAST(substr(event_date,6,2) AS INTEGER), COUNT(*) FROM events WHERE event_date >= ? AND event_date < ? GROUP BY substr(event_date,6,2)",
            arrayOf(LocalDate.of(year,1,1).toString(), LocalDate.of(year+1,1,1).toString())
        ).use { c -> while (c.moveToNext()) result[c.getInt(0)] = c.getInt(1) }
        return result
    }

    @Synchronized fun daysInMonth(month: String): Set<String> {
        val days = mutableSetOf<String>()
        readableDatabase.rawQuery(
            "SELECT DISTINCT event_date FROM events WHERE event_date LIKE ?", arrayOf("$month%")
        ).use { c -> while (c.moveToNext()) days.add(c.getString(0)) }
        return days
    }

    @Synchronized fun exportJson(): String {
        val root = JSONObject().put("format", "life-bits").put("version", 1)
        val allTrackers = JSONArray()
        trackers(true).forEach { t -> allTrackers.put(JSONObject()
            .put("id", t.id).put("name", t.name).put("icon", t.icon)
            .put("active", t.active).put("sortOrder", t.sortOrder)) }
        val allEvents = JSONArray()
        events().forEach { e -> allEvents.put(JSONObject()
            .put("id", e.id).put("trackerId", e.trackerId)
            .put("eventDate", e.eventDate)
            .put("eventTime", e.eventTime ?: JSONObject.NULL)
            .put("recordedAt", e.recordedAt)) }
        root.put("trackers", allTrackers).put("events", allEvents)
        return root.toString(2)
    }

    /** Validate the entire file BEFORE any transaction or confirmation prompt. */
    @Synchronized fun inspectBackup(json: String): BackupSummary {
        val root = JSONObject(json)
        require(root.optString("format") == "life-bits" && root.optInt("version") == 1) {
            "Keine unterstützte Life Bits Sicherung."
        }
        val t = root.getJSONArray("trackers")
        val e = root.getJSONArray("events")
        require(t.length() <= 5000 && e.length() <= 500000) { "Die Datei ist zu groß." }
        val validIds = mutableSetOf<Long>()
        for (i in 0 until t.length()) {
            val item = t.getJSONObject(i)
            val id = item.getLong("id")
            require(id > 0 && validIds.add(id) && item.getString("name").isNotBlank()) {
                "Ungültiger Tracker."
            }
            require(item.getString("icon").isNotBlank()) { "Tracker ohne Icon." }
            item.getBoolean("active")
            item.getInt("sortOrder")
        }
        val eventIds = mutableSetOf<Long>()
        var earliest: String? = null
        var latest: String? = null
        for (i in 0 until e.length()) {
            val item = e.getJSONObject(i)
            val eventId = item.getLong("id")
            require(eventId > 0 && eventIds.add(eventId)) { "Doppelte Ereignis-ID." }
            require(item.getLong("trackerId") in validIds) { "Eintrag ohne Tracker." }
            val date = LocalDate.parse(item.getString("eventDate")).toString()
            if (earliest == null || date < earliest) earliest = date
            if (latest == null || date > latest) latest = date
            if (!item.isNull("eventTime")) {
                require(Regex("^(?:[01]\\d|2[0-3]):[0-5]\\d$").matches(item.getString("eventTime"))) {
                    "Ungültige Ereignisuhrzeit."
                }
            }
            require(item.getLong("recordedAt") >= 0L) { "Ungültige Erfassungszeit." }
        }
        return BackupSummary(t.length(), e.length(), earliest, latest)
    }

    /** Number of stored rows, without loading potentially large histories into memory. */
    @Synchronized fun eventCount(): Int = readableDatabase.rawQuery(
        "SELECT COUNT(*) FROM events", null
    ).use { c -> if (c.moveToFirst()) c.getInt(0) else 0 }

    /** Atomic replacement, including a second validation pass immediately before writing. */
    @Synchronized fun importJson(json: String) {
        inspectBackup(json)
        val root = JSONObject(json)
        val t = root.getJSONArray("trackers")
        val e = root.getJSONArray("events")
        val db = writableDatabase
        db.beginTransaction()
        try {
            db.delete("events", null, null)
            db.delete("trackers", null, null)
            for (i in 0 until t.length()) {
                val item = t.getJSONObject(i)
                db.insertOrThrow("trackers", null, ContentValues().apply {
                    put("id", item.getLong("id")); put("name", item.getString("name"))
                    put("icon", item.getString("icon")); put("active", if (item.getBoolean("active")) 1 else 0)
                    put("sort_order", item.getInt("sortOrder"))
                })
            }
            for (i in 0 until e.length()) {
                val item = e.getJSONObject(i)
                db.insertOrThrow("events", null, ContentValues().apply {
                    put("id", item.getLong("id")); put("tracker_id", item.getLong("trackerId"))
                    put("event_date", item.getString("eventDate"))
                    if (item.isNull("eventTime")) putNull("event_time")
                    else put("event_time", item.getString("eventTime"))
                    put("recorded_at", item.getLong("recordedAt"))
                })
            }
            db.setTransactionSuccessful()
        } finally { db.endTransaction() }
    }
}
