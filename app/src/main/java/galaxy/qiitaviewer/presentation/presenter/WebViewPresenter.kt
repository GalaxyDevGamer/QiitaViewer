package galaxy.qiitaviewer.presentation.presenter

import android.content.Context
import android.support.v4.content.ContextCompat
import android.util.Log
import galaxy.qiitaviewer.R
import galaxy.qiitaviewer.domain.entity.Article
import galaxy.qiitaviewer.domain.entity.StockResponse
import galaxy.qiitaviewer.domain.usecase.ArticleUseCase
import galaxy.qiitaviewer.helper.ArticleManager
import galaxy.qiitaviewer.presentation.fragment.WebViewFragment
import galaxy.qiitaviewer.realm.Favourite
import io.realm.Realm
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class WebViewPresenter @Inject constructor(private val  useCase: ArticleUseCase) {

    var view: WebViewFragment? = null
    var id: String? = null
    var isStocked = false
    var isLiked = false

    fun isFavourite() = Realm.getDefaultInstance().where(Favourite::class.java).equalTo("id", id).count() > 0

    fun getStates() = launch(UI) {
        useCase.isStocked(id!!).let {
            if (it.isSuccessful)
                isStocked = true
        }
        useCase.isLiked(id!!).let {
            if (it.isSuccessful)
                isLiked = true
        }
        view?.activity?.invalidateOptionsMenu()
    }

    fun getDrawable(context: Context) = if (isFavourite()) ContextCompat.getDrawable(context, R.mipmap.ic_star_black_24dp)!! else ContextCompat.getDrawable(context, R.mipmap.ic_star_border_black_24dp)!!

    fun stockImage(context: Context) = if (isStocked) ContextCompat.getDrawable(context, R.mipmap.baseline_folder_black_24) else ContextCompat.getDrawable(context, R.mipmap.baseline_folder_open_black_24)

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

    fun like() = launch(UI) {
        val response = if (isLiked) useCase.unlikeThisArticle(id!!) else useCase.likeThisArticle(id!!)
        if (response.isSuccessful) {
            isLiked = !isLiked
            view?.showMessage(if (isStocked) "Liked" else "unLiked")
            view?.activity?.invalidateOptionsMenu()
        } else
            view?.showMessage("Operation failed")
    }

    fun stock() = launch(UI) {
        val response = if (isStocked) useCase.unStockArticle(id!!) else useCase.stockArticle(id!!)
        if (response.isSuccessful) {
            if (response.code() == 204) {
                isStocked = !isStocked
                view?.showMessage(if (isStocked) "Stocked" else "unStocked")
                view?.activity?.invalidateOptionsMenu()
            }
        } else
            view?.showMessage("Operation failed")
    }
}