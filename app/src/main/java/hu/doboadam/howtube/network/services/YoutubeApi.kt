package hu.doboadam.howtube.network.services

import hu.doboadam.howtube.model.YoutubeVideoResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface YoutubeApi {

    companion object {
        private const val API_KEY = "AIzaSyBk6clBc6NZKnhv1lF_VAJaU3QvA4AEFI0"
        private const val SNIPPET = "snippet"
    }

    @GET("youtube/v3/videos")
    fun getVideoById(@Query("id") id: String,
                     @Query("part") part: String = SNIPPET,
                     @Query("key") key: String = API_KEY): Call<YoutubeVideoResponse>
}