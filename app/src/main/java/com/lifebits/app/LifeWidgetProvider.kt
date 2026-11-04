package com.lifebits.app

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.RemoteViews
import java.time.LocalDate

/** Per-instance widget settings (not shared between differently configured widgets). */
data class WidgetSettings(
    val selected: List<Long>,
    val showLabels: Boolean = false,
    val showCounts: Boolean = true,
    val style: String = "pink",
    val showTitle: Boolean = false
)

object WidgetPrefs {
    private fun prefs(c: Context) = c.getSharedPreferences("life_widgets", Context.MODE_PRIVATE)
    private fun key(id: Int) = "w_$id"

    fun isSaved(c: Context, id: Int): Boolean = prefs(c).contains(key(id))
    fun load(c: Context, id: Int, trackers: List<LifeTracker>): WidgetSettings {
        val raw = prefs(c).getString(key(id), null)
        if (raw.isNullOrBlank()) return WidgetSettings(trackers.filter { it.active }.take(4).map { it.id })
        return try {
            val j = org.json.JSONObject(raw)
            val arr = j.getJSONArray("ids")
            WidgetSettings(
                (0 until arr.length()).map { arr.getLong(it) },
                j.optBoolean("labels", false), j.optBoolean("counts", true),
                j.optString("style", "pink"), j.optBoolean("title", false)
            )
        } catch (_: Exception) { WidgetSettings(trackers.filter { it.active }.take(4).map { it.id }) }
    }
    fun save(c: Context, id: Int, settings: WidgetSettings) {
        val j = org.json.JSONObject()
            .put("ids", org.json.JSONArray(settings.selected))
            .put("labels", settings.showLabels)
            .put("counts", settings.showCounts)
            .put("style", settings.style)
            .put("title", settings.showTitle)
        prefs(c).edit().putString(key(id), j.toString()).apply()
    }
    fun remove(c: Context, id: Int) = prefs(c).edit().remove(key(id)).apply()
}

class LifeWidgetProvider : AppWidgetProvider() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ACTION_LOG) {
            val id = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
            val trackerId = intent.getLongExtra(EXTRA_TRACKER, -1)
            // Never log unless the requested tracker belongs to this widget.
            val manager = AppWidgetManager.getInstance(context)
            if (id == AppWidgetManager.INVALID_APPWIDGET_ID ||
                !manager.getAppWidgetIds(ComponentName(context, LifeWidgetProvider::class.java)).contains(id)
            ) return
            val pending = goAsync()
            Thread {
                try {
                    val db = LifeDatabase(context)
                    val all = db.trackers(true)
                    if (all.any { it.id == trackerId && it.active } &&
                        trackerId in WidgetPrefs.load(context, id, all).selected
                    ) {
                        db.addEvent(trackerId) // eventTime deliberately stays NULL
                        updateAll(context)
                    }
                } catch (_: Exception) {
                    // Do not crash the launcher. The local database remains unchanged on failed insert.
                } finally { pending.finish() }
            }.start()
            return
        }
        super.onReceive(context, intent)
    }

    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) {
        ids.forEach { update(context, manager, it) }
    }
    override fun onAppWidgetOptionsChanged(context: Context, manager: AppWidgetManager, id: Int, options: Bundle) {
        super.onAppWidgetOptionsChanged(context, manager, id, options)
        update(context, manager, id)
    }
    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        appWidgetIds.forEach { WidgetPrefs.remove(context, it) }
    }

    companion object {
        const val ACTION_LOG = "com.lifebits.app.LOG_EVENT"
        const val EXTRA_TRACKER = "tracker_id"

        fun updateAll(context: Context) {
            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(ComponentName(context, LifeWidgetProvider::class.java))
            ids.forEach { update(context, manager, it) }
        }

        fun update(context: Context, manager: AppWidgetManager, widgetId: Int) {
            val db = LifeDatabase(context)
            val all = db.trackers(true)
            val cfg = WidgetPrefs.load(context, widgetId, all)
            val options = manager.getAppWidgetOptions(widgetId)
            val width = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, 110)
            val height = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, 110)
            // The wide 4x1 widget is a compact toolbar, NOT a stretched 2x2 grid.
            // Android reports widget sizes in approximate dp through options.
            val wide = width >= 215 && height < 190
            val cols = if (wide || width >= 300) 4 else 2
            val rows = if (wide) 1 else if (height >= 215) 4 else 2
            val choices = cfg.selected.mapNotNull { sid -> all.find { it.id == sid && it.active } }
                .take(cols * rows)
            val counts = db.countForDay(LocalDate.now().toString())
            val style = cfg.style.takeIf { it in listOf("pink", "lilac", "cream") } ?: "pink"
            val rootLayout = when (style) {
                "lilac" -> R.layout.widget_root_lilac
                "cream" -> R.layout.widget_root_cream
                else -> R.layout.widget_root
            }
            val tileLayout = when {
                wide && style == "lilac" -> R.layout.widget_tile_wide_lilac
                wide && style == "cream" -> R.layout.widget_tile_wide_cream
                wide -> R.layout.widget_tile_wide
                style == "lilac" -> R.layout.widget_tile_lilac
                style == "cream" -> R.layout.widget_tile_cream
                else -> R.layout.widget_tile
            }
            val remote = RemoteViews(context.packageName, rootLayout)
            remote.setViewVisibility(R.id.widget_title, if (cfg.showTitle) View.VISIBLE else View.GONE)
            remote.removeAllViews(R.id.widget_grid)
            if (choices.isEmpty()) {
                // No configured active trackers; tapping the placeholder opens the app.
                val row = RemoteViews(context.packageName, R.layout.widget_row)
                val tile = RemoteViews(context.packageName, tileLayout)
                tile.setImageViewResource(R.id.tile_icon, R.drawable.pixel_cat)
                tile.setTextViewText(R.id.tile_label, "+ Tracker")
                tile.setViewVisibility(R.id.tile_label, View.VISIBLE)
                tile.setViewVisibility(R.id.tile_count, View.GONE)
                val intent = Intent(context, MainActivity::class.java)
                val pi = PendingIntent.getActivity(context, widgetId, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
                tile.setOnClickPendingIntent(R.id.tile_root, pi)
                row.addView(R.id.widget_row_items, tile)
                remote.addView(R.id.widget_grid, row)
            } else {
                choices.chunked(cols).forEach { group ->
                    val row = RemoteViews(context.packageName, R.layout.widget_row)
                    group.forEach { tracker ->
                        val tile = RemoteViews(context.packageName, tileLayout)
                        tile.setImageViewResource(R.id.tile_icon, PixelIcons.resource(tracker.icon))
                        tile.setTextViewText(R.id.tile_count, "${counts[tracker.id] ?: 0}")
                        tile.setTextViewText(R.id.tile_label, tracker.name)
                        tile.setViewVisibility(R.id.tile_count, if (cfg.showCounts) View.VISIBLE else View.GONE)
                        tile.setViewVisibility(R.id.tile_label, if (cfg.showLabels) View.VISIBLE else View.GONE)
                        val intent = Intent(context, LifeWidgetProvider::class.java).apply {
                            action = ACTION_LOG
                            data = Uri.parse("lifebits://tap/$widgetId/${tracker.id}")
                            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
                            putExtra(EXTRA_TRACKER, tracker.id)
                        }
                        val pi = PendingIntent.getBroadcast(context, 0, intent,
                            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
                        tile.setOnClickPendingIntent(R.id.tile_root, pi)
                        row.addView(R.id.widget_row_items, tile)
                    }
                    remote.addView(R.id.widget_grid, row)
                }
            }
            manager.updateAppWidget(widgetId, remote)
        }
    }
}
