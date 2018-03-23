package galaxy.qiitaviewer.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.*
import galaxy.qiitaviewer.*
import galaxy.qiitaviewer.activity.WebViewActivity
import galaxy.qiitaviewer.adapter.RecyclerAdapter
import galaxy.qiitaviewer.callback.QiitaClient
import galaxy.qiitaviewer.callback.RecyclerListener
import galaxy.qiitaviewer.data.Info
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [HomeFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment(), RecyclerListener {

    var recyclerAdapter: RecyclerAdapter? = null
    var recyclerView: RecyclerView? = null
    var currentPage = 1
    var loading = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_recyclerview, container, false)
        recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        val layoutManager = LinearLayoutManager(context)
        recyclerAdapter = RecyclerAdapter(context, this)
        recyclerView!!.apply {
            hasFixedSize()
            this.layoutManager = layoutManager
            adapter = recyclerAdapter
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
                        getArticles(false, currentPage)
                    }
                }
            })
        }
        getArticles(false, currentPage)
//        recyclerView.addOnScrollListener(onBottomScrollListener(LinearLayoutManager(context)){
//            getArticles(it)
//        })
        view.findViewById<SwipeRefreshLayout>(R.id.swipeRefreshLayout).setOnRefreshListener({
            if (!loading) {
                loading = true
                recyclerAdapter?.clear()
                currentPage = 1
                getArticles(true, currentPage)
            }
            view.findViewById<SwipeRefreshLayout>(R.id.swipeRefreshLayout)?.isRefreshing = false
            loading = false
        })
        return view
    }

    fun getArticles(isSwipeRefresh: Boolean, page: Int) {
        QiitaClient.create().getArticles(page, 10).enqueue(object : Callback<List<Info>> {
            override fun onResponse(call: Call<List<Info>>?, response: Response<List<Info>>?) {
                Log.d("Responce", "" + response?.body()?.size)
                val list: List<Info> = response?.body()!!
                recyclerAdapter?.addList(list)
                if (isSwipeRefresh)
                    recyclerView!!.smoothScrollToPosition(0)
//                    response?.body()?.forEach { recyclerAdapter?.update(it) }
                Log.d("Qiita API", "Recycler size:" + recyclerAdapter?.itemCount)
            }

            override fun onFailure(call: Call<List<Info>>?, t: Throwable?) {
            }
        })
    }

    override fun onClick(position: Int) {
        val intent = Intent(context, WebViewActivity::class.java)
        intent.putExtra("id", recyclerAdapter?.getId(position))
        intent.putExtra("url", recyclerAdapter?.getUrl(position))
        intent.putExtra("title", recyclerAdapter?.getTitle(position))
        intent.putExtra("body", recyclerAdapter?.getBody(position))
        intent.putExtra("profile_image_url", recyclerAdapter?.getImage(position))
        context?.startActivity(intent)
    }
}