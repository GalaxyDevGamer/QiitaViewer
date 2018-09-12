package galaxy.qiitaviewer.presentation.presenter

import android.util.Log
import galaxy.qiitaviewer.ContextData
import galaxy.qiitaviewer.domain.entity.Article
import galaxy.qiitaviewer.domain.usecase.ArticleUseCase
import galaxy.qiitaviewer.helper.ArticleManager
import galaxy.qiitaviewer.helper.PreferenceHelper
import galaxy.qiitaviewer.presentation.fragment.StockFragment
import galaxy.qiitaviewer.presentation.view.StockView
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import javax.inject.Inject

class StockPresenter @Inject constructor(private val useCase: ArticleUseCase) {

    var view: StockView? = null
    var currentPage = 1

    /**
     * Clear all articles on array(Which managed on Singleton) and load first page of article
     */
    fun initialize() = launch(UI) {
        if (PreferenceHelper.instance.getUser() != null) {
            view?.showLoading(true)
            ArticleManager.instance.stock.clear()
            currentPage = 1
            getStocks(currentPage)
        }
    }

    /**
     * Load more articles when user scrolled the view to the bottom
     */
    fun loadMore() = launch(UI) {
        view?.showLoading(true)
        currentPage++
        getStocks(currentPage)
    }

    /**
     * Get articles by api on useCase using coroutine
     */
    private suspend fun getStocks(page: Int) = useCase.getStocks(page).let {
        if (it.isSuccessful) {
            ArticleManager.instance.stock.addAll(it.body()!!)
            view?.onComplete()
        } else {
            view?.showError("Failed to get stocks")
            Log.e("Error:", it.message())
        }
        view?.showLoading(false)
    }

    /**
     * Open BrowserFragment from mainActivity
     */
    fun openBrowser(article: Article) = ContextData.instance.mainActivity?.openBrowser(article)
}