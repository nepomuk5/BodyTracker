package com.lifebits.app

/** Offline, non-medical, light-hearted affirmations. No tracking or telemetry. */
object Affirmations {
    val messages = listOf(
        "stay hydrated, icon 💧",
        "tiny wins are still wins ♡",
        "you are the moment ✨",
        "main character energy, cozy edition 🎀",
        "soft life, strong boundaries ♡",
        "one little win at a time 🌷",
        "today looks cute on you ♡",
        "resting is productive, actually ☁️",
        "you ate (metaphorically) 💅",
        "you deserve a cozy little break ♡",
        "progress, but make it gentle ✨",
        "no rush, you're doing lovely 🌸"
    )
    fun random(): String = messages.random()
    fun dailyIndex(widgetId: Int): Int =
        Math.floorMod(java.time.LocalDate.now().toEpochDay().toInt() + widgetId, messages.size)
}
