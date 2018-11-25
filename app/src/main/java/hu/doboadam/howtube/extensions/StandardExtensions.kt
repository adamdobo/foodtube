package hu.doboadam.howtube.extensions

import java.time.Duration
import java.time.LocalTime

fun String.isYoutubeVideo() =
        matches("^((?:https?:)?//)?((?:www|m)\\.)?((?:youtube\\.com|youtu.be))(/(?:[\\w\\-]+\\?v=|embed/|v/)?)([\\w\\-]+)(\\S+)?$".toRegex())

fun String.parseYoutubeDuration(): String {
    val seconds = Duration.parse(this).seconds
    return when {
        seconds >= 3600 -> {
            val timeOfDay = LocalTime.ofSecondOfDay(seconds)
            timeOfDay.toString()
        }
        else -> {
            String.format("%02d:%02d", seconds / 60, seconds % 60)
        }
    }

}
