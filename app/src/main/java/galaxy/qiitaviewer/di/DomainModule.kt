package galaxy.qiitaviewer.di

import dagger.Module
import dagger.Provides
import galaxy.qiitaviewer.api.ArticleApi
import galaxy.qiitaviewer.api.AuthorizeApi
import galaxy.qiitaviewer.domain.usecase.ArticleUseCase
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Singleton

@Module
class DomainModule {

    @Provides
    @Singleton
    fun provideArticleApi(retrofit: Retrofit): ArticleApi = retrofit.create(ArticleApi::class.java)

    @Provides
    @Singleton
    fun provideAuthorizeApi(retrofit: Retrofit): AuthorizeApi = retrofit.create(AuthorizeApi::class.java)
}