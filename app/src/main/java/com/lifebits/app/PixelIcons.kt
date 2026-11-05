package com.lifebits.app

object PixelIcons {
    val available = linkedMapOf(
        "sneeze" to R.drawable.cutie_sneeze,
        "poop" to R.drawable.cutie_poop,
        "migraine" to R.drawable.cutie_migraine,
        "vomit" to R.drawable.cutie_vomit,
        "hiccup" to R.drawable.cutie_hiccup,
        "throat" to R.drawable.cutie_throat,
        "alcohol" to R.drawable.cutie_alcohol,
        "yawn" to R.drawable.cutie_yawn,
        "cloud" to R.drawable.cutie_cloud,
        "tear" to R.drawable.cutie_tear,
        "heart" to R.drawable.cutie_heart,
        "brain" to R.drawable.cutie_brain,
        "moon" to R.drawable.cutie_moon,
        "music" to R.drawable.cutie_music,
        "star" to R.drawable.cutie_star,
        "book" to R.drawable.cutie_book,
        "coffee" to R.drawable.cutie_coffee,
        "clover" to R.drawable.cutie_clover,
        "pill" to R.drawable.cutie_pill,
        "cat" to R.drawable.cutie_cat
    )
    // Branding asset stays addressable but is not an option in the custom-tracker picker.
    fun resource(key: String): Int = when (key) {
        "mascot" -> R.drawable.soft_mascot
        else -> available[key] ?: R.drawable.cutie_heart
    }
}
