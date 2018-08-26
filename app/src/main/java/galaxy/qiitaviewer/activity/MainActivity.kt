package galaxy.qiitaviewer.activity

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import galaxy.qiitaviewer.ArticleData
import galaxy.qiitaviewer.ContextData
import galaxy.qiitaviewer.R
import galaxy.qiitaviewer.data.Article
import galaxy.qiitaviewer.fragment.HomeFragment
import galaxy.qiitaviewer.fragment.SearchFragment
import galaxy.qiitaviewer.fragment.WebViewFragment
import galaxy.qiitaviewer.helper.FragmentMakeHelper
import galaxy.qiitaviewer.type.FragmentType
import galaxy.qiitaviewer.type.NavigationType
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val fragmentHistory = ArrayList<Fragment>()
    val fragmentTypeHistory = ArrayList<FragmentType>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        initVariable()
    }

    private fun initVariable() {
        ContextData.instance.mainActivity = this
        fragmentHistory.add(HomeFragment.newInstance())
        fragmentTypeHistory.add(FragmentType.HOME)
        changeFragment(fragmentTypeHistory[0], "")
    }

    fun changeFragment(fragmentType: FragmentType, any: Any) {
        val fragment = FragmentMakeHelper.makeFragment(fragmentType, any)
        fragmentHistory.add(fragment)
        fragmentTypeHistory.add(fragmentType)
        replaceFragment(fragment)
        updateToolbar()
    }

    fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.main_container, fragment).commit()
    }

    fun openBrowser(article: Article) {
        fragmentHistory.add(WebViewFragment.newInstance(ArticleData().apply { this.article = article }))
        fragmentTypeHistory.add(FragmentType.BROWSER)
        supportFragmentManager.beginTransaction().add(R.id.main_container, fragmentHistory[fragmentHistory.size-1]).commit()
        updateToolbar()
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        menu.clear()
        menuInflater.inflate(fragmentTypeHistory[fragmentTypeHistory.size-1].menu, menu)
        if (fragmentTypeHistory[fragmentTypeHistory.size-1] == FragmentType.SEARCH) {
            val searchFragment = fragmentHistory[fragmentHistory.size-1] as SearchFragment
            val searchBar = menu.findItem(R.id.searchBar).actionView as SearchView
            searchBar.setIconifiedByDefault(false)
            searchBar.requestFocus()
            searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextChange(newText: String): Boolean {
                    return false
                }

                override fun onQueryTextSubmit(query: String): Boolean {
                    searchFragment.searchArticles(query)
                    return false
                }
            })
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.search) {
            ContextData.instance.mainActivity?.changeFragment(FragmentType.SEARCH, "")
        }
        return super.onOptionsItemSelected(item)
    }

    fun updateToolbar() {
        when (fragmentTypeHistory[fragmentTypeHistory.size-1].navigation) {
            NavigationType.NONE -> {
                toolbar.navigationIcon = null
                toolbar.setNavigationOnClickListener(null)
            }
            NavigationType.BACK -> {
                toolbar.navigationIcon = ContextCompat.getDrawable(applicationContext, R.mipmap.ic_keyboard_arrow_left_black_24dp)
                toolbar.setNavigationOnClickListener { backFragment() }
            }
        }
        toolbar.title = fragmentTypeHistory[fragmentTypeHistory.size-1].title
        invalidateOptionsMenu()
    }

    fun backFragment() {
        fragmentHistory.removeAt(fragmentHistory.size-1)
        fragmentTypeHistory.removeAt(fragmentTypeHistory.size-1)
        if (fragmentTypeHistory[fragmentTypeHistory.size-1] == FragmentType.BROWSER) {
            supportFragmentManager.beginTransaction().remove(fragmentHistory[fragmentHistory.size-1]).commit()
        } else {
            replaceFragment(fragmentHistory[fragmentHistory.size - 1])
        }
        updateToolbar()
    }

    override fun onBackPressed() {
        if (fragmentHistory.size == 1) {
            finish()
            return
        }
        backFragment()
    }
}