package hu.doboadam.howtube.ui.content

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import hu.doboadam.howtube.R
import hu.doboadam.howtube.extensions.replaceFragment
import hu.doboadam.howtube.model.YoutubeVideo
import hu.doboadam.howtube.ui.login.LoginActivity

class ContentActivity : AppCompatActivity(), VideoListFragment.OnVideoClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_content)
        replaceFragment(VideoListFragment.newInstance(), R.id.fragmentContainer)
    }

    override fun onVideoClicked(video: YoutubeVideo) {
        val intent = Intent(this, PlayVideoActivity::class.java)
        intent.putExtra(PlayVideoActivity.VIDEO_ID, video.id)
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.content_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item?.itemId == R.id.menu_logout){
            logout()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun logout() {
        FirebaseAuth.getInstance().signOut()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
