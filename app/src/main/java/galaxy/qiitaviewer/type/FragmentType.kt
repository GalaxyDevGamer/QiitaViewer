package galaxy.qiitaviewer.type

import galaxy.qiitaviewer.R

enum class FragmentType(val navigation: NavigationType, var title: String, val menu: Int) {
    HOME(NavigationType.NONE, "Home", R.menu.home_menu),
    ARTICLE(NavigationType.NONE, "Article", R.menu.empty),
    SEARCH(NavigationType.BACK, "Search", R.menu.search_menu),
    BROWSER(NavigationType.BACK, "BROWSER", R.menu.webview_menu),
    USERINFO(NavigationType.BACK, "User", R.menu.empty)
}