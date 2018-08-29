package galaxy.qiitaviewer.domain.entity

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import javax.annotation.Generated

@Generated("org.jsonschema2pojo")
class UserInfo: Serializable {

    @SerializedName("description")
    @Expose
    var description: String? = null

    @SerializedName("id")
    @Expose
    var id: String? = null

    @SerializedName("items_count")
    @Expose
    var items = 0

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("permanent_id")
    @Expose
    var permanent_id = 0

    @SerializedName("profile_image_url")
    @Expose
    var profile_image: String? = null

    @SerializedName("image_monthly_upload_limit")
    @Expose
    var limit = 0

    @SerializedName("image_monthly_upload_remaining")
    @Expose
    var remaining = 0
}