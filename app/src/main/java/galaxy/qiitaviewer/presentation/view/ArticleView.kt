package galaxy.qiitaviewer.presentation.view

interface ArticleView {
    fun onComplete()

    fun showError(message: String)

    fun showLoading(state: Boolean)
}