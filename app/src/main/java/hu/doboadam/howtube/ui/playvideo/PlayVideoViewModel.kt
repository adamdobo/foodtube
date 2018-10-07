package hu.doboadam.howtube.ui.playvideo

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.google.firebase.firestore.FieldValue
import hu.doboadam.howtube.model.Comment
import hu.doboadam.howtube.model.YoutubeVideo
import hu.doboadam.howtube.ui.BaseViewModel
import timber.log.Timber

class PlayVideoViewModel(private val videoId: String) : BaseViewModel() {

    private val youtubeVideoLiveData: MutableLiveData<YoutubeVideo> = MutableLiveData()
    private val VIDEO_PATH = "videos/$videoId"
    private val COMMENT_PATH = "comments"

    val getYoutubeVideoLiveData: LiveData<YoutubeVideo> = youtubeVideoLiveData

    override fun startListeningToDbChanges() {
        listener = db.document(VIDEO_PATH)
                .addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                    if(firebaseFirestoreException != null){
                        Timber.e("Listen failed with $firebaseFirestoreException")
                    }
                    if(documentSnapshot != null && documentSnapshot.exists()){
                        youtubeVideoLiveData.postValue(documentSnapshot.toObject(YoutubeVideo::class.java))
                    }
                }
    }

    fun submitComment(comment: Comment) {
        val map = mapOf("author" to comment.author,
                "message" to comment.message,
                "timeStamp" to comment.timeStamp)
        db.document(VIDEO_PATH)
                .update(COMMENT_PATH, FieldValue.arrayUnion(map))

    }
}
