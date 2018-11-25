package hu.doboadam.howtube.ui.content.browseyoutubevideos

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import hu.doboadam.howtube.R
import hu.doboadam.howtube.model.YoutubeVideoOnlySnippet
import hu.doboadam.howtube.ui.BaseViewModelFragment
import kotlinx.android.synthetic.main.fragment_videolist.*

class BrowseYoutubeVideosFragment : BaseViewModelFragment() {

    override val TAG: String = "BrowseYoutubeVideosFragment"
    private lateinit var viewModel: BrowseYoutubeVideosViewModel
    private lateinit var adapter: YoutubeVideoListAdapter

    companion object {
        fun newInstance(): BaseViewModelFragment = BrowseYoutubeVideosFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_videolist, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(BrowseYoutubeVideosViewModel::class.java)
        initRecyclerView()
        observeViewModel()
    }

    private fun initRecyclerView() {
        adapter = YoutubeVideoListAdapter(emptyList<YoutubeVideoOnlySnippet>().toMutableList()) {
            val openYoutubeInAppIntent = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:${it.id.videoId}"))
            val openYoutubeInBrowserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=${it.id.videoId}"))
            try {
                startActivity(openYoutubeInAppIntent)
            } catch (e: ActivityNotFoundException){
                startActivity(openYoutubeInBrowserIntent)
            }
        }
        videoList.adapter = adapter
        videoList.layoutManager = LinearLayoutManager(context)
    }

    private fun observeViewModel() {
        viewModel.videoList.observe(this, Observer {value ->
            value?.let {
                adapter.setList(it.items)
            }
        })
    }

    override fun startListeningToDb() {
        viewModel.startListeningToDbChanges()
    }

    override fun stopListeningToDb() {
        viewModel.stopListeningToDbChanges()
    }


}