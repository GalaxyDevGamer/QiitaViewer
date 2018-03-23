package galaxy.qiitaviewer.application

import android.app.Application
import io.realm.Realm

/**
 * Created by galaxy on 2018/03/21.
 */
class AppManager : Application() {

    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
    }
}