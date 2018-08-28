package galaxy.qiitaviewer.api

import galaxy.qiitaviewer.domain.entity.Article
import kotlinx.coroutines.experimental.Deferred
import retrofit2.http.GET
import retrofit2.http.Query

interface ArticleApi {
    @GET("/api/v2/items")
    fun getArticles(@Query("page") page: Int, @Query("per_page") perPage: Int): Deferred<List<Article>>

    @GET("/api/v2/items")
    fun searchArticles(@Query("query") query: String?, @Query("page") page: Int, @Query("per_page") perPage: Int): Deferred<List<Article>>

    @GET("/api/v2/users/Galaxy/stocks")
    fun getStocks(@Query("page") page: Int): Deferred<List<Article>>
}