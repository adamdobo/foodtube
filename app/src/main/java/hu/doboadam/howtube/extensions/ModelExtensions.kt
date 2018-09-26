package hu.doboadam.howtube.extensions

import hu.doboadam.howtube.model.YoutubeVideoResponse

fun YoutubeVideoResponse.convertToYoutubeVideo() = items.first()