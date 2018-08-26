package galaxy.qiitaviewer.callback

import android.util.Log
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import galaxy.qiitaviewer.data.Article
import galaxy.qiitaviewer.helper.ArticleManager
import kotlinx.android.synthetic.main.fragment_recyclerview.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
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
        private var retrofit = retrofit2.Retrofit.Builder().addConverterFactory(GsonConverterFactory.create(GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create())).baseUrl("https://qiita.com/").build()

        val api = retrofit.create(QiitaClient::class.java)

        fun create(): QiitaClient {
//            val retrofit = retrofit2.Retrofit.Builder().apply {
//                baseUrl("https://qiita.com/")
//                addConverterFactory(GsonConverterFactory.create())
//            }.build()
            val retrofit = retrofit2.Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create(GsonBuilder()
                            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                            .create()))
                    .baseUrl("https://qiita.com/").build()
            return retrofit.create(QiitaClient::class.java)
        }

        fun Encode(query: String?) = try {
            // url encode
            val encode_query = URLEncoder.encode(query, "UTF-8")
            encode_query
        } catch (e: UnsupportedEncodingException) {
            Log.e("", e.toString(), e)
            null
        }

        fun getArticle(page: Int, listener: OnRequestComplete) {
            api.getArticles(page, 10).enqueue(object : Callback<List<Article>> {
                override fun onResponse(call: Call<List<Article>>?, response: Response<List<Article>>?) {
                    Log.e("count", response?.body()?.count().toString())
                    listener.onComplete(response?.body()!!)
                }

                override fun onFailure(call: Call<List<Article>>?, t: Throwable?) {
                }
            })
        }

        fun search(query: String?, page: Int, listener: OnRequestComplete) {
            api.searchArticles(query, page, 10).enqueue(object : Callback<List<Article>> {
                override fun onResponse(call: Call<List<Article>>?, response: Response<List<Article>>?) {
                    listener.onComplete(response?.body()!!)
                }

                override fun onFailure(call: Call<List<Article>>?, t: Throwable?) {
                }
            })
        }

        fun getStock(page: Int, listener: OnRequestComplete) {
            api.getStocks(page).enqueue(object : Callback<List<Article>> {
                override fun onResponse(call: Call<List<Article>>, response: Response<List<Article>>) {
                    listener.onComplete(response.body()!!)
                }

                override fun onFailure(call: Call<List<Article>>, t: Throwable) {

                }
            })
        }
    }
}