package hu.doboadam.howtube.model

data class YoutubeVideoResponse(val kind: String, val etag: String,
                                val pageInfo: PageInfo, val items: List<YoutubeVideo>)

data class YoutubeVideoListResponse(val kind: String, val etag: String,
                                    val nextPageToken: String, val regionCode: String,
                                    val pageInfo: PageInfo,
                                    val items: List<YoutubeVideoOnlySnippet>)
