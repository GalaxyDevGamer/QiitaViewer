package galaxy.qiitaviewer.presentation.view

interface StockView {
    fun onComplete()

    fun showError(message: String)

    fun showLoading(state: Boolean)
}