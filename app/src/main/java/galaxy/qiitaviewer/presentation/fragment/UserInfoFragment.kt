package galaxy.qiitaviewer.presentation.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.*
import com.squareup.picasso.Picasso
import galaxy.qiitaviewer.R
import galaxy.qiitaviewer.application.App
import galaxy.qiitaviewer.domain.entity.UserInfo
import galaxy.qiitaviewer.helper.PreferenceHelper
import galaxy.qiitaviewer.presentation.presenter.UserInfoPresenter
import galaxy.qiitaviewer.presentation.view.UserInfoView
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
class UserInfoFragment : android.support.v4.app.Fragment(), UserInfoView {

    @Inject
    internal lateinit var presenter: UserInfoPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity?.application as App).appComponent.inject(this)
        presenter.view = this
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_info, container, false)
    }

    override fun onResume() {
        super.onResume()
        if (PreferenceHelper.instance.getUser() == null) showNothing() else launch(UI) { presenter.getUserInfo() }
    }

    override fun showNothing() {
        user_info_screen.visibility = View.GONE
        nothing.visibility = View.VISIBLE
        login.setOnClickListener { activity?.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.authorize_uri)))) }
    }

    override fun showUserInfo(userInfo: UserInfo) {
        user_info_screen.visibility = View.VISIBLE
        nothing.visibility = View.GONE
        Picasso.with(context).load(userInfo.profile_image).into(profile_image)
        user_id.text = userInfo.id
        description.text = userInfo.description
        logout.setOnClickListener { launch(UI) { presenter.deleteToken() } }
    }

    override fun showMessage(message: String) = Snackbar.make(view!!, message, Snackbar.LENGTH_LONG).show()

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.empty, menu)
    }

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