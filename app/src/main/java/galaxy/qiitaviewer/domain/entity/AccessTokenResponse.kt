package galaxy.qiitaviewer.domain.entity

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class AccessTokenResponse: Serializable {

    @SerializedName("client_id")
    @Expose
    var client_id: String? = null

    @SerializedName("token")
    @Expose
    var token: String? = null
}