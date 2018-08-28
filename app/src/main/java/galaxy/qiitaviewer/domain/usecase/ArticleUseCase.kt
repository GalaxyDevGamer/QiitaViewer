package galaxy.qiitaviewer.domain.usecase

import galaxy.qiitaviewer.api.ArticleApi
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.withContext
import javax.inject.Inject

class ArticleUseCase @Inject constructor(private val articleApi: ArticleApi){

    suspend fun getArticles(page: Int) = withContext(CommonPool) {
        articleApi.getArticles(page, 10).await()
    }

    suspend fun getStocks(page: Int) = withContext(CommonPool) {
        articleApi.getStocks(page).await()
    }

    suspend fun searchArticle(query: String, page: Int) = withContext(CommonPool) {
        articleApi.searchArticles(query, page, 10).await()
    }
}