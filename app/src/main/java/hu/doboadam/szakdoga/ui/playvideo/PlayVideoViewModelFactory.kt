package hu.doboadam.szakdoga.ui.playvideo

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider

class PlayVideoViewModelFactory(private val videoId: String) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PlayVideoViewModel(videoId) as T
    }
}