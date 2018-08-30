package galaxy.qiitaviewer.presentation.presenter

import android.util.Log
import galaxy.qiitaviewer.ContextData
import galaxy.qiitaviewer.domain.entity.Article
import galaxy.qiitaviewer.domain.usecase.ArticleUseCase
import galaxy.qiitaviewer.helper.ArticleManager
import galaxy.qiitaviewer.presentation.fragment.ArticleFragment
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import javax.inject.Inject

class ArticlePresenter @Inject constructor(private val useCase: ArticleUseCase) {

    var view: ArticleFragment? = null
    var currentPage = 1

    fun initialize() = launch(UI) {
        ArticleManager.instance.article.clear()
        currentPage = 1
        getArticles(currentPage)
    }

    fun loadMore() = launch(UI) {
        currentPage++
        getArticles(currentPage)
    }

    private suspend fun getArticles(page: Int) = useCase.getArticles(page).let {
        if (it.isSuccessful) {
            ArticleManager.instance.article.addAll(it.body()!!)
            view?.onComplete()
        } else {
            view?.showError("Failed to get articles")
            Log.e("Error:", it.message())
        }
    }

    fun openBrowser(article: Article) = ContextData.instance.mainActivity?.openBrowser(article)
}