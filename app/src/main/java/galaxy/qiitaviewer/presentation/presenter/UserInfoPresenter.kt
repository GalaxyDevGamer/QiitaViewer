package galaxy.qiitaviewer.presentation.presenter

import galaxy.qiitaviewer.domain.usecase.AuthorizeUseCase
import galaxy.qiitaviewer.helper.PreferenceHelper
import galaxy.qiitaviewer.presentation.view.UserInfoView
import javax.inject.Inject

class UserInfoPresenter @Inject constructor(private val useCase: AuthorizeUseCase) {

    var view: UserInfoView? = null

    suspend fun getUserInfo() = useCase.getUserInfo().let {
        if (it.isSuccessful) {
            PreferenceHelper.instance.saveUserInfo(it.body()!!)
            view!!.showUserInfo(it.body()!!)
        } else
            view!!.showMessage(it.message())
    }

    suspend fun deleteToken() = useCase.deleteToken().let {
        if (it.isSuccessful) {
            PreferenceHelper.instance.deleteUser()
            view?.showNothing()
        } else
            view?.showMessage(it.message())
    }
}