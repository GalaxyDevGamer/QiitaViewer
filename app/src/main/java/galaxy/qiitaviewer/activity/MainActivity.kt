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
import galaxy.qiitaviewer.type.FragmentType
import galaxy.qiitaviewer.type.NavigationType
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val fragmentHistory = ArrayList<Fragment>()
    val dataContainer = HashMap<FragmentType, Any>()
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
        setData(FragmentType.HOME, NavigationType.NONE, "Home", R.menu.home_menu)
        changeFragment(fragmentHistory[0])
    }

    fun setData(fragmentType: FragmentType, navigationType: NavigationType, title: String, menu: Int) {
        val data = HashMap<String, Any>()
        data["nav"] = navigationType
        data["title"] = title
        data["menu"] = menu
        dataContainer[fragmentType] = data
        fragmentTypeHistory.add(fragmentType)
        updateToolbar()
    }

    fun changeFragment(fragment: Fragment) {
        fragmentHistory.add(fragment)
        replaceFragment(fragment)
    }

    fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.main_container, fragment).commit()
    }

    fun openBrowser(article: Article) {
        fragmentHistory.add(WebViewFragment.newInstance(ArticleData().apply { this.article = article }))
        setData(FragmentType.VIEWER, NavigationType.BACK, "", R.menu.webview_menu)
        supportFragmentManager.beginTransaction().add(R.id.main_container, fragmentHistory[fragmentHistory.size-1]).commit()
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        menu.clear()
        val data = dataContainer[fragmentTypeHistory[fragmentTypeHistory.size-1]] as HashMap<String, Any>
        menuInflater.inflate(data["menu"] as Int, menu)
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
            ContextData.instance.mainActivity?.setData(FragmentType.SEARCH, NavigationType.BACK, "Search", R.menu.search_menu)
            ContextData.instance.mainActivity?.changeFragment(SearchFragment.newInstance())
        }
        return super.onOptionsItemSelected(item)
    }

    fun updateToolbar() {
        val data = dataContainer[fragmentTypeHistory[fragmentTypeHistory.size-1]] as HashMap<String, Any>
        when (data["nav"]) {
            NavigationType.NONE -> toolbar.navigationIcon = null
            NavigationType.BACK -> {
                toolbar.navigationIcon = ContextCompat.getDrawable(applicationContext, R.mipmap.ic_keyboard_arrow_left_black_24dp)
                toolbar.setNavigationOnClickListener { backFragment() }
            }
        }
        toolbar.title = data["title"] as String
        invalidateOptionsMenu()
    }

    fun backFragment() {
        fragmentHistory.removeAt(fragmentHistory.size-1)
        fragmentTypeHistory.removeAt(fragmentTypeHistory.size-1)
        if (fragmentTypeHistory[fragmentTypeHistory.size-1] == FragmentType.VIEWER) {
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