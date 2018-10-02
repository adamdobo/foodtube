package hu.doboadam.howtube.ui.content

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerSupportFragment
import hu.doboadam.howtube.R
import hu.doboadam.howtube.extensions.addFragmentWithTag
import timber.log.Timber

class PlayVideoActivity : AppCompatActivity() {

    private lateinit var videoId: String
    private lateinit var viewModel: PlayVideoViewModel

    companion object {
        const val VIDEO_ID = "video_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_video)
        videoId = intent.getStringExtra(VIDEO_ID)
        addFragmentWithTag(YouTubePlayerSupportFragment.newInstance(), R.id.youtubePlayerContainer, YouTubePlayerSupportFragment::class.java.simpleName)
        initYoutubePlayer()
    }

    private fun initYoutubePlayer() {
        val youTubePlayerSupportFragment =
                supportFragmentManager.findFragmentByTag(YouTubePlayerSupportFragment::class.java.simpleName) as YouTubePlayerSupportFragment
        youTubePlayerSupportFragment.initialize(getString(R.string.api_key), object: YouTubePlayer.OnInitializedListener {
            override fun onInitializationSuccess(provider: YouTubePlayer.Provider, player: YouTubePlayer, wasRestored: Boolean) {
                if(!wasRestored){
                    player.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT)
                    player.loadVideo(videoId)
                }
            }

            override fun onInitializationFailure(p0: YouTubePlayer.Provider?, p1: YouTubeInitializationResult?) {
                Timber.e("Youtube initialization failed!")
            }
        })
    }
}
