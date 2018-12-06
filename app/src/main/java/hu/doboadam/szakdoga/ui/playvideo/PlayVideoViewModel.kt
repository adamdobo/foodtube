package hu.doboadam.szakdoga.ui.playvideo

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ListenerRegistration
import hu.doboadam.szakdoga.model.Comment
import hu.doboadam.szakdoga.model.FirestoreRepository
import hu.doboadam.szakdoga.model.Rating
import hu.doboadam.szakdoga.model.YoutubeVideo
import hu.doboadam.szakdoga.ui.BaseViewModel
import timber.log.Timber

class PlayVideoViewModel(private val videoId: String) : BaseViewModel() {

    private val youtubeVideoLiveData: MutableLiveData<YoutubeVideo> = MutableLiveData()
    private val ratingsLiveData: MutableLiveData<List<Rating>> = MutableLiveData()
    private val VIDEO_PATH = "videos"
    private val COMMENT_PATH = "comments"
    private val RATING_PATH = "ratings"

    private lateinit var ratingListener: ListenerRegistration

    val getYoutubeVideoLiveData: LiveData<YoutubeVideo> = youtubeVideoLiveData

    val getRatingsLiveData: LiveData<List<Rating>> = ratingsLiveData


    override fun startListeningToDbChanges() {
        listener = FirestoreRepository.listenToDocumentChanges("$VIDEO_PATH/$videoId") { documentSnapshot, firebaseFirestoreException ->
            if (firebaseFirestoreException != null) {
                Timber.e("Listen failed with $firebaseFirestoreException")
            }
            if (documentSnapshot != null && documentSnapshot.exists()) {
                youtubeVideoLiveData.postValue(documentSnapshot.toObject(YoutubeVideo::class.java))
            }

        }
        ratingListener = FirestoreRepository.listenToCollectionChanges("$VIDEO_PATH/$videoId/$RATING_PATH") { querySnapshot, firebaseFirestoreException ->
            if (firebaseFirestoreException != null) {
                Timber.e("Listening failed with $firebaseFirestoreException")
                return@listenToCollectionChanges
            }
            val ratings = emptyList<Rating>().toMutableList()
            for (docs in querySnapshot!!) {
                ratings.add(docs.toObject(Rating::class.java))
            }
            ratingsLiveData.postValue(ratings)
        }
    }


    override fun stopListeningToDbChanges() {
        super.stopListeningToDbChanges()
        ratingListener.remove()
    }

    fun submitComment(comment: Comment) {
        val map = mapOf("author" to comment.author,
                "message" to comment.message,
                "timeStamp" to comment.timeStamp)
        FirestoreRepository.updateDocumentWithCustomId("$VIDEO_PATH/$videoId", COMMENT_PATH, FieldValue.arrayUnion(map))
    }

    fun submitRating(firebaseUserId: String, fl: Float) {
        FirestoreRepository.addDocumentWithCustomId("$VIDEO_PATH/$videoId/$RATING_PATH/$firebaseUserId", Rating(firebaseUserId, fl))
    }
}
