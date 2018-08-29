package galaxy.qiitaviewer.api

import galaxy.qiitaviewer.domain.entity.AccessTokenResponse
import galaxy.qiitaviewer.domain.entity.UserInfo
import kotlinx.coroutines.experimental.Deferred
import retrofit2.Call
import retrofit2.http.*

interface AuthorizeApi {
    @POST("access_tokens")
    fun getAccessToken(@Query("client_id")id: String, @Query("client_secret")secret:String,@Query("code")code:String): Deferred<AccessTokenResponse>

    @GET("authenticated_user")
    fun getUserInfo(@Header("Authorization") header: String): Call<UserInfo>

    @DELETE("access_tokens/{access_token}")
    fun deleteToken(@Path("access_token") token: String): Call<AccessTokenResponse>
}