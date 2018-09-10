package galaxy.qiitaviewer.presentation.fragment

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.*
import galaxy.qiitaviewer.R
import galaxy.qiitaviewer.application.App
import galaxy.qiitaviewer.presentation.adapter.SearchAdapter
import galaxy.qiitaviewer.callback.RecyclerListener
import galaxy.qiitaviewer.domain.entity.Article
import galaxy.qiitaviewer.presentation.presenter.SearchPresenter
import kotlinx.android.synthetic.main.recycler_base.*
import javax.inject.Inject

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [RecyclerListener] interface.
 */
class SearchFragment : android.support.v4.app.Fragment(), RecyclerListener {

    @Inject
    lateinit var presenter: SearchPresenter

    private lateinit var searchAdapter: SearchAdapter
    var loading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity?.application as App).appComponent.inject(this)
        presenter.view = this
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
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
                        loading = true
                        Log.e("search", "loadmore")
                        presenter.loadMore()
                    }
                }
            })
        }
    }

    override fun onClick(article: Article) {
        presenter.openBrowser(article)
    }

    fun searchArticles(query: String) = presenter.search(query)

    fun showError(text: String) = Snackbar.make(view!!, text, Snackbar.LENGTH_LONG).show()

    fun onComplete(results: List<Article>) = searchAdapter.update(results)

    fun appendArticles(results: List<Article>) = searchAdapter.appendArticles(results)

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        Log.e("searchFragment", "oncreate menu")
        inflater?.inflate(R.menu.search_menu, menu)
        (menu!!.findItem(R.id.searchBar).actionView as SearchView).apply {
            setIconifiedByDefault(false)
            queryHint = getString(R.string.search)
            clearFocus()
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextChange(newText: String): Boolean {
                    return false
                }

                override fun onQueryTextSubmit(query: String): Boolean {
                    searchArticles(query)
                    return false
                }
            })
        }
    }

    companion object {

        @JvmStatic
        fun newInstance() = SearchFragment()
    }
}