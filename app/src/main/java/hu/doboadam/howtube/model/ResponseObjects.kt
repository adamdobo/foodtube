package hu.doboadam.howtube.model

data class YoutubeVideoResponse(val kind: String, val etag: String,
                                val pageInfo: PageInfo, val items: List<YoutubeVideo>)

data class PageInfo(val totalResults: Int, val resultsPerPage: Int)

data class YoutubeVideo(val kind: String, val etag: String,
                        val id: String, val snippet: YoutubeVideoSnippet)

data class YoutubeVideoSnippet(val publishedAt: String, val channelId: String,
                               val title: String, val description: String,
                               val thumbnails: YoutubeThumbnails, val channelTitle: String,
                               val tags: List<String>, val categoryId: String,
                               val liveBroadcastContent: String, val defaultLanguage: String,
                               val localized: YoutubeLocalizedSnippet, val defaultAudioLanguage: String)

data class YoutubeLocalizedSnippet(val title: String, val description: String)

data class YoutubeThumbnails(val default: YoutubeThumbnail, val medium: YoutubeThumbnail,
                             val high: YoutubeThumbnail, val standard: YoutubeThumbnail,
                             val maxres: YoutubeThumbnail)

data class YoutubeThumbnail(val url: String, val width: Int,
                            val height: Int)