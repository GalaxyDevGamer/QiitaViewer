package galaxy.qiitaviewer.presentation.view

interface BrowserView {
    fun showMessage(message: String)

    fun updateMenu()

    fun showNeedToLogin()
}