package hu.doboadam.howtube.ui.content.browseyoutubevideos

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import hu.doboadam.howtube.model.YoutubeVideoListResponse
import hu.doboadam.howtube.network.RetrofitInstance
import hu.doboadam.howtube.network.api.YoutubeApi
import hu.doboadam.howtube.ui.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class BrowseYoutubeVideosViewModel : BaseViewModel() {

    private val _videoList: MutableLiveData<YoutubeVideoListResponse> = MutableLiveData()

    val videoList: LiveData<YoutubeVideoListResponse>
        get() = _videoList

    companion object {
        private val api: YoutubeApi = RetrofitInstance.getYoutubeRetrofitInstance().create(YoutubeApi::class.java)
    }

    fun getYoutubeVideos() {
        GlobalScope.launch(Dispatchers.IO) {
            val retrofitRequest = api.getRecipeVideos("snippet", 20, "tasty recipes")
            val response = retrofitRequest.await()
            if(response.isSuccessful){
                _videoList.postValue(response.body())
            }
        }
    }
}