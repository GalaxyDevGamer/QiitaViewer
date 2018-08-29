package galaxy.qiitaviewer.realm

import galaxy.qiitaviewer.domain.entity.Article
import io.realm.Realm
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

/**
 * Created by galaxy on 2018/03/20.
 */
open class Favourite : RealmObject() {
    @PrimaryKey
    var id: String? = null
    var title: String? = null
    var url: String? = null
    var body: String? = null
    var profileImageUrl: String? = null

    companion object {
        fun getFavourites(): LinkedList<Article> = Realm.getDefaultInstance().use {
            val list: LinkedList<Article> = LinkedList()
            it.where(Favourite::class.java).findAll().forEach {
                list.add(0, Article().apply {
                    id = it.id
                    title = it.title
                    body = it.body
                    url = it.url
                    user?.profile_image_url = it.profileImageUrl
                })
            }
            return list
        }
    }
}