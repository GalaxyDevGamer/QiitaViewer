package galaxy.qiitaviewer.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.res.ResourcesCompat
import android.view.Menu
import android.view.MenuItem
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import galaxy.qiitaviewer.R
import galaxy.qiitaviewer.realm.Article
import io.realm.Realm
import io.realm.RealmResults
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.webview_layout.*

class WebViewActivity : AppCompatActivity() {

    private var isFavourite = false
    private var id: String = ""
    private var title: String = ""
    private var url: String = ""
    private var body: String = ""
    private var image: String = ""
    private var realm: Realm? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.webview_layout)

        setSupportActionBar(toolbar)
        webView.apply {
            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                setSupportZoom(true)
                useWideViewPort = true
                loadWithOverviewMode = true
            }
            webViewClient = WebViewClient()
            webChromeClient = WebChromeClient()
        }
        id = intent.getStringExtra("id")
        title = intent.getStringExtra("title")
        url = intent.getStringExtra("url")
        body = intent.getStringExtra("body")
        image = intent.getStringExtra("profile_image_url")
        webView?.loadUrl(url)
        toolbar.navigationIcon = ResourcesCompat.getDrawable(resources, R.mipmap.ic_keyboard_arrow_left_black_24dp, null)
        toolbar.setNavigationOnClickListener({ finish() })
        realm = Realm.getDefaultInstance()
        isFavourite = isFavourite()
    }

    private fun isFavourite(): Boolean {
        return realm!!.where(Article::class.java).equalTo("id", id).count() > 0
    }

    private fun addToFavourite() {
        if (realm!!.isClosed)
            realm = Realm.getDefaultInstance()
        realm!!.beginTransaction()

        val article = realm!!.createObject(Article::class.java)
        article.id = id
        article.title = title
        article.url = url
        article.body = body
        article.profileImageUrl = image
        realm!!.commitTransaction()
        realm!!.close()
        isFavourite = true
    }

    private fun deleteFromFavourite() {
        if (realm!!.isClosed)
            realm = Realm.getDefaultInstance()
        val result: RealmResults<Article> = realm!!.where(Article::class.java).equalTo("id", id).findAll()
        realm!!.executeTransaction({ result.deleteFromRealm(0) })
        isFavourite = false
    }

    override fun onResume() {
        super.onResume()
        toolbar.title = title
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
//        menu?.clear()
        menuInflater.inflate(R.menu.webview_menu, menu)
        val favourite = menu?.findItem(R.id.favourite)
        if (isFavourite)
            favourite?.setIcon(R.mipmap.ic_star_black_24dp)
        else
            favourite?.setIcon(R.mipmap.ic_star_border_black_24dp)
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (isFavourite)
            deleteFromFavourite()
        else
            addToFavourite()
        invalidateOptionsMenu()
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!realm!!.isClosed)
            realm!!.close()
    }
}