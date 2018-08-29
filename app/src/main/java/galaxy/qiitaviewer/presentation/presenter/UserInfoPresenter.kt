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

    fun login() {
        val uri = "https://qiita.com/api/v2/oauth/authorize?" +
                "client_id=" + "bc3deb1194eff0ce4fd62e4d9e0e9fc628f942ea" +
                "&scope=" + "read_qiita+write_qiita" +
                "&state=" + "ab6s5adw121wsa2120ed7fe1"
        view?.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(uri)))
    }

    fun update() = launch(UI) { getInfo() }

    fun delete() = launch(UI) { deleteToken() }

    private suspend fun getInfo() = useCase.getUserInfo().let {
        if (it.isSuccessful) {
            PreferenceHelper.instance.saveUserInfo(it.body()!!)
            view!!.updateInfo()
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