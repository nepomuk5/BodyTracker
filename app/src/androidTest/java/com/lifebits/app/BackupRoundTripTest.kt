package com.lifebits.app

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.json.JSONObject
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Uses a separate SQLite filename and never touches real life_bits.db.
 * Run on an emulator with :app:connectedDebugAndroidTest.
 */
@RunWith(AndroidJUnit4::class)
class BackupRoundTripTest {
    private val testDatabaseName = "life_bits_backup_instrumentation_test.db"
    private lateinit var context: Context
    private lateinit var db: LifeDatabase

    @Before fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        context.deleteDatabase(testDatabaseName)
        db = LifeDatabase(context, testDatabaseName)
        db.writableDatabase
    }

    @After fun tearDown() {
        db.close()
        context.deleteDatabase(testDatabaseName)
    }

    @Test fun restoreRoundTripPreservesTimeAndOriginalCaptureTimestamp() {
        val custom = db.addTracker("Test-Tracker", "heart")
        val unknownTimeId = db.addEvent(custom, "2026-10-01")
        val editedTimeId = db.addEvent(custom, "2026-10-02")
        db.updateEvent(editedTimeId, "2026-10-03", "09:17")
        val before = db.events().associateBy { it.id }
        val json = db.exportJson()
        val originalTrackerCount = db.trackers(true).size

        // Simulate accidental edits and verify the original snapshot can restore them.
        db.deleteEvent(unknownTimeId)
        db.updateEvent(editedTimeId, "2026-11-01", null)
        assertEquals(1, db.eventCount())

        val preview = db.inspectBackup(json)
        assertEquals(originalTrackerCount, preview.trackerCount)
        assertEquals(2, preview.eventCount)
        assertEquals("2026-10-01", preview.earliestDate)
        assertEquals("2026-10-03", preview.latestDate)
        assertEquals(1, db.eventCount()) // inspection must not restore anything

        db.importJson(json)
        val after = db.events().associateBy { it.id }
        assertEquals(2, after.size)
        assertEquals(null, after[unknownTimeId]?.eventTime)
        assertEquals("09:17", after[editedTimeId]?.eventTime)
        assertEquals("2026-10-03", after[editedTimeId]?.eventDate)
        assertEquals(before[unknownTimeId]?.recordedAt, after[unknownTimeId]?.recordedAt)
        assertEquals(before[editedTimeId]?.recordedAt, after[editedTimeId]?.recordedAt)
        assertEquals("heart", db.trackers(true).single { it.id == custom }.icon)
    }

    @Test fun invalidTrackerReferenceIsRejectedWithoutChanges() {
        val tracker = db.addTracker("Sicherungstest", "cat")
        db.addEvent(tracker, "2026-10-04")
        val before = db.exportJson()
        val invalid = JSONObject(before)
        invalid.getJSONArray("events").getJSONObject(0).put("trackerId", 999999)

        assertThrows(IllegalArgumentException::class.java) {
            db.inspectBackup(invalid.toString())
        }
        assertThrows(IllegalArgumentException::class.java) {
            db.importJson(invalid.toString())
        }
        assertEquals(before, db.exportJson())
    }

    @Test fun invalidTimeAndDuplicateEventIdCannotReplaceData() {
        val tracker = db.addTracker("Sicherungstest", "book")
        db.addEvent(tracker, "2026-09-21")
        db.addEvent(tracker, "2026-09-22")
        val before = db.exportJson()

        val invalidTime = JSONObject(before)
        invalidTime.getJSONArray("events").getJSONObject(0).put("eventTime", "28:99")
        assertThrows(IllegalArgumentException::class.java) {
            db.importJson(invalidTime.toString())
        }

        val duplicate = JSONObject(before)
        val events = duplicate.getJSONArray("events")
        events.getJSONObject(1).put("id", events.getJSONObject(0).getLong("id"))
        assertThrows(IllegalArgumentException::class.java) {
            db.importJson(duplicate.toString())
        }

        assertEquals(before, db.exportJson())
    }

    @Test fun previewRejectsUnsupportedOrMalformedBackup() {
        val originalCount = db.trackers(true).size
        val file = JSONObject(db.exportJson()).put("version", 99)
        assertThrows(IllegalArgumentException::class.java) { db.inspectBackup(file.toString()) }
        assertThrows(Exception::class.java) { db.inspectBackup("not-json") }
        assertEquals(originalCount, db.trackers(true).size)
        assertEquals(0, db.eventCount())
        assertFalse(db.events().isNotEmpty())
        assertTrue(originalCount > 0)
    }
}
