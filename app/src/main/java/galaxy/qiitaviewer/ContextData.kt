package galaxy.qiitaviewer

import android.content.Context
import galaxy.qiitaviewer.activity.MainActivity

class ContextData {
    var mainActivity: MainActivity? = null
    var context: Context?= null

    companion object {
        val instance = ContextData()
    }
}