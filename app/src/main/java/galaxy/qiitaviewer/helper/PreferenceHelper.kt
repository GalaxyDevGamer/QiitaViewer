package galaxy.qiitaviewer.helper

import android.content.Context
import galaxy.qiitaviewer.ContextData
import galaxy.qiitaviewer.domain.entity.UserInfo

class PreferenceHelper {

    fun isAuthorized() = ContextData.instance.context?.getSharedPreferences("setting", Context.MODE_PRIVATE)?.getString("access_token", null) != null

    fun getUser() = ContextData.instance.context!!.getSharedPreferences("setting", Context.MODE_PRIVATE).getString("id", null)

    fun getPreference() = ContextData.instance.context!!.getSharedPreferences("setting", Context.MODE_PRIVATE)

    fun accessToken() = ContextData.instance.context!!.getSharedPreferences("setting", Context.MODE_PRIVATE).getString("access_token", "")

    fun getAccessToken() = "Bearer " + ContextData.instance.context!!.getSharedPreferences("setting", Context.MODE_PRIVATE).getString("access_token", "")

    fun saveAccessToken(token: String) = ContextData.instance.context?.getSharedPreferences("setting", Context.MODE_PRIVATE)?.edit()?.putString("access_token", token)?.apply()

    fun getProfileImage() = ContextData.instance.context!!.getSharedPreferences("setting", Context.MODE_PRIVATE).getString("profile_image", null)

    fun deleteUser() = ContextData.instance.context!!.getSharedPreferences("setting", Context.MODE_PRIVATE).edit().apply {
        remove("access_token")
        remove("description")
        remove("id")
        remove("items")
        remove("name")
        remove("permanent_id")
        remove("profile_image")
        remove("image_limit")
        remove("image_remaining")
    }.apply()

    fun saveUserInfo(info: UserInfo) = ContextData.instance.context!!.getSharedPreferences("setting", Context.MODE_PRIVATE).edit().apply {
        putString("description", info.description)
        putString("id", info.id)
        putInt("items", info.items)
        putString("name", info.name)
        putInt("permanent_id", info.permanent_id)
        putString("profile_image", info.profile_image)
        putInt("image_limit", info.limit)
        putInt("image_remaining", info.remaining)
    }.apply()

    companion object {
        val instance = PreferenceHelper()
    }
}