package hu.doboadam.howtube.ui.content.videolist

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import hu.doboadam.howtube.extensions.convertToYoutubeVideo
import hu.doboadam.howtube.extensions.isYoutubeVideo
import hu.doboadam.howtube.model.Rating
import hu.doboadam.howtube.model.YoutubeVideo
import hu.doboadam.howtube.network.RetrofitInstance
import hu.doboadam.howtube.network.services.YoutubeApi
import hu.doboadam.howtube.ui.BaseViewModel
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import timber.log.Timber

class VideoListViewModel(private val categoryId: Int) : BaseViewModel() {

    private val youtubeVideoLiveData: MutableLiveData<List<YoutubeVideo>> = MutableLiveData()

    companion object {
        private const val VIDEOS = "videos"
    }

    fun getYoutubeLiveData(): LiveData<List<YoutubeVideo>> = youtubeVideoLiveData

    override fun startListeningToDbChanges() {
        listener = db.collection(VIDEOS).whereEqualTo("categoryId", categoryId).addSnapshotListener { snapshots: QuerySnapshot?, exception: FirebaseFirestoreException? ->
            if (exception != null) {
                Timber.e("Listening failed with $exception")
                return@addSnapshotListener
            }
            val videos = emptyList<YoutubeVideo>().toMutableList()
            for (docs in snapshots!!) {
                val video = docs.toObject(YoutubeVideo::class.java)
                val ratings = db.collection("videos/${video.id}/ratings").get()
                ratings.addOnSuccessListener {
                    val ratingList = it.toObjects(Rating::class.java)
                    video.ratings = ratingList
                    videos.add(video)
                    youtubeVideoLiveData.postValue(videos)
                }
            }
        }
    }

}