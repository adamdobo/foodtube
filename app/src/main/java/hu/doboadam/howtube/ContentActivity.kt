package hu.doboadam.howtube

import android.os.Bundle
import com.google.android.youtube.player.YouTubeBaseActivity

class ContentActivity : YouTubeBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_content)
    }

}
