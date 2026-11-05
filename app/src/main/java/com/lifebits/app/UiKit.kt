package com.lifebits.app

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

object UiKit {
    const val BACKGROUND = "#FFF0F8"
    const val CARD = "#FFE0EE"
    const val TILE = "#FFD1E7"
    const val INK = "#61234E"
    const val MUTED = "#9B6084"
    const val BERRY = "#BD2E81"
    const val BORDER = "#D95099"

    fun dp(c: Context, value: Int): Int = (c.resources.displayMetrics.density * value + .5f).toInt()
    fun color(s: String): Int = Color.parseColor(s)
    fun bg(fill: String = CARD, border: String = BORDER, radius: Float = 16f, stroke: Int = 1): GradientDrawable =
        GradientDrawable().apply {
            setColor(color(fill)); setCornerRadius(radius)
            setStroke(stroke, color(border))
        }
    fun box(c: Context, vertical: Boolean = true, pad: Int = 12, fill: String? = null): LinearLayout =
        LinearLayout(c).apply {
            orientation = if (vertical) LinearLayout.VERTICAL else LinearLayout.HORIZONTAL
            setPadding(dp(c, pad), dp(c, pad), dp(c, pad), dp(c, pad))
            if (fill != null) background = bg(fill, BORDER, dp(c, 16).toFloat(), dp(c, 1))
        }
    fun text(c: Context, value: String, size: Float = 14f, bold: Boolean = false, muted: Boolean = false, pixel: Boolean = false): TextView =
        TextView(c).apply {
            this.text = value; textSize = size
            setTextColor(color(if (muted) MUTED else INK))
            if (bold || pixel) setTypeface(if (pixel) Typeface.MONOSPACE else Typeface.DEFAULT, Typeface.BOLD)
            includeFontPadding = true
        }
    fun heading(c: Context, value: String): TextView = text(c, value, 21f, bold = true, pixel = true)
    fun button(c: Context, label: String, action: () -> Unit, filled: Boolean = true): TextView =
        text(c, label, 13f, true, pixel = filled).apply {
            gravity = Gravity.CENTER
            setPadding(dp(c, 14), dp(c, 11), dp(c, 14), dp(c, 11))
            background = bg(if (filled) "#F9A5CF" else "#FFF5FA", BORDER, dp(c, 13).toFloat(), dp(c, 2))
            isClickable = true
            setOnClickListener { action() }
        }
    fun icon(c: Context, key: String, size: Int = 46): ImageView =
        ImageView(c).apply {
            setImageResource(PixelIcons.resource(key)); scaleType = ImageView.ScaleType.FIT_CENTER
            layoutParams = LinearLayout.LayoutParams(dp(c, size), dp(c, size))
        }
    fun gap(c: Context, px: Int): View = View(c).apply { layoutParams = LinearLayout.LayoutParams(1, dp(c, px)) }
    fun margin(c: Context, top: Int = 0, bottom: Int = 0, left: Int = 0, right: Int = 0): LinearLayout.LayoutParams =
        LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            setMargins(dp(c, left), dp(c, top), dp(c, right), dp(c, bottom))
        }
}
