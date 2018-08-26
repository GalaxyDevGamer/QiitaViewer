package galaxy.qiitaviewer.data

/**
 * Created by galaxy on 2018/03/19.
 */
data class Article (
    val id: String,
    val title: String,
    val body: String,
    val url: String,
    val user: User
)
