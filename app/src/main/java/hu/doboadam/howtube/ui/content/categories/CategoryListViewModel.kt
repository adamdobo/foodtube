package hu.doboadam.howtube.ui.content.categories


import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import hu.doboadam.howtube.extensions.convertToYoutubeVideo
import hu.doboadam.howtube.extensions.isYoutubeVideo
import hu.doboadam.howtube.model.Category
import hu.doboadam.howtube.network.RetrofitInstance
import hu.doboadam.howtube.network.services.YoutubeApi
import hu.doboadam.howtube.ui.BaseViewModel
import hu.doboadam.howtube.ui.content.videolist.VideoListViewModel
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import timber.log.Timber

class CategoryListViewModel : BaseViewModel() {

    private var categoryListLiveData: MutableLiveData<List<Category>> = MutableLiveData()

    companion object {
        private const val CATEGORIES = "categories"
        private val api: YoutubeApi = RetrofitInstance.getYoutubeRetrofitInstance().create(YoutubeApi::class.java)
        private const val VIDEOS = "videos"
        private val REGEX = "((?<=([vV])/)|(?<=be/)|(?<=([?&])v=)|(?<=embed/))([\\w-]+).".toRegex()
    }

    fun getCategoryListLiveData(): LiveData<List<Category>> = categoryListLiveData

    override fun startListeningToDbChanges() {
        listener = db.collection(CATEGORIES)
                .addSnapshotListener { snapshots: QuerySnapshot?, exception: FirebaseFirestoreException? ->
                    if (exception != null) {
                        Timber.e("Listening failed with $exception")
                        return@addSnapshotListener
                    }
                    val categories = emptyList<Category>().toMutableList()
                    for (docs in snapshots!!) {
                        categories.add(docs.toObject(Category::class.java))
                    }
                    categoryListLiveData.postValue(categories)
                }
    }

    fun checkAndUploadVideo(url: String, categoryId: Int) {
        if (url.isYoutubeVideo()) {
            doAsync {
                val id = REGEX.find(url)
                val response = api.getVideoById(id?.value).execute()
                uiThread {
                    if (response.isSuccessful) {
                        if (response.body()!!.items.any()) {
                            val video = response.body()!!.convertToYoutubeVideo()
                            video.categoryId = categoryId
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

}
