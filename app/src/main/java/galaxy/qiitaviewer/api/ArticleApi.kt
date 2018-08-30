package galaxy.qiitaviewer.api

import galaxy.qiitaviewer.domain.entity.Article
import galaxy.qiitaviewer.domain.entity.LikeResponse
import galaxy.qiitaviewer.domain.entity.StockResponse
import kotlinx.coroutines.experimental.Deferred
import retrofit2.Call
import retrofit2.http.*

interface ArticleApi {
    @GET("items")
    fun getArticles(@Query("page") page: Int, @Query("per_page") perPage: Int): Call<List<Article>>

    @GET("items")
    fun searchArticles(@Query("query") query: String?, @Query("page") page: Int, @Query("per_page") perPage: Int): Call<List<Article>>

    @GET("users/{user_id}/stocks")
    fun getStocks(@Path("user_id") id: String, @Query("page") page: Int): Call<List<Article>>

    @GET("users/Galaxy/items")
    fun getLectures(@Query("page") page: Int, @Query("per_page") perPage: Int): Call<List<Article>>

    @GET("items/{item_id}/stock")
    fun isStocked(@Header("Authorization") header: String, @Path("item_id") id: String): Call<StockResponse>

    @PUT("items/{item_id}/stock")
    fun stockArticle(@Header("Authorization") header: String, @Path("item_id") id: String): Call<StockResponse>

    @DELETE("items/{item_id}/stock")
    fun unStockArticle(@Header("Authorization") header: String, @Path("item_id") id: String): Call<StockResponse>

    @GET("items/{item_id}/like")
    fun isLiked(@Header("Authorization") header: String, @Path("item_id") id: String): Call<LikeResponse>

    @PUT("items/{item_id}/like")
    fun likeThisArticle(@Header("Authorization") header: String, @Path("item_id") id: String): Call<LikeResponse>

    @DELETE("items/{item_id}/like")
    fun unlikeArticle(@Header("Authorization") header: String, @Path("item_id") id: String): Call<LikeResponse>
}