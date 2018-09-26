package hu.doboadam.howtube.network

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class RetrofitInstance {

    companion object {
        private const val YOUTUBE_BASE_URL = "https://www.googleapis.com/"
        fun getYoutubeRetrofitInstance(): Retrofit =
                retrofit2.Retrofit.Builder()
                        .baseUrl(YOUTUBE_BASE_URL)
                        .addConverterFactory(MoshiConverterFactory.create())
                        .build()
    }
}