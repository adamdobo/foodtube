package hu.doboadam.howtube.ui.content

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import hu.doboadam.howtube.model.YoutubeVideo
import hu.doboadam.howtube.ui.BaseViewModel

class PlayVideoViewModel(private val videoId: String) : BaseViewModel() {

    private val youtubeVideoLiveData: MutableLiveData<YoutubeVideo> = MutableLiveData()




    val getYoutubeVideoLiveData: LiveData<YoutubeVideo> = youtubeVideoLiveData

    override fun startListeningToDbChanges() {
        //listener = db.document("videos/$videoId")
    }
}
