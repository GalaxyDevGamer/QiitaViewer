package galaxy.qiitaviewer.helper

import galaxy.qiitaviewer.presentation.fragment.*
import galaxy.qiitaviewer.type.FragmentType

class FragmentMakeHelper {
    companion object {
        fun makeFragment(fragmentType: FragmentType, any: Any) = when (fragmentType) {
            FragmentType.HOME -> HomeFragment.newInstance()
            FragmentType.ARTICLE -> ArticleFragment.newInstance()
            FragmentType.STOCKS -> StockFragment.newInstance()
            FragmentType.LECTURES -> LectureFragment.newInstance()
            FragmentType.SEARCH -> SearchFragment.newInstance()
            FragmentType.BROWSER -> BrowserFragment.newInstance(any)
            FragmentType.USERINFO -> UserInfoFragment.newInstance()
        }
    }
}