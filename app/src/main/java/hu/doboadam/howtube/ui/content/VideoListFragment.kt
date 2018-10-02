package hu.doboadam.howtube.ui.content

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import hu.doboadam.howtube.R
import hu.doboadam.howtube.extensions.createDialog
import hu.doboadam.howtube.model.YoutubeVideo
import kotlinx.android.synthetic.main.dialog_add_new_video.view.*
import kotlinx.android.synthetic.main.fragment_videolist.view.*

class VideoListFragment : Fragment() {

    private lateinit var viewModel: ContentViewModel
    private lateinit var adapter: YoutubeVideoAdapter
    private lateinit var listener: VideoListFragment.OnVideoClickListener

    companion object {
        fun newInstance(): VideoListFragment {
            return VideoListFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_videolist, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ContentViewModel::class.java)
        observeViewModel()
        setUpRecyclerView()
        view?.addVideo?.setOnClickListener {
            createDialog {
                setTitle(getString(R.string.add_new_video))
                val dialogView = layoutInflater.inflate(R.layout.dialog_add_new_video, null)
                setView(dialogView)
                setNegativeButton(getString(R.string.cancel), null)
                setPositiveButton(getString(R.string.ok)) { _, _ ->
                    viewModel.checkAndUploadVideo(dialogView.videoUrlText.text.toString())
                }
            }.show()
        }
    }


    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnVideoClickListener) {
            listener = context
        } else {
            throw RuntimeException("${context.toString()} must implement interface onVideoClickedListener")
        }
    }

    private fun observeViewModel() {
        viewModel.getYoutubeLiveData().observe(this, Observer<List<YoutubeVideo>> { value ->
            value?.let {
                adapter.setList(it)
            }
        })

    }

    override fun onStart() {
        super.onStart()
        viewModel.startListeningToDbChanges()
    }

    override fun onStop() {
        super.onStop()
        viewModel.stopListeningToDbChanges()
    }

    private fun setUpRecyclerView() {
        adapter = YoutubeVideoAdapter(emptyList<YoutubeVideo>().toMutableList()) {
            listener.onVideoClicked(it)
        }
        view?.videoList?.layoutManager = LinearLayoutManager(context)
        view?.videoList?.adapter = adapter
    }

    interface OnVideoClickListener {
        fun onVideoClicked(video: YoutubeVideo)
    }
}