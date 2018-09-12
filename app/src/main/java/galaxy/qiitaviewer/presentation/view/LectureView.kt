package galaxy.qiitaviewer.presentation.view

interface LectureView {
    fun onComplete()

    fun showError(message: String)

    fun showLoading(state: Boolean)
}