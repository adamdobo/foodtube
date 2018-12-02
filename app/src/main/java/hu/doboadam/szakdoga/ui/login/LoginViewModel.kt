package hu.doboadam.szakdoga.ui.login

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.google.firebase.appindexing.FirebaseAppIndex
import com.google.firebase.appindexing.Indexable
import hu.doboadam.szakdoga.extensions.getMostFittingThumbnailUrl
import hu.doboadam.szakdoga.model.FirestoreRepository
import hu.doboadam.szakdoga.model.Result
import hu.doboadam.szakdoga.model.YoutubeVideo
import hu.doboadam.szakdoga.ui.BaseViewModel

class LoginViewModel : BaseViewModel() {

    private val _indexingResult: MutableLiveData<Result> = MutableLiveData()

    val indexingResult: LiveData<Result>
        get() = _indexingResult

    companion object {
        private const val VIDEOS = "videos"
        private const val START_URL = "https://doboadam.hu/start"
    }

    fun updateAppIndex(isFirstRun: Boolean) {
        val appIndex = FirebaseAppIndex.getInstance()
        if (isFirstRun) {
            appIndex.update(Indexable.Builder()
                    .setUrl(START_URL)
                    .setName("FoodTube")
                    .setKeywords("food", "recipe", "recipes", "cooking", "food videos", "recipe videos")
                    .build())
            val query = FirestoreRepository.getCollection(VIDEOS)
            query.addOnSuccessListener { querySnapshot ->
                for (snapshot in querySnapshot) {
                    val video = snapshot.toObject(YoutubeVideo::class.java)
                    appIndex.update(Indexable.Builder()
                            .setUrl("http://doboadam.hu/recipe/${video.id}")
                            .setName(video.snippet.title)
                            .setImage(video.snippet.thumbnails.getMostFittingThumbnailUrl())
                            .build())
                }
                _indexingResult.postValue(Result.Success)
            }
            query.addOnFailureListener {
                _indexingResult.postValue(Result.Failure)
            }
        }
    }


}