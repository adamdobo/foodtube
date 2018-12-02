package hu.doboadam.howtube.ui.content.categories


import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import hu.doboadam.howtube.extensions.convertToYoutubeVideo
import hu.doboadam.howtube.model.Category
import hu.doboadam.howtube.model.FirestoreRepository
import hu.doboadam.howtube.model.Result
import hu.doboadam.howtube.network.RetrofitInstance
import hu.doboadam.howtube.network.api.YoutubeApi
import hu.doboadam.howtube.ui.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber

class CategoryListViewModel : BaseViewModel() {

    private var categoryListLiveData: MutableLiveData<List<Category>> = MutableLiveData()
    private var _uploadSucceeded = MutableLiveData<Result>()

    companion object {
        private const val CATEGORIES = "categories"
        private val api: YoutubeApi = RetrofitInstance.getYoutubeRetrofitInstance().create(YoutubeApi::class.java)
        private const val VIDEOS = "videos"
        private val REGEX = "((?<=([vV])/)|(?<=be/)|(?<=([?&])v=)|(?<=embed/))([\\w-]+).".toRegex()
    }

    val categoryLiveData : LiveData<List<Category>> = categoryListLiveData
    val uploadSucceeded: LiveData<Result>
        get() = _uploadSucceeded

    override fun startListeningToDbChanges() {
        super.startListeningToDbChanges()
        listener = FirestoreRepository.listenToCollectionChanges(CATEGORIES) { snapshots: QuerySnapshot?, exception: FirebaseFirestoreException? ->
            if (exception != null) {
                Timber.e("Listening failed with $exception")
                return@listenToCollectionChanges
            }
            val categories = emptyList<Category>().toMutableList()
            for (docs in snapshots!!) {
                categories.add(docs.toObject(Category::class.java))
            }
            categoryListLiveData.postValue(categories)
        }
    }

    fun uploadVideo(url: String, categoryId: Int) {
        GlobalScope.launch(Dispatchers.IO) {
            val videoId = REGEX.find(url)
            val retrofitRequest = api.getVideoById(videoId?.value)
            val response = retrofitRequest.await()
            if (response.isSuccessful) {
                val videoResponse = response.body()
                if (videoResponse != null) {
                    val video = videoResponse.convertToYoutubeVideo()
                    video.categoryId = categoryId
                    FirestoreRepository.addDocumentWithCustomId("$VIDEOS/${video.id}", video)
                    _uploadSucceeded.postValue(Result.Success)
                } else {
                    _uploadSucceeded.postValue(Result.Failure)
                }
            } else {
                _uploadSucceeded.postValue(Result.Failure)
            }
        }
    }
}