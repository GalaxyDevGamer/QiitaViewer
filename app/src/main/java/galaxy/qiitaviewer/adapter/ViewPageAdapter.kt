package galaxy.qiitaviewer.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import galaxy.qiitaviewer.fragment.FavouriteFragment
import galaxy.qiitaviewer.fragment.HomeFragment

/**
 * Created by galaxy on 2018/03/16.
 */
class ViewPageAdapter(fm: FragmentManager?) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment =
            when (position) {
                0 -> HomeFragment()
                1 -> FavouriteFragment()
                else -> HomeFragment()
            }


    override fun getCount(): Int = TAB_COUNT


    override fun getPageTitle(position: Int): String = FRAGMENT_TITLES[position]


    companion object {
        private const val TAB_COUNT = 2
        private val FRAGMENT_TITLES = arrayOf("Home", "Favourite")
    }
}