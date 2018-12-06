package hu.doboadam.szakdoga.ui.content.videolist

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import hu.doboadam.szakdoga.model.FirestoreRepository
import hu.doboadam.szakdoga.model.Rating
import hu.doboadam.szakdoga.model.YoutubeVideo
import hu.doboadam.szakdoga.ui.BaseViewModel
import timber.log.Timber
import java.util.Collections.emptyList

class VideoListViewModel(private val categoryId: Int) : BaseViewModel() {

    private val youtubeVideoLiveData: MutableLiveData<List<YoutubeVideo>> = MutableLiveData()

    companion object {
        private const val VIDEOS = "videos"
    }

    fun getYoutubeLiveData(): LiveData<List<YoutubeVideo>> = youtubeVideoLiveData

    override fun startListeningToDbChanges() {
        super.startListeningToDbChanges()
        listener = FirestoreRepository.getCollectionReference(VIDEOS).whereEqualTo("categoryId", categoryId)
                .addSnapshotListener { snapshots: QuerySnapshot?, exception: FirebaseFirestoreException? ->
            if (exception != null) {
                Timber.e("Listening failed with $exception")
                return@addSnapshotListener
            }
            val videos = emptyList<YoutubeVideo>().toMutableList()
            for (docs in snapshots!!) {
                val video = docs.toObject(YoutubeVideo::class.java)
                val ratings = FirestoreRepository.getCollection("videos/${video.id}/ratings")
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