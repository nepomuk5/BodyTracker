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
    private var lastDeleted: LifeEntry? = null
    private var previewWide = false
    private var showingStats = false
    private var statsYear = LocalDate.now().year
    private var statsMonth: Int? = null
    private var launchAffirmation = Affirmations.random()

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
        if (tab == 0) top.addView(UiKit.icon(this, "mascot", 48))
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
        title("RANDOM LIFE STATS", "Little moments, big statistics ♡")
        val headline = UiKit.box(this, vertical = false, fill = "#FFF7FB")
        headline.gravity = Gravity.CENTER_VERTICAL
        val left = UiKit.box(this, pad = 0)
        left.addView(UiKit.text(this, "HEUTE", 18f, true, pixel = true))
        left.addView(UiKit.text(this, LocalDate.now().format(dateFormat), 12f, muted = true))
        headline.addView(left, LinearLayout.LayoutParams(0, -2, 1f))
        // Single mascot in the header; no duplicate illustration here.
        content.addView(headline)
        content.addView(UiKit.gap(this, 10))
        val pepCard = UiKit.box(this, vertical = false, pad = 10, fill = "#FFE1F0")
        pepCard.gravity = Gravity.CENTER_VERTICAL
        val pepCopy = UiKit.box(this, pad = 0)
        pepCopy.addView(UiKit.text(this, "TINY PEP TALK ♡", 10f, true, muted = true, pixel = true))
        pepCopy.addView(UiKit.text(this, launchAffirmation, 13f, true))
        pepCard.addView(pepCopy, LinearLayout.LayoutParams(0, -2, 1f))
        pepCard.addView(UiKit.button(this, "↻", {
            launchAffirmation = Affirmations.random()
            draw()
        }, filled = false))
        content.addView(pepCard)
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
        section("HEUTIGE EINTRÄGE")
        renderEntryList(LocalDate.now())
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
        lastDeleted?.let { removed ->
            val undo = UiKit.box(this, vertical = false, pad = 8, fill = "#FFE1F0")
            undo.gravity = Gravity.CENTER_VERTICAL
            undo.addView(UiKit.text(this, "Eintrag entfernt", 12f, true),
                LinearLayout.LayoutParams(0, -2, 1f))
            undo.addView(UiKit.button(this, "↶ RÜCKGÄNGIG", {
                try {
                    db.restoreEvent(removed)
                    lastDeleted = null
                    LifeWidgetProvider.updateAll(this)
                    draw()
                } catch (_: Exception) {
                    Toast.makeText(this, "Wiederherstellen fehlgeschlagen", Toast.LENGTH_LONG).show()
                }
            }, filled = false))
            content.addView(undo, UiKit.margin(this, bottom = 8))
        }
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
            val remove = UiKit.button(this, "−", {
                db.deleteEvent(e.id)
                lastDeleted = e
                LifeWidgetProvider.updateAll(this)
                draw()
            }, filled = false)
            remove.contentDescription = "${t.name} Eintrag entfernen"
            remove.setPadding(dp(10), dp(4), dp(10), dp(4))
            row.addView(remove, LinearLayout.LayoutParams(-2, -2).apply { marginStart = dp(10) })
            row.setOnClickListener { editEntry(e, t) }
            content.addView(row, UiKit.margin(this, bottom = 6))
        }
    }

    private fun formatRecorded(millis: Long): String =
        Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).format(timeFormat)

    private fun renderHistory() {
        title("♡ VERLAUF", if (showingStats) "Deine Life Bits in Zahlen" else "Kalender & Einträge bearbeiten")
        content.addView(UiKit.button(this,
            if (showingStats) "♡ ZUM KALENDER" else "▦ MONATS- & JAHRESSTATS", {
                showingStats = !showingStats
                draw()
            }, filled = false))
        content.addView(UiKit.gap(this, 12))
        if (showingStats) {
            renderStats()
            return
        }
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
        if (!chosenDay.isAfter(LocalDate.now())) {
            content.addView(UiKit.button(this, "+ EINTRAG FÜR DIESEN TAG", { openAddForDay(chosenDay) }))
        } else {
            content.addView(UiKit.text(this, "Für zukünftige Tage kannst du noch keine Ereignisse erfassen.", 12f, muted = true))
        }
        content.addView(UiKit.gap(this, 12))
        content.addView(UiKit.button(this, "↗ ZU EINEM DATUM SPRINGEN", {
            pickDate(chosenDay) { day -> chosenDay = day; month = YearMonth.from(day); draw() }
        }, filled = false))
    }

    private fun renderStats() {
        val yearNav = UiKit.box(this, vertical = false, fill = "#FFF9FC", pad = 8)
        yearNav.gravity = Gravity.CENTER_VERTICAL
        yearNav.addView(UiKit.button(this, "‹", {
            statsYear--; statsMonth = null; draw()
        }, false))
        val heading = UiKit.text(this, "$statsYear", 19f, true, pixel = true)
        heading.gravity = Gravity.CENTER
        yearNav.addView(heading, LinearLayout.LayoutParams(0, -2, 1f))
        yearNav.addView(UiKit.button(this, "›", {
            statsYear++; statsMonth = null; draw()
        }, false))
        content.addView(yearNav)
        content.addView(UiKit.gap(this, 10))

        val monthly = db.monthlyCounts(statsYear)
        val months = GridLayout(this).apply { columnCount = 3 }
        for (number in 1..12) {
            val selected = statsMonth == number
            val label = YearMonth.of(statsYear, number).month.getDisplayName(TextStyle.SHORT, locale)
            val tile = UiKit.box(this, fill = if (selected) "#FFC1DF" else "#FFF6FB", pad = 9)
            tile.gravity = Gravity.CENTER
            val name = UiKit.text(this, label, 12f, true)
            name.gravity = Gravity.CENTER
            tile.addView(name)
            val count = UiKit.text(this, "${monthly[number] ?: 0} ♡", 14f, true, pixel = true)
            count.gravity = Gravity.CENTER
            tile.addView(count)
            tile.setOnClickListener {
                statsMonth = if (selected) null else number
                draw()
            }
            months.addView(tile, GridLayout.LayoutParams(
                GridLayout.spec((number - 1) / 3),
                GridLayout.spec((number - 1) % 3, 1f)
            ).apply {
                width = 0
                height = dp(65)
                setMargins(dp(3), dp(3), dp(3), dp(3))
            })
        }
        content.addView(months)
        content.addView(UiKit.gap(this, 8))
        content.addView(UiKit.button(this, "GESAMTES JAHR", {
            statsMonth = null
            draw()
        }, filled = false))

        val currentMonth = statsMonth?.let { YearMonth.of(statsYear, it) }
        val start = currentMonth?.atDay(1) ?: LocalDate.of(statsYear, 1, 1)
        val end = currentMonth?.plusMonths(1)?.atDay(1) ?: LocalDate.of(statsYear + 1, 1, 1)
        val counts = db.countsBetween(start.toString(), end.toString())
        val total = counts.values.sum()
        val days = db.daysWithEventsBetween(start.toString(), end.toString())
        val period = currentMonth?.month?.getDisplayName(TextStyle.FULL, locale)
            ?.let { "$it $statsYear" } ?: "Gesamtjahr $statsYear"

        section(period.uppercase(locale))
        val summary = UiKit.box(this, fill = "#FFE0F0", pad = 14)
        summary.addView(UiKit.text(this, "$total Life Bits", 22f, true, pixel = true))
        summary.addView(UiKit.text(this, "$days Tage mit Einträgen", 12f, muted = true))
        content.addView(summary)

        section("NACH TRACKER")
        val trackers = db.trackers(true)
            .filter { it.active || (counts[it.id] ?: 0) > 0 }
            .sortedWith(compareByDescending<LifeTracker> { counts[it.id] ?: 0 }
                .thenBy { it.sortOrder })
        if (total == 0) {
            content.addView(UiKit.text(this, "Noch keine Life Bits in diesem Zeitraum ♡", 13f, muted = true))
        } else {
            trackers.forEach { t ->
                val row = UiKit.box(this, vertical = false, fill = "#FFF9FC", pad = 8)
                row.gravity = Gravity.CENTER_VERTICAL
                row.addView(UiKit.icon(this, t.icon, 34))
                row.addView(UiKit.text(this, t.name, 13f, true),
                    LinearLayout.LayoutParams(0, -2, 1f).apply { marginStart = dp(10) })
                row.addView(UiKit.text(this, "${counts[t.id] ?: 0}", 16f, true, pixel = true))
                content.addView(row, UiKit.margin(this, bottom = 5))
            }
        }
    }

    private fun renderTrackers() {
        title("♡ TRACKER ♡", "Deine persönliche Sammlung")
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
        content.addView(UiKit.button(this,
            if (previewWide) "2×2-VORSCHAU" else "4×1-VORSCHAU", {
                previewWide = !previewWide
                draw()
            }, filled = false))
        content.addView(UiKit.gap(this, 7))
        val preview = UiKit.box(this, fill = "#FFD4E9", pad = 9)
        val columns = if (previewWide) 4 else 2
        val previewGrid = GridLayout(this).apply { columnCount = columns }
        db.trackers().take(4).forEachIndexed { index, t ->
            val tile = UiKit.box(this, pad = 4, fill = "#FFF0F8")
            tile.gravity = Gravity.CENTER
            tile.addView(UiKit.icon(this, t.icon, if (previewWide) 42 else 48))
            val number = UiKit.text(this, "0", 11f, true, pixel = true)
            number.gravity = Gravity.CENTER
            tile.addView(number)
            previewGrid.addView(tile, GridLayout.LayoutParams(
                GridLayout.spec(index / columns), GridLayout.spec(index % columns, 1f)
            ).apply {
                width = 0; height = dp(if (previewWide) 80 else 90)
                setMargins(dp(2), dp(2), dp(2), dp(2))
            })
        }
        preview.addView(previewGrid)
        content.addView(preview)
        content.addView(UiKit.gap(this, 12))
        content.addView(UiKit.text(this, "Die Vorschau ist schematisch. Die tatsächliche Größe legst du auf dem Android-Homescreen fest.", 12f, muted = true))
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
        content.addView(UiKit.gap(this, 10))
        section("DAILY PEP TALK")
        content.addView(UiKit.text(this, "Ein eigenes Widget mit kleinen positiven Sprüchen – tippe darauf, um zu wechseln.", 12f, muted = true))
        content.addView(UiKit.gap(this, 8))
        content.addView(UiKit.button(this, "+ AFFIRMATIONS-WIDGET", {
            val component = ComponentName(this, AffirmationWidgetProvider::class.java)
            if (!manager.isRequestPinAppWidgetSupported ||
                !manager.requestPinAppWidget(component, null, null)) {
                Toast.makeText(this, "Homescreen gedrückt halten → Widgets → Daily Pep Talk", Toast.LENGTH_LONG).show()
            }
        }))
        content.addView(UiKit.gap(this, 10))
        content.addView(UiKit.text(this, "Tracker-Widgets sind unabhängig. Farben, Beschriftungen und Zähler kannst du einzeln einstellen.", 12f, muted = true))
    }

    private fun openAddForDay(day: LocalDate) {
        if (day.isAfter(LocalDate.now())) {
            Toast.makeText(this, "Für zukünftige Tage ist kein Nachtrag möglich.", Toast.LENGTH_SHORT).show()
            return
        }
        val ts = db.trackers()
        if (ts.isEmpty()) {
            Toast.makeText(this, "Erstelle zunächst einen Tracker.", Toast.LENGTH_SHORT).show(); return
        }
        AlertDialog.Builder(this).setTitle("Welches Life Bit?")
            .setItems(ts.map { it.name }.toTypedArray()) { _, pos -> log(ts[pos].id, day) }
            .setNegativeButton("Abbrechen", null).show()
    }

    private fun pickDate(current: LocalDate, callback: (LocalDate) -> Unit) {
        val picker = DatePickerDialog(this, { _, y, m, d -> callback(LocalDate.of(y, m + 1, d)) },
            current.year, current.monthValue - 1, current.dayOfMonth)
        picker.datePicker.maxDate = System.currentTimeMillis()
        picker.show()
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
        box.addView(UiKit.text(this, "SOFT EMOJI ICON WÄHLEN", 13f, true))
        val icons = GridLayout(this).apply { columnCount = 4 }
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
            icons.addView(tile, GridLayout.LayoutParams(GridLayout.spec(idx / 4), GridLayout.spec(idx % 4)).apply {
                width = dp(62); height = dp(64); setMargins(dp(1),dp(2),dp(1),dp(2))
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
        AlertDialog.Builder(this)
            .setTitle("Backup auswählen")
            .setMessage(
                "Im nächsten Schritt öffnet Android die Dateiauswahl. " +
                "Mit Zurück kannst du jederzeit abbrechen. " +
                "Nur die Auswahl einer Datei ändert noch keine Einträge. ♡"
            )
            .setNegativeButton("Abbrechen", null)
            .setPositiveButton("DATEI AUSWÄHLEN") { _, _ ->
                val i = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "application/json"
                }
                startActivityForResult(i, 302)
            }.show()
    }

    /**
     * Read a document from Android's Storage Access Framework with a strict byte limit.
     * Never call readText() directly on an untrusted content Uri: that could allocate
     * arbitrarily large data before the old length check ran.
     */
    private fun readBackupText(uri: Uri): String {
        val maxBytes = 15_000_000
        val input = contentResolver.openInputStream(uri)
            ?: error("Datei konnte nicht geöffnet werden")
        return input.use { stream ->
            val output = java.io.ByteArrayOutputStream()
            val chunk = ByteArray(8192)
            while (true) {
                val read = stream.read(chunk)
                if (read == -1) break
                if (read == 0) continue
                require(output.size().toLong() + read <= maxBytes.toLong()) {
                    "Backup ist zu groß (max. 15 MB)"
                }
                output.write(chunk, 0, read)
            }
            output.toString(Charsets.UTF_8.name())
        }
    }

    private fun restoreBackupPreview(uri: Uri) {
        try {
            // Parsing and validating a document is read-only.
            val raw = readBackupText(uri)
            val summary = db.inspectBackup(raw)
            val currentTrackerCount = db.trackers(true).size
            val currentEventCount = db.eventCount()
            val dateInfo = if (summary.earliestDate != null) {
                "\\nZeitraum: ${summary.earliestDate} bis ${summary.latestDate}"
            } else ""
            val message =
                "AUSGEWÄHLTE SICHERUNG\\n" +
                "${summary.trackerCount} Tracker · ${summary.eventCount} Ereignisse" +
                dateInfo + "\\n\\n" +
                "AKTUELL AUF DIESEM HANDY\\n" +
                "$currentTrackerCount Tracker · $currentEventCount Ereignisse\\n\\n" +
                "ACHTUNG: Wiederherstellen ERSETZT alle aktuellen Tracker und " +
                "Einträge. Widget-Einstellungen sind nicht Teil der JSON-Datei. " +
                "Bitte nur mit einer zuvor geprüften Sicherung fortfahren."
            AlertDialog.Builder(this)
                .setTitle("Backup geprüft ♡")
                .setMessage(message)
                .setNegativeButton("NICHT ERSETZEN", null)
                .setPositiveButton("ALLES ERSETZEN") { _, _ ->
                    try {
                        // A second validation and an SQL transaction protect against
                        // partially imported files and duplicate references.
                        db.importJson(raw)
                        lastDeleted = null
                        LifeWidgetProvider.updateAll(this)
                        draw()
                        Toast.makeText(this,
                            "${summary.eventCount} Einträge wiederhergestellt ♡",
                            Toast.LENGTH_LONG).show()
                    } catch (e: Exception) {
                        Toast.makeText(this,
                            "Wiederherstellung fehlgeschlagen. Vorherige Daten bleiben erhalten: ${e.message}",
                            Toast.LENGTH_LONG).show()
                    }
                }.show()
        } catch (e: Exception) {
            AlertDialog.Builder(this)
                .setTitle("Backup nicht verwendbar")
                .setMessage("${e.message ?: "Die Datei ist beschädigt oder ungültig."}\\n\\nDeine Daten wurden nicht verändert.")
                .setPositiveButton("OK", null)
                .show()
        }
    }

    @Deprecated("Uses platform activity-result API so no extra dependencies are needed")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode !in listOf(301, 302)) return
        if (resultCode != RESULT_OK || data?.data == null) {
            Toast.makeText(this, "Dateiauswahl abgebrochen – alles unverändert ♡", Toast.LENGTH_SHORT).show()
            return
        }
        val uri: Uri = data.data!!
        if (requestCode == 301) {
            try {
                val raw = db.exportJson()
                val info = db.inspectBackup(raw)
                contentResolver.openOutputStream(uri, "wt")?.use { output ->
                    output.write(raw.toByteArray(Charsets.UTF_8))
                    output.flush()
                } ?: error("Datei konnte nicht geöffnet werden")
                Toast.makeText(this,
                    "Backup gespeichert: ${info.trackerCount} Tracker, ${info.eventCount} Einträge ♡",
                    Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                AlertDialog.Builder(this)
                    .setTitle("Backup nicht erfolgreich")
                    .setMessage("Bitte prüfe die gewählte Datei und versuche es erneut. ${e.message}")
                    .setPositiveButton("OK", null).show()
            }
        } else if (requestCode == 302) {
            restoreBackupPreview(uri)
        }
    }
}
