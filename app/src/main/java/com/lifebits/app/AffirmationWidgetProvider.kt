package com.lifebits.app

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.RemoteViews
import java.time.LocalDate

/** An independent, offline affirmation widget. Tap the card to shuffle. */
class AffirmationWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, manager: AppWidgetManager, widgetIds: IntArray) {
        widgetIds.forEach { update(context, manager, it, false) }
    }

    override fun onAppWidgetOptionsChanged(
        context: Context, manager: AppWidgetManager, widgetId: Int, options: Bundle
    ) {
        super.onAppWidgetOptionsChanged(context, manager, widgetId, options)
        update(context, manager, widgetId, false)
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ACTION_NEXT) {
            val id = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
            val manager = AppWidgetManager.getInstance(context)
            if (id != AppWidgetManager.INVALID_APPWIDGET_ID) {
                update(context, manager, id, true)
            }
            return
        }
        super.onReceive(context, intent)
    }

    override fun onDeleted(context: Context, widgetIds: IntArray) {
        val prefs = context.getSharedPreferences("affirmation_widgets", Context.MODE_PRIVATE).edit()
        widgetIds.forEach {
            prefs.remove("index_$it")
            prefs.remove("day_$it")
        }
        prefs.apply()
    }

    companion object {
        const val ACTION_NEXT = "com.lifebits.app.NEXT_AFFIRMATION"

        private fun update(context: Context, manager: AppWidgetManager, id: Int, next: Boolean) {
            val prefs = context.getSharedPreferences("affirmation_widgets", Context.MODE_PRIVATE)
            val today = LocalDate.now().toString()
            val prevDay = prefs.getString("day_$id", null)
            val old = prefs.getInt("index_$id", Affirmations.dailyIndex(id))
            val index = when {
                prevDay != today -> Affirmations.dailyIndex(id)
                next -> (old + 1) % Affirmations.messages.size
                else -> old
            }
            prefs.edit().putString("day_$id", today).putInt("index_$id", index).apply()

            val views = RemoteViews(context.packageName, R.layout.widget_affirmation)
            views.setTextViewText(R.id.affirmation_text, Affirmations.messages[index])
            val intent = Intent(context, AffirmationWidgetProvider::class.java).apply {
                action = ACTION_NEXT
                data = Uri.parse("lifebits://affirmation/$id")
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id)
            }
            val pi = PendingIntent.getBroadcast(
                context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.affirmation_card, pi)
            manager.updateAppWidget(id, views)
        }
    }
}
