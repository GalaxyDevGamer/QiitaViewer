package galaxy.qiitaviewer.domain.usecase

import galaxy.qiitaviewer.api.AuthorizeApi
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.withContext
import javax.inject.Inject

class AuthorizeUseCase @Inject constructor(private val authorizeApi: AuthorizeApi) {
    suspend fun authorize() = withContext(CommonPool) {

    }
}