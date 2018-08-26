package galaxy.qiitaviewer.fragment

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import galaxy.qiitaviewer.ContextData
import galaxy.qiitaviewer.R
import galaxy.qiitaviewer.adapter.StockAdapter
import galaxy.qiitaviewer.callback.OnRequestComplete
import galaxy.qiitaviewer.callback.QiitaClient
import galaxy.qiitaviewer.callback.RecyclerListener
import galaxy.qiitaviewer.data.Article
import galaxy.qiitaviewer.helper.ArticleManager
import kotlinx.android.synthetic.main.fragment_recyclerview.*

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [StockFragment.OnListFragmentInteractionListener] interface.
 */
class StockFragment : android.support.v4.app.Fragment(), RecyclerListener, OnRequestComplete {

    private lateinit var stockAdapter: StockAdapter
    var currentPage = 1
    var loading = false
    private var isSwipeRefresh = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_recyclerview, container, false)
    }

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
                        currentPage++
                        loading = true
                        getArticles(currentPage)
                    }
                }
            })
        }
        swipe_refresh.setOnRefreshListener {
            if (!loading) {
                loading = true
                stockAdapter.list.clear()
                currentPage = 1
                isSwipeRefresh = true
                getArticles(currentPage)
            }
        }
        if (ArticleManager.instance.stock.size == 0) {
            getArticles(currentPage)
            Log.e("stock", "initialize")
        }
    }

    fun getArticles(page: Int) = QiitaClient.getStock(page, this)

    override fun onComplete(results: List<Article>) {
        ArticleManager.instance.stock.addAll(results)
        stockAdapter.notifyDataSetChanged()
        loading = false
        if (isSwipeRefresh) {
            recyclerView.smoothScrollToPosition(0)
            swipe_refresh.isRefreshing = false
            isSwipeRefresh = false
        }
    }

    override fun onClick(article: Article) {
        ContextData.instance.mainActivity?.openBrowser(article)
    }

    companion object {

        @JvmStatic
        fun newInstance() = StockFragment()
    }
}