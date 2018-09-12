package galaxy.qiitaviewer.presentation.presenter

import android.util.Log
import galaxy.qiitaviewer.ContextData
import galaxy.qiitaviewer.domain.entity.Article
import galaxy.qiitaviewer.domain.usecase.ArticleUseCase
import galaxy.qiitaviewer.helper.ArticleManager
import galaxy.qiitaviewer.presentation.fragment.ArticleFragment
import galaxy.qiitaviewer.presentation.view.ArticleView
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import javax.inject.Inject

class ArticlePresenter @Inject constructor(private val useCase: ArticleUseCase) {

    var view: ArticleView? = null
    var currentPage = 1

    /**
     * Clear all articles on array(Which managed on Singleton) and load first page of article
     */
    fun initialize() = launch(UI) {
        view?.showLoading(true)
        ArticleManager.instance.article.clear()
        currentPage = 1
        getArticles(currentPage)
    }

    /**
     * Load more articles when user scrolled the view to the bottom
     */
    fun loadMore() = launch(UI) {
        view?.showLoading(true)
        currentPage++
        getArticles(currentPage)
    }

    /**
     * Get articles by api on useCase using coroutine
     */
    private suspend fun getArticles(page: Int) = useCase.getArticles(page).let {
        if (it.isSuccessful) {
            ArticleManager.instance.article.addAll(it.body()!!)
            view?.onComplete()
        } else {
            view?.showError("Failed to get articles")
            Log.e("Error:", it.message())
        }
        view?.showLoading(false)
    }

    /**
     * Open BrowserFragment from mainActivity
     */
    fun openBrowser(article: Article) = ContextData.instance.mainActivity?.openBrowser(article)
}