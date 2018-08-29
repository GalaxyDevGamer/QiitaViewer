package galaxy.qiitaviewer.domain.usecase

import galaxy.qiitaviewer.api.AuthorizeApi
import galaxy.qiitaviewer.helper.PreferenceHelper
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.withContext
import ru.gildor.coroutines.retrofit.awaitResponse
import javax.inject.Inject

class AuthorizeUseCase @Inject constructor(private val authorizeApi: AuthorizeApi) {
    suspend fun authorize(code: String) = withContext(CommonPool) {
        authorizeApi.getAccessToken("bc3deb1194eff0ce4fd62e4d9e0e9fc628f942ea", "fd50c24ed944996c3aa68c2ae0f7cd696dbb7ff9", code).await()
    }

    suspend fun getUserInfo() = withContext(CommonPool) {
        authorizeApi.getUserInfo(PreferenceHelper.instance.getAccessToken()).awaitResponse()
    }

    suspend fun deleteToken() = withContext(CommonPool) {
        authorizeApi.deleteToken(PreferenceHelper.instance.accessToken()).awaitResponse()
    }
}