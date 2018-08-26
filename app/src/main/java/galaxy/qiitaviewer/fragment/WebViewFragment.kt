package galaxy.qiitaviewer.fragment


import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.*
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import galaxy.qiitaviewer.ArticleData
import galaxy.qiitaviewer.R
import galaxy.qiitaviewer.data.Article
import galaxy.qiitaviewer.helper.ArticleManager
import galaxy.qiitaviewer.realm.Favourite
import io.realm.Realm
import kotlinx.android.synthetic.main.webview_layout.*
import java.io.Serializable

/**
 * A simple [Fragment] subclass.
 * Use the [WebViewFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class WebViewFragment : android.support.v4.app.Fragment() {
    lateinit var article: Article

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val data = arguments?.getSerializable(ARG) as ArticleData
        article = data.article!!
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_web_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
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
        webView?.loadUrl(article.url)
    }

    private fun isFavourite() = Realm.getDefaultInstance().where(Favourite::class.java).equalTo("id", article.id).count() > 0

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        menu?.findItem(R.id.favourite)?.icon = if (isFavourite()) ContextCompat.getDrawable(context!!, R.mipmap.ic_star_black_24dp)!! else ContextCompat.getDrawable(context!!, R.mipmap.ic_star_border_black_24dp)!!
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        ArticleManager.instance.isFavouriteUpdated =! ArticleManager.instance.isFavouriteUpdated
        if (isFavourite())
            Realm.getDefaultInstance().executeTransaction { Realm.getDefaultInstance().where(Favourite::class.java).equalTo("id", article.id).findFirst()?.deleteFromRealm() }
        else {
            val favourite = Favourite().apply {
                id = article.id
                title = article.title
                url = article.url
                body = article.body
                profileImageUrl = article.user.profile_image_url
            }
            Realm.getDefaultInstance().executeTransaction { it.insertOrUpdate(favourite) }
        }
        activity!!.invalidateOptionsMenu()
        return super.onOptionsItemSelected(item)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param arg Parameter 1.
         * @return A new instance of fragment WebViewFragment.
         */
        private const val ARG = "index"

        @JvmStatic
        fun newInstance(arg: Any) = WebViewFragment().apply {
            arguments = Bundle().apply {
                putSerializable(ARG, arg as Serializable)
            }
        }
    }
}