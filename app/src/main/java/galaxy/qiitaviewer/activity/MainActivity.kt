package galaxy.qiitaviewer.activity

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.squareup.picasso.Picasso
import galaxy.qiitaviewer.ArticleData
import galaxy.qiitaviewer.ContextData
import galaxy.qiitaviewer.R
import galaxy.qiitaviewer.application.App
import galaxy.qiitaviewer.domain.entity.Article
import galaxy.qiitaviewer.helper.FragmentMakeHelper
import galaxy.qiitaviewer.helper.PreferenceHelper
import galaxy.qiitaviewer.presentation.fragment.ArticleFragment
import galaxy.qiitaviewer.presentation.fragment.LectureFragment
import galaxy.qiitaviewer.presentation.fragment.StockFragment
import galaxy.qiitaviewer.presentation.fragment.UserInfoFragment
import galaxy.qiitaviewer.presentation.presenter.MainPresenter
import galaxy.qiitaviewer.type.FragmentType
import galaxy.qiitaviewer.type.NavigationType
import galaxy.qiitaviewer.type.TabType
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var presenter: MainPresenter

    val fragmentHistory = HashMap<TabType, ArrayList<Fragment>>()
    val fragmentTypeHistory = HashMap<TabType, ArrayList<FragmentType>>()

    var currentTabType = TabType.HOME

    var rootHistory = ArrayList<Fragment>()
    var rootTypeHistory = ArrayList<FragmentType>()

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
        bottom_navigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    changeTab(TabType.HOME)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.stocks -> {
                    changeTab(TabType.STOCKS)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.lectures -> {
                    changeTab(TabType.LECTURES)
                    return@setOnNavigationItemSelectedListener true
                }
                else -> return@setOnNavigationItemSelectedListener true
            }
        }
        fragmentHistory[TabType.HOME] = ArrayList()
        fragmentHistory[TabType.STOCKS] = ArrayList()
        fragmentHistory[TabType.LECTURES] = ArrayList()
        fragmentTypeHistory[TabType.HOME] = ArrayList()
        fragmentTypeHistory[TabType.STOCKS] = ArrayList()
        fragmentTypeHistory[TabType.LECTURES] = ArrayList()
        fragmentHistory[TabType.HOME]?.add(ArticleFragment.newInstance())
        fragmentHistory[TabType.STOCKS]?.add(StockFragment.newInstance())
        fragmentHistory[TabType.LECTURES]?.add(LectureFragment.newInstance())
        fragmentTypeHistory[TabType.HOME]?.add(FragmentType.ARTICLE)
        fragmentTypeHistory[TabType.STOCKS]?.add(FragmentType.STOCKS)
        fragmentTypeHistory[TabType.LECTURES]?.add(FragmentType.LECTURES)
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.homeTab, fragmentHistory[TabType.HOME]!![0])
            replace(R.id.stockTab, fragmentHistory[TabType.STOCKS]!![0])
            replace(R.id.lectureTab, fragmentHistory[TabType.LECTURES]!![0])
            commit()
        }
        changeTab(TabType.HOME)
    }

    fun changeTab(tabType: TabType) {
        currentTabType = tabType
        updateToolbar()
        homeTab.visibility = if (currentTabType == TabType.HOME) View.VISIBLE else View.INVISIBLE
        stockTab.visibility = if (currentTabType == TabType.STOCKS) View.VISIBLE else View.INVISIBLE
        lectureTab.visibility = if (currentTabType == TabType.LECTURES) View.VISIBLE else View.INVISIBLE
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent?.action == Intent.ACTION_VIEW) {
            val code = intent.data?.getQueryParameter("code")
            if (code != null) {
                launch(UI) {
                    presenter.getAccessToken(code)
                    presenter.getUserInfo()
                    if (rootTypeHistory[rootTypeHistory.size-1] == FragmentType.USERINFO)
                        (rootHistory[rootHistory.size-1] as UserInfoFragment).presenter.getUserInfo()
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, "Authentication success", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    fun replaceFragment(fragment: Fragment) = supportFragmentManager.beginTransaction().replace(R.id.main_container, fragment).commit()

    fun openBrowser(article: Article) = openFragmentToAdd(FragmentType.BROWSER, FragmentMakeHelper.makeFragment(FragmentType.BROWSER, ArticleData().apply { this.article = article }))

    fun openFragmentToAdd(fragmentType: FragmentType, fragment: Fragment) {
        rootHistory.add(fragment)
        rootTypeHistory.add(fragmentType)
        bottom_navigation.visibility = View.GONE
        supportFragmentManager.beginTransaction().add(R.id.main_container, fragment).commit()
        updateToolbar()
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        Log.e("mainActivity", "prepare menu")
        if (rootHistory.size == 0) {
            menu.clear()
            menuInflater.inflate(fragmentTypeHistory[currentTabType]!![fragmentTypeHistory[currentTabType]!!.size - 1].menu, menu)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.search)
            openFragmentToAdd(FragmentType.SEARCH, FragmentMakeHelper.makeFragment(FragmentType.SEARCH, ""))
        return super.onOptionsItemSelected(item)
    }

    fun updateToolbar() {
        if (rootHistory.size > 0) {
            toolbar.apply {
                navigationIcon = ContextCompat.getDrawable(applicationContext, R.mipmap.ic_keyboard_arrow_left_black_24dp)
                setNavigationOnClickListener { backFragment() }
                title = rootTypeHistory[rootTypeHistory.size-1].title
            }
            invalidateOptionsMenu()
            return
        }
        toolbar.apply {
            when (fragmentTypeHistory[currentTabType]!![fragmentTypeHistory[currentTabType]!!.size - 1].navigation) {
                NavigationType.NONE -> {
                    navigationIcon = null
                    setNavigationOnClickListener(null)
                }
                NavigationType.BACK -> {
                    navigationIcon = ContextCompat.getDrawable(applicationContext, R.mipmap.ic_keyboard_arrow_left_black_24dp)
                    setNavigationOnClickListener { backFragment() }
                }
                NavigationType.USER -> {
                    launch {
                        navigationIcon = if (PreferenceHelper.instance.getProfileImage().isNullOrEmpty()) ContextCompat.getDrawable(applicationContext, R.mipmap.baseline_person_black_24) else BitmapDrawable(resources, Bitmap.createScaledBitmap(Picasso.with(this@MainActivity).load(PreferenceHelper.instance.getProfileImage()).get(), 100, 100, false))
                    }
                    setNavigationOnClickListener { openFragmentToAdd(FragmentType.USERINFO, FragmentMakeHelper.makeFragment(FragmentType.USERINFO, "")) }
                }
            }
            title = fragmentTypeHistory[currentTabType]!![fragmentTypeHistory[currentTabType]!!.size - 1].title
        }
        invalidateOptionsMenu()
    }

    fun backFragment() {
        if (rootHistory.size > 0) {
            supportFragmentManager.beginTransaction().remove(rootHistory[0]).commit()
            rootHistory.removeAt(rootHistory.size-1)
            rootTypeHistory.removeAt(rootTypeHistory.size-1)
            bottom_navigation.visibility = View.VISIBLE
        } else {
            fragmentHistory[currentTabType]?.removeAt(fragmentHistory[currentTabType]!!.size - 1)
            fragmentTypeHistory[currentTabType]?.removeAt(fragmentTypeHistory[currentTabType]!!.size - 1)
            replaceFragment(fragmentHistory[currentTabType]!![fragmentHistory[currentTabType]!!.size - 1])
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