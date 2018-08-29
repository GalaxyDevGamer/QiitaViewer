package galaxy.qiitaviewer.domain.usecase

import galaxy.qiitaviewer.api.ArticleApi
import galaxy.qiitaviewer.domain.entity.StockResponse
import galaxy.qiitaviewer.helper.PreferenceHelper
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.withContext
import ru.gildor.coroutines.retrofit.awaitResponse
import javax.inject.Inject

class ArticleUseCase @Inject constructor(private val articleApi: ArticleApi){

    suspend fun getArticles(page: Int) = withContext(CommonPool) {
        articleApi.getArticles(page, 10).await()
    }

    suspend fun getStocks(page: Int) = withContext(CommonPool) {
        articleApi.getStocks(PreferenceHelper.instance.getUser(), page).await()
    }

    suspend fun searchArticle(query: String, page: Int) = withContext(CommonPool) {
        articleApi.searchArticles(query, page, 10).await()
    }

    suspend fun isStocked(id: String) = withContext(CommonPool)  {
        articleApi.isStocked(PreferenceHelper.instance.getAccessToken(), id).awaitResponse()
    }

    suspend fun stockArticle(id: String) = withContext(CommonPool) {
        articleApi.stockArticle(PreferenceHelper.instance.getAccessToken(), id).awaitResponse()
    }

    suspend fun unStockArticle(id: String) = withContext(CommonPool) {
        articleApi.unStockArticle(PreferenceHelper.instance.getAccessToken(), id).awaitResponse()
    }

    suspend fun isLiked(id: String) = withContext(CommonPool) {
        articleApi.isLiked(PreferenceHelper.instance.getAccessToken(), id).awaitResponse()
    }

    suspend fun likeThisArticle(id: String) = withContext(CommonPool) {
        articleApi.likeThisArticle(PreferenceHelper.instance.getAccessToken(), id).awaitResponse()
    }

    suspend fun unlikeThisArticle(id: String) = withContext(CommonPool) {
        articleApi.unlikeArticle(PreferenceHelper.instance.getAccessToken(), id).awaitResponse()
    }
}