package galaxy.qiitaviewer.activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.Menu
import galaxy.qiitaviewer.R
import galaxy.qiitaviewer.adapter.RecyclerAdapter
import galaxy.qiitaviewer.callback.QiitaClient
import galaxy.qiitaviewer.callback.RecyclerListener
import galaxy.qiitaviewer.data.Info
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.toolbar.*
import retrofit2.Call
import retrofit2.Response
import java.io.UnsupportedEncodingException
import java.net.URLEncoder

class SearchActivity : AppCompatActivity(), RecyclerListener {

    var recyclerAdapter: RecyclerAdapter? = null
    var loading: Boolean = false
    var currentPage: Int = 1
    private var query: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        setSupportActionBar(toolbar)
        val layoutManager = LinearLayoutManager(this)
        recyclerAdapter = RecyclerAdapter(this, this)
        recyclerView.apply {
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
                    if (!loading && (visibleItemCount + firstVisibleItem) > (totalItemCount-5)) {
                        currentPage += 1
                        loading = true
                        getArticles(currentPage)
                    }
                }
            })
        }
        toolbar.navigationIcon = ResourcesCompat.getDrawable(resources, R.mipmap.ic_keyboard_arrow_left_black_24dp, null)
        toolbar.setNavigationOnClickListener({ finish() })
    }

    fun getArticles(page: Int) {
        try {
            // url encode
            query = URLEncoder.encode(query, "UTF-8")
        } catch (e: UnsupportedEncodingException) {
            Log.e("", e.toString(), e)
        }
        QiitaClient.create().searchArticles(query, page, 10).enqueue(object : retrofit2.Callback<List<Info>> {
            override fun onResponse(call: Call<List<Info>>?, response: Response<List<Info>>?) {
                recyclerAdapter?.addList(response?.body()!!)
            }

            override fun onFailure(call: Call<List<Info>>?, t: Throwable?) {
            }
        })
    }

    override fun onClick(position: Int) {
        val intent = Intent(this, WebViewActivity::class.java)
        intent.putExtra("id", recyclerAdapter?.getId(position))
        intent.putExtra("url", recyclerAdapter?.getUrl(position))
        intent.putExtra("title", recyclerAdapter?.getTitle(position))
        intent.putExtra("body", recyclerAdapter?.getBody(position))
        intent.putExtra("profile_image_url", recyclerAdapter?.getImage(position))
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        toolbar.title = ""
    }
    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)
        val searchItem = menu.findItem(R.id.searchBar)
        val searchBar = searchItem.actionView as SearchView
        searchBar.setIconifiedByDefault(false)
        searchBar.requestFocus()
        searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                this@SearchActivity.query = query
                recyclerAdapter?.clear()
                getArticles(currentPage)
                return false
            }
        })
        searchBar.setOnCloseListener(object : SearchView.OnCloseListener{
            override fun onClose(): Boolean {
                query = ""
                recyclerAdapter?.clear()
                currentPage = 1
                return false
            }
        })
        return true
    }
}