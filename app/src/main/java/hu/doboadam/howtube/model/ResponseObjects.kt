package hu.doboadam.howtube.model

data class YoutubeVideoResponse(val items: List<YoutubeVideo>)

data class YoutubeVideoListResponse(val items: List<YoutubeVideoOnlySnippet>)
