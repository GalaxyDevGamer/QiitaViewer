package galaxy.qiitaviewer

import galaxy.qiitaviewer.activity.MainActivity

class ContextData {
    var mainActivity: MainActivity? = null

    companion object {
        val instance = ContextData()
    }
}