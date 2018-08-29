package galaxy.qiitaviewer.presentation.presenter

import android.support.v4.app.Fragment
import galaxy.qiitaviewer.activity.MainActivity
import galaxy.qiitaviewer.domain.usecase.AuthorizeUseCase
import galaxy.qiitaviewer.helper.FragmentMakeHelper
import galaxy.qiitaviewer.helper.PreferenceHelper
import galaxy.qiitaviewer.type.FragmentType
import javax.inject.Inject

class MainPresenter @Inject constructor(private val useCase: AuthorizeUseCase) {

    var view: MainActivity? = null

    val fragmentHistory = ArrayList<Fragment>()
    val fragmentTypeHistory = ArrayList<FragmentType>()

    fun changeFragment(fragmentType: FragmentType, any: Any, title: String) {
        val fragment = FragmentMakeHelper.makeFragment(fragmentType, any)
        fragmentType.title = title
        fragmentHistory.add(fragment)
        fragmentTypeHistory.add(fragmentType)
        view!!.replaceFragment(fragment)
        view?.updateToolbar()
    }

    suspend fun getAccessToken(code: String) = useCase.authorize(code).let { PreferenceHelper.instance.saveAccessToken(it.token!!) }

    suspend fun getUserInfo() = useCase.getUserInfo().let {
        if (it.isSuccessful)
            PreferenceHelper.instance.saveUserInfo(it.body()!!)
    }
}