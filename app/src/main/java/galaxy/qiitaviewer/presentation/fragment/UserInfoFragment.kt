package galaxy.qiitaviewer.presentation.fragment

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import galaxy.qiitaviewer.R
import galaxy.qiitaviewer.application.App
import galaxy.qiitaviewer.helper.PreferenceHelper
import galaxy.qiitaviewer.presentation.presenter.UserInfoPresenter
import kotlinx.android.synthetic.main.fragment_user_info.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 * Use the [UserInfoFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class UserInfoFragment : android.support.v4.app.Fragment() {

    @Inject
    internal lateinit var presenter: UserInfoPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity?.application as App).appComponent.inject(this)
        presenter.view = this
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_info, container, false)
    }

    override fun onResume() {
        super.onResume()
        checkUser()
    }

    fun checkUser() = if (PreferenceHelper.instance.getUser() == null) showNothing() else showUserInfo()

    fun showNothing() {
        user_info_screen.visibility = View.GONE
        nothing.visibility = View.VISIBLE
        login.setOnClickListener { presenter.login() }
    }

    fun showUserInfo() {
        user_info_screen.visibility = View.VISIBLE
        nothing.visibility = View.GONE
        updateInfo()
        update.setOnClickListener { presenter.update() }
        logout.setOnClickListener { launch(UI) { presenter.deleteToken() } }
    }

    fun updateInfo() {
        val preference = PreferenceHelper.instance.getPreference()
        Picasso.with(context).load(preference.getString("profile_image", null)).into(profile_image)
        user_id.text = preference.getString("id", null)
        description.text = preference.getString("description", null)
    }

    fun showMessage(message: String) = Snackbar.make(view!!, message, Snackbar.LENGTH_LONG).show()

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment UserInfoFragment.
         */
        @JvmStatic
        fun newInstance() = UserInfoFragment()
    }
}