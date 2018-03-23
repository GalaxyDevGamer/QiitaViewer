package galaxy.qiitaviewer

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log

/**
 * Created by galaxy on 2018/03/20.
 */
class OnBottomScrollListener(linearLayoutManager: LinearLayoutManager, private val func: (page:Int) -> Unit):RecyclerView.OnScrollListener() {
    var firstVisibleItem = 0
    var visibleItemCount = 0
    var totalItemCount = 0
    var previousTotal = 0
    var current_page = 1
    var loading = false
    var mlinearLayoutManager = linearLayoutManager

//    fun OnBottomScrollListener(linearLayoutManager: LinearLayoutManager){
//        mlinearLayoutManager = linearLayoutManager
//    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int){
        super.onScrolled(recyclerView, dx, dy)

        visibleItemCount = recyclerView.childCount
        totalItemCount = mlinearLayoutManager.itemCount
        firstVisibleItem = mlinearLayoutManager.findFirstVisibleItemPosition()

        if(loading){
            if(totalItemCount > previousTotal){
                loading = false
                previousTotal = totalItemCount
                Log.d("onScroll", "Configure changes")
            }
        }
        Log.d("onScroll:", "Scrolling")
        if(!loading && (totalItemCount -visibleItemCount) <= (mlinearLayoutManager.findLastVisibleItemPosition()+1)){
            ++current_page
            func(current_page)
            loading = true
            Log.d("onScroll:", "BottomScroll")
        }
    }

//    abstract fun onLoadMore(currentPage: Int)
}