package hu.doboadam.howtube.model

data class YoutubeVideoResponse(val kind: String, val etag: String,
                                val pageInfo: PageInfo, val items: List<YoutubeVideo>)

data class PageInfo(val totalResults: Int, val resultsPerPage: Int)

data class YoutubeVideo(val kind: String = "", val etag: String = "",
                        val id: String = "", val snippet: YoutubeVideoSnippet = YoutubeVideoSnippet(),
                        val contentDetails: YoutubeVideoContentDetails = YoutubeVideoContentDetails(), val comments: List<Comment> = emptyList(),
                        var categoryId: Int = 0)

data class YoutubeVideoSnippet(val publishedAt: String = "", val channelId: String = "",
                               val title: String = "", val description: String = "",
                               val thumbnails: YoutubeThumbnails = YoutubeThumbnails(), val channelTitle: String = "",
                               val tags: List<String> = emptyList(), val categoryId: String = "",
                               val liveBroadcastContent: String = "", val defaultLanguage: String = "",
                               val localized: YoutubeLocalizedSnippet = YoutubeLocalizedSnippet(), val defaultAudioLanguage: String = "")

data class YoutubeLocalizedSnippet(val title: String = "", val description: String = "")

data class YoutubeThumbnails(val default: YoutubeThumbnail = YoutubeThumbnail(), val medium: YoutubeThumbnail = YoutubeThumbnail(),
                             val high: YoutubeThumbnail = YoutubeThumbnail(), val standard: YoutubeThumbnail = YoutubeThumbnail(),
                             val maxres: YoutubeThumbnail = YoutubeThumbnail())

data class YoutubeThumbnail(val url: String = "", val width: Int = 0,
                            val height: Int = 0)

data class YoutubeVideoContentDetails(val duration: String = "", val dimension: String = "",
                                      val definition: String = "", val caption: String = "",
                                      val licensedContent: Boolean = false, val projection: String = "")

data class Comment(val author: String? = "", val message: String = " ", val timeStamp : Long = 0)

data class Rating(val author: String = "", val rating: Float = 0f)

data class Category(val id: Int = 0, val name: String = "")
