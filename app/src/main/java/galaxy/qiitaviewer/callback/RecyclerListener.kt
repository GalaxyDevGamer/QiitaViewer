package galaxy.qiitaviewer.callback

import galaxy.qiitaviewer.data.Article

/**
 * Created by galaxy on 2018/03/19.
 */
interface RecyclerListener {
    fun onClick(article: Article)
}