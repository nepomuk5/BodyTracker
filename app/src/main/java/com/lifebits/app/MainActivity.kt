package com.lifebits.app

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.HapticFeedbackConstants
import android.view.View
import android.widget.EditText
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

/**
 * One-tap, offline-first Android application. No permissions, accounts or analytics.
 * All example tracking buttons save data to SQLite; no demo-only fake state.
 */
class MainActivity : Activity() {
    private lateinit var db: LifeDatabase
    private var tab = 0
    private var chosenDay: LocalDate = LocalDate.now()
    private var month: YearMonth = YearMonth.now()
    private val locale = Locale.GERMANY
    private val dateFormat = DateTimeFormatter.ofPattern("d. MMMM yyyy", locale)
    private val timeFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy · HH:mm", locale)
    private lateinit var content: LinearLayout

    private fun dp(value: Int) = UiKit.dp(this, value)
    private fun lp() = LinearLayout.LayoutParams(-1, -2)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = LifeDatabase(this)
        window.statusBarColor = UiKit.color(UiKit.BACKGROUND)
        window.navigationBarColor = UiKit.color("#F8D4E7")
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        draw()
    }

    override fun onResume() {
        super.onResume()
        if (::content.isInitialized) draw() // widget taps update when app becomes visible again
    }

    private fun draw() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(UiKit.color(UiKit.BACKGROUND))
        }
        setContentView(root)
        val scroll = ScrollView(this).apply {
            isFillViewport = false
            clipToPadding = false
        }
        content = UiKit.box(this, pad = 16)
        scroll.addView(content)
        root.addView(scroll, LinearLayout.LayoutParams(-1, 0, 1f))
        when (tab) {
            0 -> renderToday()
            1 -> renderHistory()
            2 -> renderTrackers()
            3 -> renderStudio()
        }
        root.addView(bottomNav())
    }

    private fun title(title: String, subtitle: String? = null) {
        val top = UiKit.box(this, vertical = false, pad = 8)
        top.gravity = Gravity.CENTER_VERTICAL
        val left = UiKit.box(this, pad = 0)
        left.addView(UiKit.heading(this, title))
        if (subtitle != null) left.addView(UiKit.text(this, subtitle, 12f, muted = true))
        top.addView(left, LinearLayout.LayoutParams(0, -2, 1f))
        top.addView(UiKit.icon(this, "cat", 38))
        content.addView(top)
        content.addView(UiKit.gap(this, 10))
    }

    private fun section(heading: String) {
        content.addView(UiKit.gap(this, 16))
        content.addView(UiKit.text(this, heading, 16f, true, pixel = true))
        content.addView(UiKit.gap(this, 9))
    }

    private fun bottomNav(): View {
        val nav = UiKit.box(this, vertical = false, pad = 7, fill = "#FFE1EF")
        val options = listOf("⌂" to "Heute", "▦" to "Verlauf", "♡" to "Tracker", "✧" to "Widget")
        options.forEachIndexed { idx, (glyph, label) ->
            val item = UiKit.box(this, pad = 5, fill = if (tab == idx) "#FFA8D1" else "#FFEAF4")
            item.gravity = Gravity.CENTER
            val ico = UiKit.text(this, glyph, 22f, true, pixel = true)
            ico.gravity = Gravity.CENTER
            item.addView(ico)
            val txt = UiKit.text(this, label, 11f, true)
            txt.gravity = Gravity.CENTER
            item.addView(txt)
            item.isClickable = true
            item.setOnClickListener { tab = idx; draw() }
            nav.addView(item, LinearLayout.LayoutParams(0, -2, 1f).apply {
                marginStart = dp(2); marginEnd = dp(2)
            })
        }
        return nav
    }

    private fun renderToday() {
        title("✦ RANDOM LIFE STATS", "Little moments, big statistics ♡")
        val headline = UiKit.box(this, vertical = false, fill = "#FFF7FB")
        headline.gravity = Gravity.CENTER_VERTICAL
        val left = UiKit.box(this, pad = 0)
        left.addView(UiKit.text(this, "✦ HEUTE", 18f, true, pixel = true))
        left.addView(UiKit.text(this, LocalDate.now().format(dateFormat), 12f, muted = true))
        headline.addView(left, LinearLayout.LayoutParams(0, -2, 1f))
        headline.addView(UiKit.icon(this, "cat", 43))
        content.addView(headline)
        content.addView(UiKit.gap(this, 12))
        val trackers = db.trackers()
        val counts = db.countForDay(LocalDate.now().toString())
        if (trackers.isEmpty()) {
            content.addView(UiKit.text(this, "Noch keine aktiven Tracker. Erstelle deinen ersten unter Tracker ♡", 14f))
        } else {
            val grid = GridLayout(this).apply { columnCount = 4; alignmentMode = GridLayout.ALIGN_BOUNDS }
            trackers.forEachIndexed { index, t ->
                val card = UiKit.box(this, pad = 3, fill = if (index % 3 == 0) "#FFD0E7" else "#FFE0F0")
                card.elevation = dp(2).toFloat()
                card.gravity = Gravity.CENTER
                card.addView(UiKit.icon(this, t.icon, 49))
                val name = UiKit.text(this, t.name, 10f, true)
                name.gravity = Gravity.CENTER
                name.maxLines = 2
                name.minLines = 2
                card.addView(name)
                val count = UiKit.text(this, "${counts[t.id] ?: 0}", 13f, true, pixel = true)
                count.gravity = Gravity.CENTER
                count.setMinWidth(dp(34))
                count.background = UiKit.bg("#FFF8FC", "#ED84B5", dp(11).toFloat(), dp(1))
                card.addView(count)
                card.setOnClickListener { tapped ->
                    tapped.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                    log(t.id)
                }
                grid.addView(card, GridLayout.LayoutParams(
                    GridLayout.spec(index / 4), GridLayout.spec(index % 4, 1f)
                ).apply {
                    width = 0; height = dp(123)
                    setMargins(dp(2), dp(2), dp(2), dp(2))
                })
            }
            content.addView(grid)
        }
        section("✦ HEUTIGE EINTRÄGE")
        renderEntryList(LocalDate.now())
        content.addView(UiKit.gap(this, 12))
        content.addView(UiKit.button(this, "+ EINTRAG NACHTRAGEN", { openAddForDay(LocalDate.now()) }))
        content.addView(UiKit.gap(this, 15))
        content.addView(UiKit.text(this, "Jeder Klick = ein Ereignis. Die tatsächliche Uhrzeit bleibt zunächst unbekannt; die Erfassungszeit wird automatisch gespeichert.", 12f, muted = true))
    }

    private fun log(id: Long, day: LocalDate = LocalDate.now()) {
        try {
            db.addEvent(id, day.toString())
            LifeWidgetProvider.updateAll(this)
            draw()
        } catch (ex: Exception) {
            Toast.makeText(this, "Konnte nicht speichern: ${ex.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun renderEntryList(day: LocalDate) {
        val entries = db.events(day.toString())
        val trackers = db.trackers(true).associateBy { it.id }
        if (entries.isEmpty()) {
            val empty = UiKit.box(this, fill = "#FFF9FC")
            empty.addView(UiKit.text(this, "✧ Noch keine Life Bits an diesem Tag.", 13f, muted = true))
            content.addView(empty)
            return
        }
        entries.forEach { e ->
            val t = trackers[e.trackerId] ?: return@forEach
            val row = UiKit.box(this, vertical = false, pad = 9, fill = "#FFF9FC")
            row.gravity = Gravity.CENTER_VERTICAL
            row.addView(UiKit.icon(this, t.icon, 40))
            val info = UiKit.box(this, pad = 0)
            info.addView(UiKit.text(this, t.name, 14f, true))
            info.addView(UiKit.text(this,
                "Ereigniszeit: ${e.eventTime ?: "unbekannt"}", 11f, muted = true))
            info.addView(UiKit.text(this,
                "Erfasst: ${formatRecorded(e.recordedAt)}", 10f, muted = true))
            row.addView(info, LinearLayout.LayoutParams(0, -2, 1f).apply { marginStart = dp(9) })
            row.addView(UiKit.text(this, "✎", 18f, true, muted = true))
            row.setOnClickListener { editEntry(e, t) }
            content.addView(row, UiKit.margin(this, bottom = 6))
        }
    }

    private fun formatRecorded(millis: Long): String =
        Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).format(timeFormat)

    private fun renderHistory() {
        title("♡ VERLAUF", "Kalender & Einträge bearbeiten")
        val nav = UiKit.box(this, vertical = false, fill = "#FFF9FC", pad = 8)
        nav.gravity = Gravity.CENTER_VERTICAL
        nav.addView(UiKit.button(this, "‹", { month = month.minusMonths(1); chosenDay = month.atDay(1); draw() }, false))
        val monthLabel = UiKit.text(this,
            "${month.month.getDisplayName(TextStyle.FULL, locale)} ${month.year}", 15f, true, pixel = true)
        monthLabel.gravity = Gravity.CENTER
        nav.addView(monthLabel, LinearLayout.LayoutParams(0, -2, 1f))
        nav.addView(UiKit.button(this, "›", { month = month.plusMonths(1); chosenDay = month.atDay(1); draw() }, false))
        content.addView(nav)
        content.addView(UiKit.gap(this, 8))
        val calendar = UiKit.box(this, fill = "#FFF9FC", pad = 6)
        val names = listOf("Mo", "Di", "Mi", "Do", "Fr", "Sa", "So")
        val header = LinearLayout(this)
        names.forEach { n ->
            val text = UiKit.text(this, n, 11f, true, muted = true)
            text.gravity = Gravity.CENTER
            header.addView(text, LinearLayout.LayoutParams(0, -2, 1f))
        }
        calendar.addView(header)
        val startOffset = month.atDay(1).dayOfWeek.value - 1
        val highlight = db.daysInMonth(month.toString())
        val count = month.lengthOfMonth()
        val weekCount = (startOffset + count + 6) / 7
        for (rowNum in 0 until weekCount) {
            val line = LinearLayout(this)
            for (col in 0 until 7) {
                val num = rowNum * 7 + col + 1 - startOffset
                val cell = UiKit.box(this, pad = 3)
                cell.gravity = Gravity.CENTER
                if (num in 1..count) {
                    val day = month.atDay(num)
                    if (day == chosenDay) cell.background = UiKit.bg("#FFB4D9", "#C4538D", dp(11).toFloat(), dp(2))
                    val label = UiKit.text(this, "$num", 14f, day == chosenDay)
                    label.gravity = Gravity.CENTER
                    cell.addView(label)
                    val badge = UiKit.text(this, if (day.toString() in highlight) "♥" else " ", 10f)
                    badge.gravity = Gravity.CENTER
                    cell.addView(badge)
                    cell.setOnClickListener { chosenDay = day; draw() }
                }
                line.addView(cell, LinearLayout.LayoutParams(0, dp(52), 1f))
            }
            calendar.addView(line)
        }
        content.addView(calendar)
        section(chosenDay.format(dateFormat))
        renderEntryList(chosenDay)
        content.addView(UiKit.gap(this, 10))
        content.addView(UiKit.button(this, "+ AN DIESEM TAG NACHTRAGEN", { openAddForDay(chosenDay) }))
        content.addView(UiKit.gap(this, 12))
        content.addView(UiKit.button(this, "↗ ZU EINEM DATUM SPRINGEN", {
            pickDate(chosenDay) { day -> chosenDay = day; month = YearMonth.from(day); draw() }
        }, filled = false))
    }

    private fun renderTrackers() {
        title("♡ TRACKER ♡", "Deine kleine Pixel-Sammlung")
        val all = db.trackers(true)
        val grid = GridLayout(this).apply { columnCount = 3 }
        all.forEachIndexed { index, t ->
            val card = UiKit.box(this, pad = 7, fill = if (t.active) "#FFD5E8" else "#F3E8EE")
            card.elevation = dp(2).toFloat()
            card.gravity = Gravity.CENTER
            card.addView(UiKit.icon(this, t.icon, 53))
            val name = UiKit.text(this, t.name, 11f, true)
            name.gravity = Gravity.CENTER
            name.maxLines = 1
            name.ellipsize = android.text.TextUtils.TruncateAt.END
            card.addView(name)
            card.addView(UiKit.text(this, if (t.active) "⋯" else "Archiv", 10f, muted = true))
            card.setOnClickListener { trackerDialog(t) }
            grid.addView(card, GridLayout.LayoutParams(
                GridLayout.spec(index / 3), GridLayout.spec(index % 3, 1f)
            ).apply { width = 0; height = dp(112); setMargins(dp(3),dp(3),dp(3),dp(3)) })
        }
        content.addView(grid)
        content.addView(UiKit.gap(this, 16))
        content.addView(UiKit.button(this, "+ NEUEN TRACKER ERSTELLEN", { trackerDialog(null) }))
        section("DEINE DATEN GEHÖREN DIR")
        content.addView(UiKit.text(this, "Alle Ereignisse bleiben lokal auf deinem Handy. Sichere sie regelmäßig als JSON-Datei – inklusive Ereignistag, optionaler Uhrzeit und Erfassungszeit.", 12f, muted = true))
        content.addView(UiKit.gap(this, 12))
        content.addView(UiKit.button(this, "♡ BACKUP EXPORTIEREN", { exportBackup() }, false))
        content.addView(UiKit.gap(this, 8))
        content.addView(UiKit.button(this, "↙ BACKUP IMPORTIEREN", { importBackup() }, false))
    }

    private fun renderStudio() {
        title("♡ WIDGET STUDIO", "Pink Pixel Power auf deinem Homescreen")
        val preview = UiKit.box(this, fill = "#FFD4E9", pad = 14)
        val line = UiKit.box(this, vertical = false, pad = 0)
        db.trackers().take(4).forEach { t ->
            val tile = UiKit.box(this, pad = 4, fill = "#FFF0F8")
            tile.gravity = Gravity.CENTER
            tile.addView(UiKit.icon(this, t.icon, 52))
            line.addView(tile, LinearLayout.LayoutParams(0, dp(92), 1f).apply {
                setMargins(dp(2), dp(2), dp(2), dp(2))
            })
        }
        preview.addView(line)
        content.addView(preview)
        content.addView(UiKit.gap(this, 12))
        content.addView(UiKit.text(this, "Echte Homescreen-Widgets: antippen → automatisch speichern. Kein Öffnen der App, kein Dialog.", 13f))
        section("WIDGETS AUF DEINEM HANDY")
        val manager = AppWidgetManager.getInstance(this)
        val ids = manager.getAppWidgetIds(ComponentName(this, LifeWidgetProvider::class.java))
        if (ids.isEmpty()) {
            content.addView(UiKit.text(this, "Noch kein Widget installiert. Tippe unten auf Hinzufügen oder halte den Homescreen gedrückt und wähle Widgets → Life Bits.", 13f, muted = true))
        } else {
            ids.forEachIndexed { idx, id ->
                val s = WidgetPrefs.load(this, id, db.trackers(true))
                val tile = UiKit.box(this, vertical = false, fill = "#FFF9FC")
                tile.gravity = Gravity.CENTER_VERTICAL
                val desc = UiKit.box(this, pad = 0)
                desc.addView(UiKit.text(this, "Widget ${idx + 1}", 14f, true))
                desc.addView(UiKit.text(this, "${s.selected.size} Tracker · ${if (s.showLabels) "mit Namen" else "diskret"} · ${s.style}", 11f, muted = true))
                tile.addView(desc, LinearLayout.LayoutParams(0, -2, 1f))
                tile.addView(UiKit.button(this, "ANPASSEN", {
                    startActivity(Intent(this, WidgetConfigActivity::class.java)
                        .putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id))
                }))
                content.addView(tile, UiKit.margin(this, bottom = 8))
            }
        }
        content.addView(UiKit.gap(this, 12))
        content.addView(UiKit.button(this, "+ WIDGET ZUM HOMESCREEN", {
            val component = ComponentName(this, LifeWidgetProvider::class.java)
            if (manager.isRequestPinAppWidgetSupported) {
                val accepted = manager.requestPinAppWidget(component, null, null)
                if (!accepted) Toast.makeText(this, "Widget über den Homescreen hinzufügen.", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Homescreen gedrückt halten → Widgets → Life Bits", Toast.LENGTH_LONG).show()
            }
        }))
        content.addView(UiKit.gap(this, 12))
        content.addView(UiKit.text(this, "Widgets sind unabhängig voneinander. Du kannst Tracker-Reihenfolge, Beschriftung, Tageszähler und Rosa/Lila/Creme für jedes Widget separat einstellen. Die Größe passt du auf dem Homescreen an.", 12f, muted = true))
    }

    private fun openAddForDay(day: LocalDate) {
        val ts = db.trackers()
        if (ts.isEmpty()) {
            Toast.makeText(this, "Erstelle zunächst einen Tracker.", Toast.LENGTH_SHORT).show(); return
        }
        AlertDialog.Builder(this).setTitle("Welches Life Bit?")
            .setItems(ts.map { it.name }.toTypedArray()) { _, pos -> log(ts[pos].id, day) }
            .setNegativeButton("Abbrechen", null).show()
    }

    private fun pickDate(current: LocalDate, callback: (LocalDate) -> Unit) {
        DatePickerDialog(this, { _, y, m, d -> callback(LocalDate.of(y, m + 1, d)) },
            current.year, current.monthValue - 1, current.dayOfMonth).show()
    }

    private fun editEntry(entry: LifeEntry, tracker: LifeTracker) {
        var eventDay = LocalDate.parse(entry.eventDate)
        var eventTime = entry.eventTime
        val box = UiKit.box(this, pad = 16)
        val dateLabel = UiKit.text(this, "", 14f, true)
        val timeLabel = UiKit.text(this, "", 14f, true)
        val details = UiKit.text(this, "Automatisch erfasst: ${formatRecorded(entry.recordedAt)}", 12f, muted = true)
        fun refresh() {
            dateLabel.text = "Ereignisdatum: ${eventDay.format(dateFormat)}"
            timeLabel.text = "Tatsächliche Uhrzeit: ${eventTime ?: "unbekannt"}"
        }
        refresh()
        box.addView(UiKit.icon(this, tracker.icon, 56))
        box.addView(UiKit.gap(this, 10))
        box.addView(UiKit.button(this, "DATUM ÄNDERN", {
            pickDate(eventDay) { newDate -> eventDay = newDate; refresh() }
        }, false))
        box.addView(dateLabel)
        box.addView(UiKit.gap(this, 8))
        box.addView(UiKit.button(this, "EREIGNISUHRZEIT EINTRAGEN", {
            val t = eventTime?.split(":")
            TimePickerDialog(this, { _, hh, mm ->
                eventTime = "%02d:%02d".format(Locale.ROOT, hh, mm); refresh()
            }, t?.getOrNull(0)?.toIntOrNull() ?: 12, t?.getOrNull(1)?.toIntOrNull() ?: 0, true).show()
        }, false))
        box.addView(timeLabel)
        box.addView(UiKit.button(this, "UHRZEIT UNBEKANNT LASSEN", {
            eventTime = null; refresh()
        }, false))
        box.addView(UiKit.gap(this, 10))
        box.addView(details)
        val dlg = AlertDialog.Builder(this).setTitle("${tracker.name} bearbeiten")
            .setView(box)
            .setPositiveButton("Speichern") { _, _ ->
                db.updateEvent(entry.id, eventDay.toString(), eventTime)
                LifeWidgetProvider.updateAll(this)
                draw()
            }
            .setNeutralButton("Löschen", null)
            .setNegativeButton("Abbrechen", null)
            .create()
        dlg.setOnShowListener {
            dlg.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener {
                AlertDialog.Builder(this).setMessage("Diesen Eintrag wirklich löschen?")
                    .setPositiveButton("Löschen") { _, _ ->
                        db.deleteEvent(entry.id)
                        LifeWidgetProvider.updateAll(this)
                        dlg.dismiss(); draw()
                    }.setNegativeButton("Abbrechen", null).show()
            }
        }
        dlg.show()
    }

    private fun trackerDialog(existing: LifeTracker?) {
        var icon = existing?.icon ?: "heart"
        val box = UiKit.box(this, pad = 12)
        val input = EditText(this).apply {
            hint = "z. B. Déjà-vu"
            setSingleLine(true)
            setText(existing?.name ?: "")
            setTextColor(UiKit.color(UiKit.INK))
        }
        box.addView(UiKit.text(this, "TRACKER-NAME", 13f, true))
        box.addView(input)
        box.addView(UiKit.gap(this, 10))
        box.addView(UiKit.text(this, "PIXEL-ICON WÄHLEN", 13f, true))
        val icons = GridLayout(this).apply { columnCount = 5 }
        fun highlightIcons() {
            for (i in 0 until icons.childCount) {
                val child = icons.getChildAt(i)
                val key = PixelIcons.available.keys.elementAt(i)
                child.background = UiKit.bg(if (key == icon) "#FFB4D9" else "#FFF6FB",
                    UiKit.BORDER, dp(8).toFloat(), if (key == icon) dp(2) else dp(1))
            }
        }
        PixelIcons.available.keys.forEachIndexed { idx, key ->
            val image = UiKit.icon(this, key, 48)
            val tile = UiKit.box(this, pad = 5)
            tile.gravity = Gravity.CENTER
            tile.addView(image)
            tile.setOnClickListener { icon = key; highlightIcons() }
            icons.addView(tile, GridLayout.LayoutParams(GridLayout.spec(idx / 5), GridLayout.spec(idx % 5)).apply {
                width = dp(60); height = dp(62); setMargins(dp(1),dp(2),dp(1),dp(2))
            })
        }
        highlightIcons()
        box.addView(icons)
        if (existing != null) {
            box.addView(UiKit.gap(this, 10))
            box.addView(UiKit.text(this,
                if (existing.active) "Archivieren blendet den Tracker aus, deine bisherigen Einträge bleiben erhalten."
                else "Der Tracker ist archiviert. Du kannst ihn reaktivieren.",
                12f, muted = true))
        }
        val builder = AlertDialog.Builder(this)
            .setTitle(if (existing == null) "Neuen Tracker erstellen" else "Tracker bearbeiten")
            .setView(ScrollView(this).apply { addView(box) })
            .setPositiveButton("Speichern", null)
            .setNegativeButton("Abbrechen", null)
        if (existing != null) {
            builder.setNeutralButton(if (existing.active) "Archivieren" else "Aktivieren", null)
        }
        val dlg = builder.create()
        dlg.setOnShowListener {
            dlg.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val name = input.text.toString().trim()
                if (name.isBlank()) { input.error = "Bitte einen Namen eingeben"; return@setOnClickListener }
                if (existing == null) db.addTracker(name, icon)
                else db.updateTracker(existing.id, name, icon, existing.active)
                LifeWidgetProvider.updateAll(this)
                dlg.dismiss(); draw()
            }
            if (existing != null) dlg.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener {
                db.updateTracker(existing.id, existing.name, existing.icon, !existing.active)
                LifeWidgetProvider.updateAll(this)
                dlg.dismiss(); draw()
            }
        }
        dlg.show()
    }

    private fun exportBackup() {
        val i = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/json"
            putExtra(Intent.EXTRA_TITLE, "LifeBits_${LocalDate.now()}.json")
        }
        startActivityForResult(i, 301)
    }

    private fun importBackup() {
        val i = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/json"
        }
        startActivityForResult(i, 302)
    }

    @Deprecated("Uses platform activity-result API so no extra dependencies are needed")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_OK) return
        val uri: Uri = data?.data ?: return
        if (requestCode == 301) {
            try {
                contentResolver.openOutputStream(uri)?.use { output ->
                    output.write(db.exportJson().toByteArray(Charsets.UTF_8))
                } ?: error("Datei konnte nicht geöffnet werden")
                Toast.makeText(this, "Backup gespeichert ♡", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(this, "Backup fehlgeschlagen: ${e.message}", Toast.LENGTH_LONG).show()
            }
        } else if (requestCode == 302) {
            AlertDialog.Builder(this)
                .setTitle("Backup wiederherstellen?")
                .setMessage("Achtung: Das ersetzt ALLE aktuellen Tracker und Ereignisse durch die Daten aus der ausgewählten Datei.")
                .setPositiveButton("Wiederherstellen") { _, _ ->
                    try {
                        val raw = contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }
                            ?: error("Datei konnte nicht gelesen werden")
                        require(raw.length < 15_000_000) { "Backup zu groß" }
                        db.importJson(raw)
                        LifeWidgetProvider.updateAll(this)
                        draw()
                        Toast.makeText(this, "Backup wiederhergestellt ♡", Toast.LENGTH_LONG).show()
                    } catch (e: Exception) {
                        Toast.makeText(this, "Import fehlgeschlagen: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }.setNegativeButton("Abbrechen", null).show()
        }
    }
}
