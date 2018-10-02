package hu.doboadam.howtube.extensions

import hu.doboadam.howtube.model.YoutubeThumbnails
import hu.doboadam.howtube.model.YoutubeVideoResponse

fun YoutubeVideoResponse.convertToYoutubeVideo() = items.first()


fun YoutubeThumbnails.getMostFittingThumbnailUrl(): String {
    return when {
        maxres.url.isNotBlank() -> maxres.url
        standard.url.isNotBlank() -> standard.url
        high.url.isNotBlank() -> high.url
        medium.url.isNotBlank() -> medium.url
        default.url.isNotBlank() -> default.url
        else -> ""
    }
}