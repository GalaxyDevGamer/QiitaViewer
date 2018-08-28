package galaxy.qiitaviewer.callback

import galaxy.qiitaviewer.domain.entity.Article

/**
 * Created by galaxy on 2018/03/19.
 */
interface RecyclerListener {
    fun onClick(article: Article)
}