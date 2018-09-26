package hu.doboadam.howtube.ui.content

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import hu.doboadam.howtube.extensions.convertToYoutubeVideo
import hu.doboadam.howtube.model.YoutubeVideo
import hu.doboadam.howtube.network.RetrofitInstance
import hu.doboadam.howtube.network.services.YoutubeApi
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class ContentViewModel : ViewModel() {

    private val youtubeVideoLiveData: MutableLiveData<YoutubeVideo> = MutableLiveData()
    private val db = FirebaseFirestore.getInstance()

    companion object {
        val api: YoutubeApi = RetrofitInstance.getYoutubeRetrofitInstance().create(YoutubeApi::class.java)
        private const val VIDEOS = "videos"
    }

    fun getYoutubeLiveData(): LiveData<YoutubeVideo> = youtubeVideoLiveData

    fun checkAndUploadVideo(id: String) {
        doAsync {
            val response = api.getVideoById(id).execute()
            uiThread {
                if(response.isSuccessful){
                    db.collection(VIDEOS)
                            .add(response.body()!!.convertToYoutubeVideo())
                    youtubeVideoLiveData.postValue(response.body()!!.convertToYoutubeVideo())
                }
            }
        }
    }

}