package hu.doboadam.howtube.ui.content

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import hu.doboadam.howtube.extensions.convertToYoutubeVideo
import hu.doboadam.howtube.extensions.isYoutubeVideo
import hu.doboadam.howtube.model.YoutubeVideo
import hu.doboadam.howtube.network.RetrofitInstance
import hu.doboadam.howtube.network.services.YoutubeApi
import hu.doboadam.howtube.ui.BaseViewModel
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import timber.log.Timber

class ContentViewModel : BaseViewModel() {

    private val youtubeVideoLiveData: MutableLiveData<List<YoutubeVideo>> = MutableLiveData()

    companion object {
        val api: YoutubeApi = RetrofitInstance.getYoutubeRetrofitInstance().create(YoutubeApi::class.java)
        private const val VIDEOS = "videos"
        private val REGEX = "((?<=([vV])/)|(?<=be/)|(?<=([?&])v=)|(?<=embed/))([\\w-]+).".toRegex()

    }

    fun getYoutubeLiveData(): LiveData<List<YoutubeVideo>> = youtubeVideoLiveData

    fun checkAndUploadVideo(url: String) {
        if (url.isYoutubeVideo()) {
            doAsync {
                val id = REGEX.find(url)
                val response = api.getVideoById(id?.value).execute()
                uiThread {
                    if (response.isSuccessful) {
                        if (response.body()!!.items.any()) {
                            val video = response.body()!!.convertToYoutubeVideo()
                            db.collection(VIDEOS).document(video.id)
                                    .set(video)
                        } else {
                            //TODO: display error snackbar
                        }
                    }
                }
            }
        } else {
            //TODO: display error
        }
    }

    override fun startListeningToDbChanges() {
        listener = db.collection(VIDEOS).addSnapshotListener { snapshots: QuerySnapshot?, exception: FirebaseFirestoreException? ->
            if (exception != null) {
                Timber.e("Listening failed with $exception")
                return@addSnapshotListener
            }
            val videos = emptyList<YoutubeVideo>().toMutableList()
            for (docs in snapshots!!) {
                videos.add(docs.toObject(YoutubeVideo::class.java))
            }
            youtubeVideoLiveData.postValue(videos)
        }
    }

}