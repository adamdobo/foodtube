package hu.doboadam.howtube.ui.content

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.v7.app.AppCompatActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerSupportFragment
import hu.doboadam.howtube.R
import hu.doboadam.howtube.extensions.createDialog
import kotlinx.android.synthetic.main.activity_content.*

class ContentActivity : AppCompatActivity() {

    private lateinit var viewModel: ContentViewModel
    private lateinit var youtubePlayerFragment: YouTubePlayerSupportFragment
    private lateinit var youtubePlayer: YouTubePlayer


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_content)
        viewModel = ViewModelProviders.of(this).get(ContentViewModel::class.java)
        initializeYoutubePlayer()

        addVideo.setOnClickListener {
          createDialog {
                setTitle("Add new video")
                setView(R.layout.dialog_add_new_video)
                setNegativeButton("Cancel", null)
                setPositiveButton("OK") { _, _ ->
                    viewModel.checkAndUploadVideo("Ks-_Mh1QhMc")
                }.show()
            }
        }
    }

    private fun initializeYoutubePlayer() {
        youtubePlayerFragment = supportFragmentManager.findFragmentById(R.id.youtubePlayer) as YouTubePlayerSupportFragment
        youtubePlayerFragment.initialize(getString(R.string.api_key), object: YouTubePlayer.OnInitializedListener {
            override fun onInitializationSuccess(provider: YouTubePlayer.Provider, player: YouTubePlayer, wasRestored: Boolean) {
                youtubePlayer = player
                youtubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT)
            }

            override fun onInitializationFailure(p0: YouTubePlayer.Provider?, p1: YouTubeInitializationResult?) {
                //TODO: do something on failure
            }

        })
    }

}
