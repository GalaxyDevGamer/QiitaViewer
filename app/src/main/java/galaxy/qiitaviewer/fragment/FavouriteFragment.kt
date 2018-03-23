package galaxy.qiitaviewer.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import galaxy.qiitaviewer.R
import galaxy.qiitaviewer.activity.WebViewActivity
import galaxy.qiitaviewer.callback.RecyclerListener
import galaxy.qiitaviewer.adapter.RecyclerAdapter
import galaxy.qiitaviewer.realm.Article

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [FavouriteFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [FavouriteFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FavouriteFragment : Fragment(), RecyclerListener {

    private var recyclerAdapter: RecyclerAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_recyclerview, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerAdapter = RecyclerAdapter(context, this)
        recyclerView.apply {
            hasFixedSize()
            layoutManager = LinearLayoutManager(context)
            adapter = recyclerAdapter
        }
        view.findViewById<SwipeRefreshLayout>(R.id.swipeRefreshLayout).setOnRefreshListener({
            loadRealm()
            view.findViewById<SwipeRefreshLayout>(R.id.swipeRefreshLayout).isRefreshing = false
        })
        return view
    }

    override fun onResume() {
        super.onResume()
        loadRealm()
//        Toast.makeText(context, "onResume() from Favourite", Toast.LENGTH_LONG).show()
    }

    private fun loadRealm() {
        recyclerAdapter?.clear()
        recyclerAdapter?.addList(Article.favouriteToInfo())
//        realm = Realm.getDefaultInstance()
//        val result: RealmResults<Article> = realm!!.where(Article::class.java).findAllAsync()
//        result.load()
//        for (items: Article in realm!!.copyFromRealm(result)) {
//            recyclerAdapter?.add(Info(items.id!!, items.title!!, items.body!!, items.url!!, User(items.profileImageUrl!!)))
//        }
//        realm!!.close()
    }

    override fun onClick(position: Int) {
        val intent = Intent(context, WebViewActivity::class.java)
        intent.putExtra("id", recyclerAdapter?.getId(position))
        intent.putExtra("title", recyclerAdapter?.getTitle(position))
        intent.putExtra("url", recyclerAdapter?.getUrl(position))
        intent.putExtra("body", recyclerAdapter?.getBody(position))
        intent.putExtra("profile_image_url", recyclerAdapter?.getImage(position))
        context!!.startActivity(intent)
    }
}