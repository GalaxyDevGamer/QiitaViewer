package galaxy.qiitaviewer.callback

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import galaxy.qiitaviewer.data.Info
import retrofit2.Call
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by galaxy on 2018/03/19.
 */
interface QiitaClient {
    @GET("/api/v2/items")
    fun getArticles(@Query("page") page: Int, @Query("per_page") perPage: Int): Call<List<Info>>

    @GET("/api/v2/items")
    fun searchArticles(@Query("query") query: String?, @Query("page") page: Int, @Query("per_page") perPage: Int): Call<List<Info>>

    companion object {
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
    }
}