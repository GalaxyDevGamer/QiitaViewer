package galaxy.qiitaviewer.di

import dagger.Component
import galaxy.qiitaviewer.activity.MainActivity
import galaxy.qiitaviewer.presentation.fragment.*
import javax.inject.Singleton

@Component(modules = [
    AppModule::class,
    NetworkModule::class,
    DomainModule::class
])
@Singleton
interface AppComponent {

    fun inject(activity: MainActivity)

    fun inject(fragment: HomeFragment)

    fun inject(fragment: ArticleFragment)

    fun inject(fragment: StockFragment)

    fun inject(fragment: LectureFragment)

    fun inject(fragment: SearchFragment)

    fun inject(fragment: BrowserFragment)

    fun inject(fragment: UserInfoFragment)
}