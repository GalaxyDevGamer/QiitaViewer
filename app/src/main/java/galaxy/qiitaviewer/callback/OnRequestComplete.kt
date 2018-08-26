package galaxy.qiitaviewer.callback

import galaxy.qiitaviewer.data.Article

interface OnRequestComplete {
    fun onComplete(results: List<Article>)
}