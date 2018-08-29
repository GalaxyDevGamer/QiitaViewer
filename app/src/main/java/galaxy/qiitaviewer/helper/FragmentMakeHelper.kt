package galaxy.qiitaviewer.helper

import galaxy.qiitaviewer.presentation.fragment.*
import galaxy.qiitaviewer.type.FragmentType

class FragmentMakeHelper {
    companion object {
        fun makeFragment(fragmentType: FragmentType, any: Any) = when (fragmentType) {
            FragmentType.HOME -> HomeFragment.newInstance()
            FragmentType.ARTICLE -> ArticleFragment.newInstance()
            FragmentType.SEARCH -> SearchFragment.newInstance()
            FragmentType.BROWSER -> WebViewFragment.newInstance(any)
            FragmentType.USERINFO -> UserInfoFragment.newInstance()
        }
    }
}