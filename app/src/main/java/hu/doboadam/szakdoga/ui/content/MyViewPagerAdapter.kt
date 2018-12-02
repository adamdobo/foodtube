package hu.doboadam.szakdoga.ui.content

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import hu.doboadam.szakdoga.R
import hu.doboadam.szakdoga.ui.content.browseyoutubevideos.BrowseYoutubeVideosFragment

class MyViewPagerAdapter(private val context: Context, fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {

    companion object {
        private const val NUM_PAGES = 2
    }

    lateinit var contentFragment: ContentFragment

    override fun getItem(position: Int): Fragment {
        return when(position){
            0 -> { contentFragment = ContentFragment.newInstance()
                    contentFragment
            }
            1 -> BrowseYoutubeVideosFragment.newInstance()
            else -> ContentFragment.newInstance()
        }
    }

    override fun getCount(): Int {
        return NUM_PAGES
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when(position) {
            0 -> context.getString(R.string.uploaded_videos)
            1 -> context.getString(R.string.youtube_videos)
            else -> context.getString(R.string.uploaded_videos)
        }
    }
}