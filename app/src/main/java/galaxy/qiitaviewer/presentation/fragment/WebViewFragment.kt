package galaxy.qiitaviewer.presentation.fragment


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.*
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import galaxy.qiitaviewer.ArticleData
import galaxy.qiitaviewer.R
import galaxy.qiitaviewer.application.App
import galaxy.qiitaviewer.domain.entity.Article
import galaxy.qiitaviewer.helper.PreferenceHelper
import galaxy.qiitaviewer.presentation.presenter.WebViewPresenter
import kotlinx.android.synthetic.main.webview_layout.*
import java.io.Serializable
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 * Use the [WebViewFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class WebViewFragment : android.support.v4.app.Fragment() {

    @Inject
    internal lateinit var presenter: WebViewPresenter

    lateinit var article: Article

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity?.application as App).appComponent.inject(this)
        article = (arguments?.getSerializable(ARG) as ArticleData).article!!
        presenter.view = this
        presenter.id = article.id
        presenter.getStates()
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

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        menu?.findItem(R.id.favourite)?.icon = presenter.getDrawable(context!!)
        menu?.findItem(R.id.stock)?.icon = presenter.stockImage(context!!)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.stock -> if (PreferenceHelper.instance.isAuthorized()) presenter.stock() else showSnackbar()
            R.id.like -> if (PreferenceHelper.instance.isAuthorized()) presenter.like() else showSnackbar()
            R.id.favourite -> presenter.changeFavourite(article)
        }
        activity!!.invalidateOptionsMenu()
        return super.onOptionsItemSelected(item)
    }

    fun showSnackbar() = Snackbar.make(view!!, "You need to login to Qiita to do this", Snackbar.LENGTH_LONG).setAction(R.string.login) {
        val uri = "https://qiita.com/api/v2/oauth/authorize?" +
                "client_id=" + "bc3deb1194eff0ce4fd62e4d9e0e9fc628f942ea" +
                "&scope=" + "read_qiita+write_qiita" +
                "&state=" + "ab6s5adw121wsa2120ed7fe1"
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(uri)))
    }.show()

    fun showMessage(message: String) = Snackbar.make(view!!, message, Snackbar.LENGTH_LONG).show()

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