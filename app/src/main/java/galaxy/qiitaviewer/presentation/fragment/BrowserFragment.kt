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
import galaxy.qiitaviewer.presentation.presenter.BrowserPresenter
import galaxy.qiitaviewer.presentation.view.BrowserView
import kotlinx.android.synthetic.main.fragment_web_view.*
import java.io.Serializable
import javax.inject.Inject

/**
 * A simple [BrowserFragment] subclass.
 * Use the [BrowserFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BrowserFragment : android.support.v4.app.Fragment(), BrowserView {

    @Inject
    internal lateinit var presenter: BrowserPresenter

    lateinit var article: Article

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity?.application as App).appComponent.inject(this)
        article = (arguments?.getSerializable(ARG) as ArticleData).article!!
        presenter.view = this
        presenter.id = article.id
        presenter.getStates()
        setHasOptionsMenu(true)
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

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.webview_menu, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        menu?.findItem(R.id.favourite)?.icon = presenter.getDrawable(context!!)
        menu?.findItem(R.id.stock)?.icon = presenter.stockImage(context!!)
        menu?.findItem(R.id.like)?.icon = presenter.likeImage(context!!)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.stock -> presenter.stock()
            R.id.like -> presenter.like()
            R.id.favourite -> presenter.changeFavourite(article)
        }
        activity!!.invalidateOptionsMenu()
        return super.onOptionsItemSelected(item)
    }

    override fun showNeedToLogin() = Snackbar.make(view!!, "You need to login to Qiita to do this", Snackbar.LENGTH_LONG).setAction(R.string.login) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.authorize_uri))))
    }.show()

    override fun showMessage(message: String) = Snackbar.make(view!!, message, Snackbar.LENGTH_LONG).show()

    override fun updateMenu() {
        activity?.invalidateOptionsMenu()
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
        fun newInstance(arg: Any) = BrowserFragment().apply {
            arguments = Bundle().apply {
                putSerializable(ARG, arg as Serializable)
            }
        }
    }
}