package galaxy.qiitaviewer.di

import dagger.Module
import dagger.Provides
import galaxy.qiitaviewer.application.App
import galaxy.qiitaviewer.presentation.presenter.ArticlePresenter
import javax.inject.Singleton

@Module
class AppModule(private val app: App) {

    @Provides
    @Singleton
    fun provideApplication(): App = app
}