package hu.doboadam.szakdoga.ui.playvideo

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.widget.RatingBar
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerSupportFragment
import hu.doboadam.szakdoga.R
import hu.doboadam.szakdoga.extensions.addFragmentWithTag
import hu.doboadam.szakdoga.extensions.getFirebaseUserId
import hu.doboadam.szakdoga.model.Comment
import hu.doboadam.szakdoga.model.Rating
import hu.doboadam.szakdoga.model.YoutubeVideo
import kotlinx.android.synthetic.main.activity_play_video.*
import timber.log.Timber

class PlayVideoActivity : AppCompatActivity() {

    private lateinit var videoId: String
    private lateinit var viewModel: PlayVideoViewModel
    private lateinit var adapter: CommentListAdapter
    private lateinit var youtubePlayer: YouTubePlayer


    companion object {
        const val VIDEO_ID = "video_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_video)
        val uri = intent.data
        if (uri?.path != null) {
            videoId = uri.path!!.removePrefix("/recipe/")
        } else {
            if (intent.hasExtra(VIDEO_ID)) {
                videoId = intent.getStringExtra(VIDEO_ID)
            }
        }

        viewModel = ViewModelProviders.of(this, PlayVideoViewModelFactory(videoId)).get(PlayVideoViewModel::class.java)
        observeViewmodel()
        addFragmentWithTag(YouTubePlayerSupportFragment.newInstance(), R.id.youtubePlayerContainer, YouTubePlayerSupportFragment::class.java.simpleName)
        initYoutubePlayer()
        submitComment.setOnClickListener {
            if (commentEditText.text.isNullOrBlank()) {
                commentEditLayout.error = getString(R.string.cannot_be_empty)
            } else {
                commentEditLayout.error = null
                viewModel.submitComment(Comment(getFirebaseUserId(), commentEditText.text.toString(), System.currentTimeMillis()))
                commentEditText.text?.clear()
            }

        }
        ratingBar.onRatingBarChangeListener = RatingBar.OnRatingBarChangeListener { _, fl: Float, _ ->
            viewModel.submitRating(getFirebaseUserId()!!, fl)
        }
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        adapter = CommentListAdapter(emptyList<Comment>().toMutableList())
        commentList.layoutManager = object : LinearLayoutManager(this) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }
        commentList.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        commentList.adapter = adapter
    }

    private fun observeViewmodel() {
        viewModel.getYoutubeVideoLiveData.observe(this, Observer<YoutubeVideo> { value ->
            value?.let {
                refreshData(it)
            }
        })

        viewModel.getRatingsLiveData.observe(this, Observer<List<Rating>> { value ->
            value?.let {
                refreshRatings(it)
            }
        })
    }

    private fun refreshRatings(ratings: List<Rating>) {
        val rating = ratings.firstOrNull { it.author == getFirebaseUserId() }
        ratingBar.rating = when (rating) {
            null -> {
                ratingText.text = "0"
                0F
            }
            else -> {
                ratingText.text = ratings.map { it.rating }.average().toString()
                rating.rating
            }
        }

        usersText.text = when {
            ratings.size > 1 -> getString(R.string.users, ratings.size)
            else -> getString(R.string.user, ratings.size)
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.startListeningToDbChanges()
    }

    override fun onStop() {
        super.onStop()
        viewModel.stopListeningToDbChanges()
    }

    private fun refreshData(video: YoutubeVideo) {
        video.apply {
            videoTitle.text = snippet.title
            adapter.refreshItems(comments)
        }
    }


    private fun initYoutubePlayer() {
        val youTubePlayerSupportFragment =
                supportFragmentManager.findFragmentByTag(YouTubePlayerSupportFragment::class.java.simpleName) as YouTubePlayerSupportFragment
        youTubePlayerSupportFragment.initialize(getString(R.string.api_key), object : YouTubePlayer.OnInitializedListener {
            override fun onInitializationSuccess(provider: YouTubePlayer.Provider, player: YouTubePlayer, wasRestored: Boolean) {
                if (!wasRestored) {
                    youtubePlayer = player
                    player.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT)
                    player.loadVideo(videoId)
                }
            }

            override fun onInitializationFailure(p0: YouTubePlayer.Provider?, p1: YouTubeInitializationResult?) {
                Timber.e("Youtube initialization failed!")
            }
        })
    }

    override fun onBackPressed() {
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            youtubePlayer.setFullscreen(false)
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        } else {
            finish()
        }
    }
}
