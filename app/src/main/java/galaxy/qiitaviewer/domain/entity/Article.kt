package galaxy.qiitaviewer.domain.entity

import java.io.Serializable

/**
 * Created by galaxy on 2018/03/19.
 */
class Article : Serializable {
    var id: String? = null
    var title: String? = null
    var body: String? = null
    var url: String? = null
    var user: User? = null

    inner class User : Serializable{
        var profile_image_url: String? = null
        var id: String? = null
    }
}
