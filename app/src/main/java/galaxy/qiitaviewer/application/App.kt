package galaxy.qiitaviewer.application

import android.app.Application
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import galaxy.qiitaviewer.di.*
import io.realm.Realm

/**
 * Created by galaxy on 2018/03/21.
 */
open class App : Application() {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
        appComponent = DaggerAppComponent.builder()
                .appModule(AppModule(this))
                .networkModule(NetworkModule())
                .domainModule(DomainModule())
                .build()
    }
}