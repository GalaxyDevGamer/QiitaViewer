package galaxy.qiitaviewer.presentation.view

import galaxy.qiitaviewer.domain.entity.Article

interface SearchView {
    fun onComplete(results: List<Article>)

    fun showError(message: String)

    fun appendArticles(results: List<Article>)
}