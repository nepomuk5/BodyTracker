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
        "yawn" to R.drawable.pixel_yawn,
        "cloud" to R.drawable.pixel_cloud,
        "tear" to R.drawable.pixel_tear,
        "heart" to R.drawable.pixel_heart,
        "brain" to R.drawable.pixel_brain,
        "moon" to R.drawable.pixel_moon,
        "music" to R.drawable.pixel_music,
        "star" to R.drawable.pixel_star,
        "book" to R.drawable.pixel_book,
        "coffee" to R.drawable.pixel_coffee,
        "clover" to R.drawable.pixel_clover,
        "pill" to R.drawable.pixel_pill,
        "cat" to R.drawable.pixel_cat,
        "mascot" to R.drawable.soft_mascot
    )
    fun resource(key: String): Int = available[key] ?: R.drawable.pixel_heart
}
