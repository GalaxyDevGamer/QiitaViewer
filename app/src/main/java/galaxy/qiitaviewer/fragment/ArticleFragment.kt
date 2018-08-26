package galaxy.qiitaviewer.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import galaxy.qiitaviewer.*
import galaxy.qiitaviewer.adapter.ArticleAdapter
import galaxy.qiitaviewer.callback.OnRequestComplete
import galaxy.qiitaviewer.callback.QiitaClient
import galaxy.qiitaviewer.callback.RecyclerListener
import galaxy.qiitaviewer.data.Article
import galaxy.qiitaviewer.helper.ArticleManager
import galaxy.qiitaviewer.type.FragmentType
import galaxy.qiitaviewer.type.NavigationType
import kotlinx.android.synthetic.main.fragment_recyclerview.*


/**
 * A simple [Fragment] subclass.
 * Use the [ArticleFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ArticleFragment : Fragment(), RecyclerListener, OnRequestComplete {

    private lateinit var homeAdapter: ArticleAdapter
    var currentPage = 1
    var loading = false
    private var isSwipeRefresh = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recyclerview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val layoutManager = LinearLayoutManager(context)
        homeAdapter = ArticleAdapter(context, this)
        recyclerView!!.apply {
            hasFixedSize()
            this.layoutManager = layoutManager
            adapter = homeAdapter
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
                homeAdapter.list.clear()
                currentPage = 1
                isSwipeRefresh = true
                getArticles(currentPage)
            }
        }
        if (ArticleManager.instance.artist.size == 0)
            getArticles(currentPage)
    }

    fun getArticles(page: Int) {
        QiitaClient.getArticle(page, this)
    }

    override fun onComplete(results: List<Article>) {
        ArticleManager.instance.artist.addAll(results)
        homeAdapter.notifyDataSetChanged()
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
        fun newInstance() = ArticleFragment()
    }
}