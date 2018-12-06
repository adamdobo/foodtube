package hu.doboadam.szakdoga.ui.content.videolist

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import hu.doboadam.szakdoga.R
import hu.doboadam.szakdoga.model.YoutubeVideo
import hu.doboadam.szakdoga.ui.BaseViewModelFragment
import kotlinx.android.synthetic.main.fragment_videolist.*
import java.util.*

class  VideoListFragment : BaseViewModelFragment() {

    override val TAG: String = "VideoListFragment"
    private lateinit var adapter: YoutubeVideoAdapter
    private lateinit var listener: OnVideoClickListener
    private lateinit var viewModel: VideoListViewModel
    private var categoryId = 0
    private var startDate : Date? = null

    companion object {
        private const val CATEGORY_ID = "category_id"
        private const val START_DATE = "start_date"
        fun newInstance(id: Int, startDate: Date?): VideoListFragment {
            val fragment = VideoListFragment()
            val bundle = Bundle()
            bundle.putInt(CATEGORY_ID, id)
            bundle.putSerializable(START_DATE, startDate)
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
        startDate = arguments?.getSerializable(START_DATE) as Date?
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
        viewModel.getYoutubeLiveData().observe(this, Observer { value ->
            value?.let {
                adapter.setList(it, startDate)
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