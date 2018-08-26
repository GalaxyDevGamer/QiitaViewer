package galaxy.qiitaviewer.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import galaxy.qiitaviewer.R
import galaxy.qiitaviewer.callback.RecyclerListener
import galaxy.qiitaviewer.data.Article
import galaxy.qiitaviewer.helper.ArticleManager
import galaxy.qiitaviewer.realm.Favourite
import io.realm.Realm
import io.realm.RealmResults
import kotlinx.android.synthetic.main.article_cell.view.*

/**
 * Created by galaxy on 2018/03/19.
 */
class ArticleAdapter(val context: Context?, private val listener: RecyclerListener) : RecyclerView.Adapter<ArticleAdapter.ViewHolder>() {

    var list = ArticleManager.instance.artist

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.article_cell, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(list[position])
        holder.itemView.setOnClickListener { listener.onClick(list[position]) }
    }

    override fun getItemCount() = list.size

    fun addArticle(article: Article) {
        list.add(0, article)
        when (itemCount) {
            0 -> {
                list.add(article)
                notifyItemInserted(0)
            }
            1 -> {
                list.add(1, article)
                notifyItemInserted(1)
            }
            else -> {
                list.add(itemCount - 1, article)
                notifyItemInserted(itemCount - 1)
            }
        }
    }

    fun update(article: Article) {
        if (list.contains(article))
            return
        list.add(0, article)
        notifyItemInserted(0)
    }

    fun remove(article: Article, position: Int) {
        if (list.contains(article))
            return
        list.remove(article)
        notifyItemRemoved((itemCount - 1) - position)
    }

    /**
     * SwipeRefreshでリストの更新をする用に用意してあるけど、イマイチうまく行ってない
     * ここではまず今入ってるデータを最新データを比較して、無い物を取り除いてから新しく追加されたデータがあれば追加するようにしている
     */
    fun refresh(realm: Realm) {
        realm.beginTransaction()
        val result: RealmResults<Favourite> = realm.where(Favourite::class.java).findAllAsync()
        result.load()
        val articleInfo = realm.copyFromRealm(result)
        val cnt = 0
        do {

        } while (cnt < list.size - 1)
//        for (items: Article in articleInfo) {
//            val info = Info("", items.getTitle(), "", items.getUrl(), User(items.getProfileImageUrl()))
////            listにinfoが入ってるか確認
//            if (!list.contains(info))
//                list.remove(info)
//        }
//        for (items: Article in articleInfo) {
//            val info = Info("", items.getTitle(), "", items.getUrl(), User(items.getProfileImageUrl()))
//            if (!list.contains(info))
//                list.add(info)
//        }
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(item: Article) {
            val thumbnail = itemView.article_thumbnail
            val title = itemView.article_title
            title.text = item.title
            Picasso.with(context).load(item.user.profile_image_url).into(thumbnail)
        }
    }
}