package galaxy.qiitaviewer.domain.entity

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ErrorResponse : Serializable{

    @SerializedName("message")
    @Expose
    var message: String? = null

    @SerializedName("type")
    @Expose
    var type: String? = null
}