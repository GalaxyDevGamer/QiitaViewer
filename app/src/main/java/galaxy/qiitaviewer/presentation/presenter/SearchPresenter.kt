package galaxy.qiitaviewer.presentation.presenter

import android.util.Log
import galaxy.qiitaviewer.ContextData
import galaxy.qiitaviewer.domain.entity.Article
import galaxy.qiitaviewer.domain.usecase.ArticleUseCase
import galaxy.qiitaviewer.presentation.fragment.SearchFragment
import galaxy.qiitaviewer.presentation.view.SearchView
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import javax.inject.Inject

class SearchPresenter @Inject constructor(private val useCase: ArticleUseCase){

    var query = ""
    var currentPage = 1
    var view: SearchView? = null

    fun search(query: String) {
        try {
            // url encode
            val encode_query = URLEncoder.encode(query, "UTF-8")
            this@SearchPresenter.query = encode_query
            launch(UI) {
                currentPage = 1
                searchArticle(encode_query, currentPage)
            }
        } catch (e: UnsupportedEncodingException) {
            Log.e("", e.toString(), e)
            view?.showError("URL encode error")
        }
    }

    fun loadMore() = launch(UI) {
        currentPage++
        loadMoreArticles(query, currentPage)
    }

    private suspend fun searchArticle(query: String, page: Int) = useCase.searchArticle(query, page).let {
        if (it.isSuccessful)
            view?.onComplete(it.body()!!)
        else {
            view?.showError("Connection error")
            Log.e("Search Error", it.message())
        }
    }

    private suspend fun loadMoreArticles(query: String, page: Int) = useCase.searchArticle(query, page).let {
        if (it.isSuccessful)
            view?.appendArticles(it.body()!!)
        else {
            view?.showError("Load Failed: Connection Error")
            Log.e("Append Error", it.message())
        }
    }

    fun openBrowser(article: Article) = ContextData.instance.mainActivity?.openBrowser(article)
}