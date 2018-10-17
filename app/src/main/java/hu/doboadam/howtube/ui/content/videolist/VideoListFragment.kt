package hu.doboadam.howtube.ui.content.videolist

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import hu.doboadam.howtube.R
import hu.doboadam.howtube.R.id.videoList
import hu.doboadam.howtube.extensions.createDialog
import hu.doboadam.howtube.model.YoutubeVideo
import hu.doboadam.howtube.ui.BaseViewModelFragment
import kotlinx.android.synthetic.main.dialog_add_new_video.view.*
import kotlinx.android.synthetic.main.fragment_videolist.*

class VideoListFragment : BaseViewModelFragment() {

    override val TAG: String = "VideoListFragment"
    private lateinit var adapter: YoutubeVideoAdapter
    private lateinit var listener: OnVideoClickListener
    private lateinit var viewModel: VideoListViewModel
    private var categoryId: Int = 0

    companion object {
        private const val CATEGORY_ID = "category_id"
        fun newInstance(id: Int): VideoListFragment {
            val fragment = VideoListFragment()
            val bundle = Bundle()
            bundle.putInt(CATEGORY_ID, id)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_videolist, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        categoryId = arguments?.getInt(CATEGORY_ID)!!
        viewModel = ViewModelProviders.of(this, VideoListViewModelFactory(categoryId)).get(VideoListViewModel::class.java)
        observeViewModel()
        setUpRecyclerView()
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
    private fun setUpRecyclerView() {
        adapter = YoutubeVideoAdapter(emptyList<YoutubeVideo>().toMutableList()) {
            listener.onVideoClicked(it)
        }
        videoList.layoutManager = LinearLayoutManager(context)
        videoList.adapter = adapter
    }

    override fun startListeningToDb() {
        viewModel.startListeningToDbChanges()
    }

    override fun stopListeningToDb() {
        viewModel.stopListeningToDbChanges()
    }

    interface OnVideoClickListener {
        fun onVideoClicked(video: YoutubeVideo)
    }
}