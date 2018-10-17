package hu.doboadam.howtube.ui.playvideo

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ListenerRegistration
import hu.doboadam.howtube.model.Category
import hu.doboadam.howtube.model.Comment
import hu.doboadam.howtube.model.Rating
import hu.doboadam.howtube.model.YoutubeVideo
import hu.doboadam.howtube.ui.BaseViewModel
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
        listener = db.collection(VIDEO_PATH).document(videoId)
                .addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                    if(firebaseFirestoreException != null){
                        Timber.e("Listen failed with $firebaseFirestoreException")
                    }
                    if(documentSnapshot != null && documentSnapshot.exists()){
                        youtubeVideoLiveData.postValue(documentSnapshot.toObject(YoutubeVideo::class.java))
                    }
                }
        ratingListener = db.collection(VIDEO_PATH)
                .document(videoId)
                .collection(RATING_PATH)
                .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    if (firebaseFirestoreException != null) {
                        Timber.e("Listening failed with $firebaseFirestoreException")
                        return@addSnapshotListener
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
        db.collection(VIDEO_PATH)
                .document(videoId)
                .update(COMMENT_PATH, FieldValue.arrayUnion(map))

    }

    fun submitRating(firebaseUserId: String?, fl: Float) {
        db.collection(VIDEO_PATH)
                .document(videoId)
                .collection(RATING_PATH)
                .document(firebaseUserId!!)
                .set(Rating(firebaseUserId, fl))
    }
}
