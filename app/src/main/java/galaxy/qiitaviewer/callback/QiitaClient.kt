package galaxy.qiitaviewer.callback

import android.util.Log
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import galaxy.qiitaviewer.domain.entity.Article
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.io.UnsupportedEncodingException
import java.net.URLEncoder

/**
 * Created by galaxy on 2018/03/19.
 */
interface QiitaClient {
    @GET("/api/v2/items")
    fun getArticles(@Query("page") page: Int, @Query("per_page") perPage: Int): Call<List<Article>>

    @GET("/api/v2/items")
    fun searchArticles(@Query("query") query: String?, @Query("page") page: Int, @Query("per_page") perPage: Int): Call<List<Article>>

    @GET("/api/v2/users/Galaxy/stocks")
    fun getStocks(@Query("page") page: Int): Call<List<Article>>

    companion object {
        fun create(): QiitaClient {
            val retrofit = retrofit2.Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create(GsonBuilder()
                            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                            .create()))
                    .baseUrl("https://qiita.com/").build()
            return retrofit.create(QiitaClient::class.java)
        }

        fun getArticle(page: Int) {
            create().getArticles(page, 10).enqueue(object : Callback<List<Article>> {
                override fun onResponse(call: Call<List<Article>>?, response: Response<List<Article>>?) {
//                    listener.onComplete(response?.body()!!)
                }

                override fun onFailure(call: Call<List<Article>>?, t: Throwable?) {
                }
            })
        }

        fun search(query: String?, page: Int) {
            create().searchArticles(query, page, 10).enqueue(object : Callback<List<Article>> {
                override fun onResponse(call: Call<List<Article>>?, response: Response<List<Article>>?) {
//                    listener.onComplete(response?.body()!!)
                }

                override fun onFailure(call: Call<List<Article>>?, t: Throwable?) {
                }
            })
        }

        fun getStock(page: Int) {
            create().getStocks(page).enqueue(object : Callback<List<Article>> {
                override fun onResponse(call: Call<List<Article>>, response: Response<List<Article>>) {
//                    listener.onComplete(response.body()!!)
                }

                override fun onFailure(call: Call<List<Article>>, t: Throwable) {

                }
            })
        }
    }
}