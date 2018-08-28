package galaxy.qiitaviewer.activity

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
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
import galaxy.qiitaviewer.application.App
import galaxy.qiitaviewer.domain.entity.Article
import galaxy.qiitaviewer.presentation.fragment.SearchFragment
import galaxy.qiitaviewer.helper.FragmentMakeHelper
import galaxy.qiitaviewer.presentation.presenter.MainPresenter
import galaxy.qiitaviewer.type.FragmentType
import galaxy.qiitaviewer.type.NavigationType
import kotlinx.android.synthetic.main.activity_main.*
import java.util.prefs.Preferences
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var presenter: MainPresenter

    val fragmentHistory = ArrayList<Fragment>()
    val fragmentTypeHistory = ArrayList<FragmentType>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        (application as App).appComponent.inject(this)
        initVariable()
    }

    private fun initVariable() {
        ContextData.instance.mainActivity = this
        changeFragment(FragmentType.HOME, "", FragmentType.HOME.title)
    }

    fun changeFragment(fragmentType: FragmentType, any: Any, title: String) {
        val fragment = FragmentMakeHelper.makeFragment(fragmentType, any)
        fragmentType.title = title
        fragmentHistory.add(fragment)
        fragmentTypeHistory.add(fragmentType)
        replaceFragment(fragment)
        updateToolbar()
    }

    fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.main_container, fragment).commit()
    }

    fun openBrowser(article: Article) {
        val fragment = FragmentMakeHelper.makeFragment(FragmentType.BROWSER, ArticleData().apply { this.article = article })
        fragmentHistory.add(fragment)
        fragmentTypeHistory.add(FragmentType.BROWSER)
        supportFragmentManager.beginTransaction().add(R.id.main_container, fragment).commit()
        updateToolbar()
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        menu.clear()
        menuInflater.inflate(fragmentTypeHistory[fragmentTypeHistory.size - 1].menu, menu)
        if (fragmentTypeHistory[fragmentTypeHistory.size - 1] == FragmentType.SEARCH) {
            val searchFragment = fragmentHistory[fragmentHistory.size - 1] as SearchFragment
            val searchBar = menu.findItem(R.id.searchBar).actionView as SearchView
            searchBar.apply {
                setIconifiedByDefault(false)
                clearFocus()
                setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextChange(newText: String): Boolean {
                        return false
                    }

                    override fun onQueryTextSubmit(query: String): Boolean {
                        searchFragment.searchArticles(query)
                        return false
                    }
                })
            }
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.search) {
            changeFragment(FragmentType.SEARCH, "", FragmentType.SEARCH.title)
        }
        if (item?.itemId == R.id.user) {
            val uri = "https://qiita.com/api/v2/oauth/authorize?" +
                    "client_id=" + "bc3deb1194eff0ce4fd62e4d9e0e9fc628f942ea" +
                    "&scope=" + "read_qiita+write_qiita" +
                    "&state=" + "ab6s5adw121wsa2120ed7fe1"
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(uri)))
        }
        return super.onOptionsItemSelected(item)
    }

    fun updateToolbar() {
        when (fragmentTypeHistory[fragmentTypeHistory.size - 1].navigation) {
            NavigationType.NONE -> {
                toolbar.navigationIcon = null
                toolbar.setNavigationOnClickListener(null)
            }
            NavigationType.BACK -> {
                toolbar.navigationIcon = ContextCompat.getDrawable(applicationContext, R.mipmap.ic_keyboard_arrow_left_black_24dp)
                toolbar.setNavigationOnClickListener { backFragment() }
            }
        }
        toolbar.title = fragmentTypeHistory[fragmentTypeHistory.size - 1].title
        invalidateOptionsMenu()
    }

    fun backFragment() {
        fragmentHistory.removeAt(fragmentHistory.size - 1)
        fragmentTypeHistory.removeAt(fragmentTypeHistory.size - 1)
        if (fragmentTypeHistory[fragmentTypeHistory.size - 1] == FragmentType.BROWSER) {
            supportFragmentManager.beginTransaction().remove(fragmentHistory[fragmentHistory.size - 1]).commit()
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