package galaxy.qiitaviewer.presentation.view

import galaxy.qiitaviewer.domain.entity.UserInfo

interface UserInfoView {
    fun showNothing()

    fun showUserInfo(userInfo: UserInfo)

    fun showMessage(message: String)
}