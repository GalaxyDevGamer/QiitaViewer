package galaxy.qiitaviewer.presentation.presenter

import android.content.Context
import android.support.v4.content.ContextCompat
import galaxy.qiitaviewer.R
import galaxy.qiitaviewer.domain.entity.Article
import galaxy.qiitaviewer.domain.usecase.ArticleUseCase
import galaxy.qiitaviewer.helper.ArticleManager
import galaxy.qiitaviewer.presentation.fragment.WebViewFragment
import galaxy.qiitaviewer.realm.Favourite
import io.realm.Realm
import javax.inject.Inject

class WebViewPresenter @Inject constructor(private val  useCase: ArticleUseCase) {

    var view: WebViewFragment? = null
    var id: String? = null

    fun isFavourite() = Realm.getDefaultInstance().where(Favourite::class.java).equalTo("id", id).count() > 0

    fun getDrawable(context: Context) = if (isFavourite()) ContextCompat.getDrawable(context, R.mipmap.ic_star_black_24dp)!! else ContextCompat.getDrawable(context, R.mipmap.ic_star_border_black_24dp)!!

    fun deleteFavourite() = Realm.getDefaultInstance().executeTransaction { Realm.getDefaultInstance().where(Favourite::class.java).equalTo("id", id).findFirst()?.deleteFromRealm() }

    fun addToFavourite(article: Article) {
        val favourite = Favourite().apply {
            id = article.id
            title = article.title
            url = article.url
            body = article.body
            profileImageUrl = article.user.profile_image_url
        }
        Realm.getDefaultInstance().executeTransaction { it.insertOrUpdate(favourite) }
    }

    fun changeFavourite(article: Article) {
        ArticleManager.instance.isFavouriteUpdated =! ArticleManager.instance.isFavouriteUpdated
        if (isFavourite())
            deleteFavourite()
        else {
            addToFavourite(article)
        }
    }
}