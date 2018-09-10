package galaxy.qiitaviewer.presentation.fragment

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
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
import galaxy.qiitaviewer.presentation.adapter.ArticleAdapter
import galaxy.qiitaviewer.presentation.presenter.ArticlePresenter
import galaxy.qiitaviewer.presentation.view.ArticleView
import kotlinx.android.synthetic.main.recycler_base.*
import javax.inject.Inject


/**
 * A simple [Fragment] subclass.
 * Use the [ArticleFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ArticleFragment : Fragment(), RecyclerListener, ArticleView {

    @Inject
    internal lateinit var presenter: ArticlePresenter

    private lateinit var homeAdapter: ArticleAdapter
    var loading = false
    private var isSwipeRefresh = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity?.application as App).appComponent.inject(this)
        presenter.view = this
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.recycler_base, container, false)
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
        if (ArticleManager.instance.article.size == 0)
            presenter.initialize()
    }

    override fun onComplete() {
        homeAdapter.notifyDataSetChanged()
        loading = false
        if (isSwipeRefresh) {
            recyclerView.smoothScrollToPosition(0)
            swipe_refresh.isRefreshing = false
            isSwipeRefresh = false
        }
    }

    override fun onClick(article: Article) {
        presenter.openBrowser(article)
    }

    fun showError(text: String) = Snackbar.make(view!!, text, Snackbar.LENGTH_LONG).show()

    companion object {

        @JvmStatic
        fun newInstance() = ArticleFragment()
    }
}