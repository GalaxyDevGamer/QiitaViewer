package galaxy.qiitaviewer.presentation.presenter

import android.util.Log
import galaxy.qiitaviewer.ContextData
import galaxy.qiitaviewer.domain.entity.Article
import galaxy.qiitaviewer.domain.usecase.ArticleUseCase
import galaxy.qiitaviewer.helper.ArticleManager
import galaxy.qiitaviewer.presentation.fragment.StockFragment
import galaxy.qiitaviewer.presentation.view.StockView
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import javax.inject.Inject

class StockPresenter @Inject constructor(private val useCase: ArticleUseCase) {

    var view: StockFragment? = null
    var currentPage = 1

    fun initialize() = launch(UI) {
        ArticleManager.instance.stock.clear()
        currentPage = 1
        getStocks(currentPage)
    }

    fun loadMore() = launch(UI) {
        currentPage++
        getStocks(currentPage)
    }

    private suspend fun getStocks(page: Int) = useCase.getStocks(page).let {
        try {
            ArticleManager.instance.stock.addAll(it)
            view?.onComplete()
        } catch (e: Throwable) {
            view?.showError("Connection error")
            Log.e("Error", e.message)
        }
    }

    fun openBrowser(article: Article) = ContextData.instance.mainActivity?.openBrowser(article)
}