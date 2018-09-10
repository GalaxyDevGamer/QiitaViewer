package galaxy.qiitaviewer.type

import galaxy.qiitaviewer.R

enum class FragmentType(val navigation: NavigationType, var title: String, val menu: Int) {
    HOME(NavigationType.USER, "Home", R.menu.home_menu),
    ARTICLE(NavigationType.USER, "Article", R.menu.home_menu),
    STOCKS(NavigationType.NONE, "Stocks", R.menu.empty),
    LECTURES(NavigationType.NONE, "Lectures", R.menu.empty),
    SEARCH(NavigationType.BACK, "Search", R.menu.search_menu),
    BROWSER(NavigationType.BACK, "BROWSER", R.menu.webview_menu),
    USERINFO(NavigationType.BACK, "User", R.menu.empty)
}