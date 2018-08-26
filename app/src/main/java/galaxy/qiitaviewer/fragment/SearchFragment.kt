package galaxy.qiitaviewer.fragment

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import galaxy.qiitaviewer.ArticleData
import galaxy.qiitaviewer.ContextData
import galaxy.qiitaviewer.R
import galaxy.qiitaviewer.adapter.SearchAdapter
import galaxy.qiitaviewer.callback.OnRequestComplete
import galaxy.qiitaviewer.callback.QiitaClient
import galaxy.qiitaviewer.callback.RecyclerListener
import galaxy.qiitaviewer.data.Article
import galaxy.qiitaviewer.type.FragmentType
import galaxy.qiitaviewer.type.NavigationType
import kotlinx.android.synthetic.main.fragment_recyclerview.*

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [SearchFragment.OnListFragmentInteractionListener] interface.
 */
class SearchFragment : android.support.v4.app.Fragment(), RecyclerListener, OnRequestComplete {

    private lateinit var searchAdapter: SearchAdapter
    var loading = false
    var currentPage = 1
    var query = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_recyclerview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val layoutManager = LinearLayoutManager(context)
        searchAdapter = SearchAdapter(context, this)
        recyclerView.apply {
            hasFixedSize()
            this.layoutManager = layoutManager
            adapter = searchAdapter
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
                        currentPage += 1
                        loading = true
                        loadMore()
                    }
                }
            })
        }
    }

    override fun onClick(article: Article) {
        ContextData.instance.mainActivity?.openBrowser(article)
    }

    fun searchArticles(query: String) {
        this.query = query
        if (QiitaClient.Encode(query) != null)
            searchAdapter.result.clear()
            QiitaClient.search(QiitaClient.Encode(query), currentPage, this)
    }

    fun loadMore() {
        QiitaClient.search(QiitaClient.Encode(query), currentPage, this)
    }

    override fun onComplete(results: List<Article>) {
        searchAdapter.setResult(results)
    }

    companion object {

        @JvmStatic
        fun newInstance() = SearchFragment()
    }
}