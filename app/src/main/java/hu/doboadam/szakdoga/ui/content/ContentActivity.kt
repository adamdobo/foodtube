package hu.doboadam.szakdoga.ui.content

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import hu.doboadam.szakdoga.R
import hu.doboadam.szakdoga.extensions.createDialog
import hu.doboadam.szakdoga.extensions.replaceFragment
import hu.doboadam.szakdoga.model.YoutubeVideo
import hu.doboadam.szakdoga.ui.content.categories.CategoryListFragment
import hu.doboadam.szakdoga.ui.content.videolist.VideoListFragment
import hu.doboadam.szakdoga.ui.login.LoginActivity
import hu.doboadam.szakdoga.ui.playvideo.PlayVideoActivity
import kotlinx.android.synthetic.main.activity_content.*
import java.util.concurrent.TimeUnit

class ContentActivity : AppCompatActivity(), ContentFragment.OnBackPressedListener, VideoListFragment.OnVideoClickListener, CategoryListFragment.OnCategoryClickedListener {
    private lateinit var viewModel: ContentViewModel
    private lateinit var countDownTimer: CountDownTimer
    private lateinit var adapter: MyViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_content)
        adapter = MyViewPagerAdapter(this, supportFragmentManager)
        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)
        viewModel = ViewModelProviders.of(this).get(ContentViewModel::class.java)
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.getContestLiveData.observe(this, Observer { contest ->
            contest?.let {
                if (System.currentTimeMillis() < it.endDate.time
                        && System.currentTimeMillis() > it.startDate.time
                        && contestView.visibility != View.VISIBLE) {
                    countDownTimer = object : CountDownTimer(it.endDate.time - System.currentTimeMillis(), 1000) {
                        override fun onTick(millisUntilFinished: Long) {
                            contestView.text = getString(R.string.ongoing_contest,
                                    String.format("%02d:%02d:%02d",
                                            TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                                            TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60,
                                            TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60))
                        }

                        override fun onFinish() {
                            contestView.visibility = View.GONE
                        }
                    }
                    countDownTimer.start()
                    contestView.setOnClickListener {
                        createDialog {
                            setTitle(getString(R.string.details))
                            setMessage(contest.description)
                            setPositiveButton(getString(R.string.go_to_category)) { _, _ ->
                                replaceFragment(VideoListFragment.newInstance(contest.categoryId, contest.startDate), R.id.fragmentContainer, true)
                            }
                            setNegativeButton(getString(R.string.got_it)) { dialogInterface: DialogInterface, _ ->
                                dialogInterface.dismiss()
                            }
                        }.show()
                    }
                    contestView.visibility = View.VISIBLE
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.content_menu, menu)
        return true
    }

    override fun onStart() {
        super.onStart()
        viewModel.startListeningToDbChanges()
    }

    override fun onStop() {
        viewModel.stopListeningToDbChanges()
        super.onStop()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.menu_logout) {
            logout()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun logout() {
        FirebaseAuth.getInstance().signOut()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }


    override fun onBackPressed() {
        if(viewPager.currentItem == 0){
            adapter.contentFragment.onBackPressed()
        } else {
            super.onBackPressed()
        }
    }

    override fun defaultBack() {
        super.onBackPressed()
    }

    override fun onVideoClicked(video: YoutubeVideo) {
        val intent = Intent(this, PlayVideoActivity::class.java)
        intent.putExtra(PlayVideoActivity.VIDEO_ID, video.id)
        startActivity(intent)
    }

    override fun onCategoryClicked(id: Int) {
        adapter.contentFragment.replaceFragment(VideoListFragment.newInstance(id, null), R.id.fragmentContainer, true)
    }
}
