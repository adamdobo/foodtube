package hu.doboadam.howtube.model

import com.google.firebase.Timestamp
import java.util.*


data class YoutubeVideo(val id: String = "", val snippet: YoutubeVideoSnippet = YoutubeVideoSnippet(),
                        val contentDetails: YoutubeVideoContentDetails = YoutubeVideoContentDetails(), val comments: List<Comment> = emptyList(), var uploadDate: Timestamp = Timestamp(Date()),
                        var ratings: List<Rating> = emptyList(),
                        var categoryId: Int = 0)

data class YoutubeVideoOnlySnippet(val kind: String = "",
                                   val id: YoutubeVideoId = YoutubeVideoId(), val snippet: YoutubeVideoSnippet = YoutubeVideoSnippet())

data class YoutubeVideoId(val kind: String = "", val videoId: String = "")

data class YoutubeVideoSnippet(val title: String = "",
                               val thumbnails: YoutubeThumbnails = YoutubeThumbnails())

data class YoutubeThumbnails(val default: YoutubeThumbnail = YoutubeThumbnail(), val medium: YoutubeThumbnail = YoutubeThumbnail(),
                             val high: YoutubeThumbnail = YoutubeThumbnail(), val standard: YoutubeThumbnail = YoutubeThumbnail(),
                             val maxres: YoutubeThumbnail = YoutubeThumbnail())

data class YoutubeThumbnail(val url: String = "", val width: Int = 0,
                            val height: Int = 0)

data class YoutubeVideoContentDetails(val duration: String = "")

data class Comment(val author: String? = "", val message: String = " ", val timeStamp: Long = 0)

data class Rating(val author: String = "", val rating: Float = 0f)

data class Category(val id: Int = 0, val name: String = "")

data class Contest(val categoryId: Int = 0, val description: String = "", val endDate: Date = Date(), val startDate: Date = Date())

sealed class Result {
    object Success : Result()
    object Failure : Result()
}