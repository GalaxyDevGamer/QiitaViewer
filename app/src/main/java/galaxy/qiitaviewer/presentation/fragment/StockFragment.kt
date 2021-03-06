package galaxy.qiitaviewer.presentation.fragment

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import galaxy.qiitaviewer.R
import galaxy.qiitaviewer.application.App
import galaxy.qiitaviewer.callback.RecyclerListener
import galaxy.qiitaviewer.domain.entity.Article
import galaxy.qiitaviewer.helper.ArticleManager
import galaxy.qiitaviewer.presentation.adapter.StockAdapter
import galaxy.qiitaviewer.presentation.presenter.StockPresenter
import galaxy.qiitaviewer.presentation.view.StockView
import kotlinx.android.synthetic.main.recycler_base.*
import javax.inject.Inject

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [RecyclerListener] interface.
 */
class StockFragment : android.support.v4.app.Fragment(), RecyclerListener, StockView {

    @Inject
    internal lateinit var presenter: StockPresenter

    private lateinit var stockAdapter: StockAdapter
    var loading = false
    private var isSwipeRefresh = false

    /**
     * Injection and setting View interface
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity?.application as App).appComponent.inject(this)
        presenter.view = this
    }

    /**
     * Inflate layout
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.recycler_base, container, false)
    }

    /**
     * Initialize RecyclerView and load Stocks
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        stockAdapter = StockAdapter(context!!, this)
        val layoutManager = LinearLayoutManager(context)
        recyclerView.apply {
            hasFixedSize()
            this.layoutManager = layoutManager
            adapter = stockAdapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                var firstVisibleItem = 0
                var visibleItemCount = 0
                var totalItemCount = 0
                var previousTotal = 0

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    visibleItemCount = recyclerView.childCount
                    totalItemCount = layoutManager.itemCount
                    firstVisibleItem = layoutManager.findFirstVisibleItemPosition()

                    if (totalItemCount != previousTotal) {
                        loading = false
                    }

                    previousTotal = totalItemCount
                    if (!loading && (visibleItemCount + firstVisibleItem) > (totalItemCount - 5)) {
                        loading = true
                        presenter.loadMore()
                    }
                }
            })
        }
        swipe_refresh.setOnRefreshListener {
            if (!loading) {
                loading = true
                isSwipeRefresh = true
                presenter.initialize()
            }
        }
        if (ArticleManager.instance.stock.size == 0)
            presenter.initialize()
    }

    /**
     * Called when loading finished
     */
    override fun onComplete() {
        stockAdapter.notifyDataSetChanged()
        loading = false
        if (isSwipeRefresh) {
            recyclerView.smoothScrollToPosition(0)
            swipe_refresh.isRefreshing = false
            isSwipeRefresh = false
        }
    }

    /**
     * Called when article clicked
     */
    override fun onClick(article: Article) {
        presenter.openBrowser(article)
    }

    /**
     * Called to show error message
     */
    override fun showError(message: String) = Snackbar.make(view!!, message, Snackbar.LENGTH_LONG).show()

    /**
     * Switch visibility of ProgressBar
     */
    override fun showLoading(state: Boolean) {
        progressBar.visibility = if (state) View.VISIBLE else View.GONE
    }

    /**
     * Initialize this Fragment
     */
    companion object {

        @JvmStatic
        fun newInstance() = StockFragment()
    }
}