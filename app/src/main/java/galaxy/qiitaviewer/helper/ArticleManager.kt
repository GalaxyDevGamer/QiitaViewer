package galaxy.qiitaviewer.helper

import galaxy.qiitaviewer.domain.entity.Article
import galaxy.qiitaviewer.realm.Favourite

class ArticleManager {

    var article: MutableList<Article> = mutableListOf()
    var favourite: MutableList<Article> = Favourite.getFavourites()
    var stock: MutableList<Article> = mutableListOf()
    var lectures: MutableList<Article> = mutableListOf()

    var isFavouriteUpdated = false

    fun initialize() {

    }

    fun loadFavourites() {
        favourite = Favourite.getFavourites()
    }

    companion object {
        val instance = ArticleManager()
    }
}