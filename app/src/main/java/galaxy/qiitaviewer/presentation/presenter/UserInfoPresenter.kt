package galaxy.qiitaviewer.presentation.presenter

import android.content.Intent
import android.net.Uri
import galaxy.qiitaviewer.domain.usecase.AuthorizeUseCase
import galaxy.qiitaviewer.helper.PreferenceHelper
import galaxy.qiitaviewer.presentation.fragment.UserInfoFragment
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import javax.inject.Inject

class UserInfoPresenter @Inject constructor(private val useCase: AuthorizeUseCase) {

    var view: UserInfoFragment? = null

    fun login(uri: String) = view?.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(uri)))

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