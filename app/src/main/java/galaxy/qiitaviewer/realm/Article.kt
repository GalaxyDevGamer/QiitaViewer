package galaxy.qiitaviewer.realm

import galaxy.qiitaviewer.data.Info
import galaxy.qiitaviewer.data.User
import io.realm.Realm
import io.realm.RealmObject
import java.util.*

/**
 * Created by galaxy on 2018/03/20.
 */
open class Article : RealmObject() {
    var id: String? = null
    var title: String? = null
    var url: String? = null
    var body: String? = null
    var profileImageUrl: String? = null

    companion object {
        fun getFavourite(): List<Article> =
                Realm.getDefaultInstance().use {
                    //                    it.executeTransaction { realm ->
//                        realm.where(Article::class.java).findAll()
//                    }
                    it.where(Article::class.java).findAll()
                }

        fun favouriteToInfo(): List<Info> =
                Realm.getDefaultInstance().use {
                    val list: LinkedList<Info> = LinkedList()
                    it.where(Article::class.java).findAll().forEach {
                        list.add(0, Info(it.id!!, it.title!!, it.body!!, it.url!!, User(it.profileImageUrl!!)))
                    }
                    return list
                }
    }
}