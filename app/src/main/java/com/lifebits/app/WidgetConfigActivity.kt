package com.lifebits.app

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Toast

/** The Android launcher opens this screen when a widget is added or reconfigured. */
class WidgetConfigActivity : Activity() {
    private var widgetId = AppWidgetManager.INVALID_APPWIDGET_ID
    private lateinit var db: LifeDatabase
    private var selected = mutableListOf<Long>()
    private var labels = false
    private var counts = true
    private var title = false
    private var style = "pink"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = UiKit.color(UiKit.BACKGROUND)
        window.navigationBarColor = UiKit.color("#F8D4E7")
        db = LifeDatabase(this)
        widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
        if (widgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            Toast.makeText(this, "Bitte zuerst ein Widget zum Homescreen hinzufügen.", Toast.LENGTH_LONG).show()
            finish(); return
        }
        val saved = WidgetPrefs.load(this, widgetId, db.trackers(true))
        selected = saved.selected.toMutableList()
        labels = saved.showLabels; counts = saved.showCounts
        title = saved.showTitle; style = saved.style
        setResult(RESULT_CANCELED)
        draw()
    }

    private fun draw() {
        val scroll = ScrollView(this).apply { setBackgroundColor(UiKit.color(UiKit.BACKGROUND)) }
        val page = UiKit.box(this, pad = 18)
        scroll.addView(page)
        setContentView(scroll)
        page.addView(UiKit.heading(this, "♡ WIDGET STUDIO ♡"))
        page.addView(UiKit.text(this, "Jedes Widget bekommt seine eigenen Buttons und Einstellungen.", 13f, muted = true))
        page.addView(UiKit.gap(this, 16))
        page.addView(UiKit.text(this, "DEINE TRACKER-BUTTONS", 16f, true, pixel = true))
        page.addView(UiKit.gap(this, 6))

        val trackers = db.trackers().sortedWith(compareBy<LifeTracker> { if (it.id in selected) selected.indexOf(it.id) else Int.MAX_VALUE }.thenBy { it.sortOrder })
        trackers.forEach { tracker ->
            val row = UiKit.box(this, vertical = false, pad = 8, fill = "#FFE4F0")
            row.gravity = Gravity.CENTER_VERTICAL
            row.addView(UiKit.icon(this, tracker.icon, 40))
            val cb = CheckBox(this).apply {
                text = tracker.name
                isChecked = tracker.id in selected
                setTextColor(UiKit.color(UiKit.INK))
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                setOnCheckedChangeListener { _, checked ->
                    if (checked && tracker.id !in selected) selected.add(tracker.id)
                    if (!checked) selected.remove(tracker.id)
                    draw()
                }
            }
            row.addView(cb)
            if (tracker.id in selected) {
                row.addView(UiKit.button(this, "↑", {
                    val pos = selected.indexOf(tracker.id)
                    if (pos > 0) { selected.removeAt(pos); selected.add(pos-1, tracker.id); draw() }
                }, filled = false))
                row.addView(UiKit.button(this, "↓", {
                    val pos = selected.indexOf(tracker.id)
                    if (pos in 0 until selected.lastIndex) { selected.removeAt(pos); selected.add(pos+1, tracker.id); draw() }
                }, filled = false))
            }
            page.addView(row, UiKit.margin(this, bottom = 7))
        }
        page.addView(UiKit.text(this, "Die Widget-Größe passt du später auf dem Android-Homescreen an (lange drücken → Größe ändern). Nicht sichtbare Tracker bleiben ausgewählt.", 12f, muted = true))
        page.addView(UiKit.gap(this, 16))
        page.addView(UiKit.text(this, "DARSTELLUNG", 16f, true, pixel = true))
        fun toggle(label: String, value: Boolean, onChange: (Boolean) -> Unit) {
            page.addView(CheckBox(this).apply {
                text = label; isChecked = value
                setTextColor(UiKit.color(UiKit.INK))
                setOnCheckedChangeListener { _, checked -> onChange(checked) }
            })
        }
        toggle("Tracker-Namen anzeigen", labels) { labels = it }
        toggle("Tageszähler anzeigen", counts) { counts = it }
        toggle("Kleine Überschrift anzeigen", title) { title = it }
        page.addView(UiKit.gap(this, 12))
        page.addView(UiKit.text(this, "FARBE", 15f, true, pixel = true))
        val colors = UiKit.box(this, vertical = false, pad = 0)
        listOf("pink" to "🎀 Rosa", "lilac" to "✦ Lila", "cream" to "♡ Creme").forEach { (key, name) ->
            val btn = UiKit.button(this, if (style == key) "✓ $name" else name, {
                style = key; draw()
            }, filled = style == key)
            colors.addView(btn, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
        }
        page.addView(colors)
        page.addView(UiKit.gap(this, 22))
        page.addView(UiKit.button(this, "♡ WIDGET SPEICHERN ♡", {
            if (selected.isEmpty()) {
                Toast.makeText(this, "Wähle mindestens einen Tracker.", Toast.LENGTH_SHORT).show(); return@button
            }
            WidgetPrefs.save(this, widgetId, WidgetSettings(selected.toList(), labels, counts, style, title))
            LifeWidgetProvider.update(this, AppWidgetManager.getInstance(this), widgetId)
            val result = Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
            setResult(RESULT_OK, result)
            finish()
        }))
        page.addView(UiKit.gap(this, 14))
    }
}
