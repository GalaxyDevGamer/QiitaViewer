package galaxy.qiitaviewer.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import galaxy.qiitaviewer.ArticleData
import galaxy.qiitaviewer.ContextData
import galaxy.qiitaviewer.R
import galaxy.qiitaviewer.adapter.FavouriteAdapter
import galaxy.qiitaviewer.callback.RecyclerListener
import galaxy.qiitaviewer.data.Article
import galaxy.qiitaviewer.helper.ArticleManager
import galaxy.qiitaviewer.realm.Favourite
import galaxy.qiitaviewer.type.FragmentType
import galaxy.qiitaviewer.type.NavigationType
import kotlinx.android.synthetic.main.fragment_recyclerview.*

/**
 * A simple [Fragment] subclass.
 * Use the [FavouriteFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FavouriteFragment : Fragment(), RecyclerListener {

    private lateinit var favouriteAdapter: FavouriteAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_recyclerview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        favouriteAdapter = FavouriteAdapter(context, this)
        recyclerView.apply {
            hasFixedSize()
            layoutManager = LinearLayoutManager(context)
            adapter = favouriteAdapter
        }
        swipe_refresh.setOnRefreshListener {
            swipe_refresh.isRefreshing = false
        }
    }

    private fun getFavourite() {
        ArticleManager.instance.favourite.clear()
        ArticleManager.instance.favourite.addAll(Favourite.getFavourites())
        favouriteAdapter.notifyDataSetChanged()
    }

    override fun onClick(article: Article) {
        ContextData.instance.mainActivity?.openBrowser(article)
    }

    fun refresh() {
        if (ArticleManager.instance.isFavouriteUpdated) { getFavourite() }
    }

    companion object {

        @JvmStatic
        fun newInstance() = FavouriteFragment()
    }
}