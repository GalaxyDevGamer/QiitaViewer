package galaxy.qiitaviewer.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import galaxy.qiitaviewer.fragment.ArticleFragment
import galaxy.qiitaviewer.fragment.FavouriteFragment
import galaxy.qiitaviewer.fragment.StockFragment

/**
 * Created by galaxy on 2018/03/16.
 */
class ViewPageAdapter(fm: FragmentManager?) : FragmentPagerAdapter(fm) {

    private val fragments: MutableList<Fragment> = mutableListOf()

    init {
        if (fragments.size == 0) {
            fragments.add(ArticleFragment.newInstance())
            fragments.add(FavouriteFragment.newInstance())
            fragments.add(StockFragment.newInstance())
        }
    }

    override fun getItem(position: Int) = fragments[position]

    override fun getCount() = fragments.size

    override fun getPageTitle(position: Int) = FRAGMENT_TITLES[position]

    fun favouriteFragment() = fragments[1] as FavouriteFragment

    companion object {
        private val FRAGMENT_TITLES = arrayOf("Home", "Favourite", "Stock")
    }
}