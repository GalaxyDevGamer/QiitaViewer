package galaxy.qiitaviewer.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import galaxy.qiitaviewer.ArticleData
import galaxy.qiitaviewer.ContextData
import galaxy.qiitaviewer.R
import galaxy.qiitaviewer.application.App
import galaxy.qiitaviewer.domain.entity.AccessTokenResponse
import galaxy.qiitaviewer.domain.entity.Article
import galaxy.qiitaviewer.domain.usecase.AuthorizeUseCase
import galaxy.qiitaviewer.presentation.fragment.SearchFragment
import galaxy.qiitaviewer.helper.FragmentMakeHelper
import galaxy.qiitaviewer.helper.PreferenceHelper
import galaxy.qiitaviewer.presentation.fragment.UserInfoFragment
import galaxy.qiitaviewer.presentation.presenter.MainPresenter
import galaxy.qiitaviewer.type.FragmentType
import galaxy.qiitaviewer.type.NavigationType
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
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
        presenter.view = this
        initVariable()
    }

    private fun initVariable() {
        ContextData.instance.mainActivity = this
        ContextData.instance.context = this
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

    override fun onResume() {
        super.onResume()
        Log.e("activity", "onresume")
    }
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Log.e("activity", "onnewintent")
        if (intent?.action == Intent.ACTION_VIEW) {
            Log.e("intent", "action view")
            val code = intent.data?.getQueryParameter("code")
            if (code != null) {
                launch(UI) {
                    presenter.getAccessToken(code)
                    presenter.getUserInfo()
                    runOnUiThread {
                        showAccessToken()
                        if (fragmentTypeHistory[fragmentTypeHistory.size-1] == FragmentType.USERINFO)
                            (fragmentHistory[fragmentHistory.size-1] as UserInfoFragment).showUserInfo()
                    }
                }
            }
        }
    }
    fun showAccessToken() = Toast.makeText(this@MainActivity, "Authentication success", Toast.LENGTH_LONG).show()

    fun replaceFragment(fragment: Fragment) = supportFragmentManager.beginTransaction().replace(R.id.main_container, fragment).commit()

    fun openBrowser(article: Article) {
        val fragment = FragmentMakeHelper.makeFragment(FragmentType.BROWSER, ArticleData().apply { this.article = article })
        fragmentHistory.add(fragment)
        fragmentTypeHistory.add(FragmentType.BROWSER)
        supportFragmentManager.beginTransaction().add(R.id.main_container, fragment).commit()
        updateToolbar()
    }

    fun showUserScreen() {
        val fragment = FragmentMakeHelper.makeFragment(FragmentType.USERINFO, "")
        fragmentHistory.add(fragment)
        fragmentTypeHistory.add(FragmentType.USERINFO)
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
        if (item?.itemId == R.id.search)
            changeFragment(FragmentType.SEARCH, "", FragmentType.SEARCH.title)
        if (item?.itemId == R.id.user)
            showUserScreen()
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