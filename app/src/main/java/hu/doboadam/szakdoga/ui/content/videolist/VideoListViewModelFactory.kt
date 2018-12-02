package hu.doboadam.szakdoga.ui.content.videolist

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider

class VideoListViewModelFactory(private val categoryId: Int) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return VideoListViewModel(categoryId) as T
    }
}