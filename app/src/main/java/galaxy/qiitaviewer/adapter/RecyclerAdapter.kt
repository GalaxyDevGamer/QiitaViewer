package galaxy.qiitaviewer.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import galaxy.qiitaviewer.R
import galaxy.qiitaviewer.callback.RecyclerListener
import galaxy.qiitaviewer.data.Info
import galaxy.qiitaviewer.data.User
import galaxy.qiitaviewer.realm.Article
import io.realm.Realm
import io.realm.RealmResults
import java.util.*

/**
 * Created by galaxy on 2018/03/19.
 */
class RecyclerAdapter(val context: Context?, listener: RecyclerListener) : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    private var clickListener: RecyclerListener = listener
    private var list: LinkedList<Info> = LinkedList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_item, parent, false)
        Log.d("Adapter", "onCreate")
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(list[position])
        holder.itemView.setOnClickListener({ clickListener.onClick(position) })
        Log.d("Adapter", "onBindView")
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun add(info: Info) {
        list.add(0, info)
        notifyItemInserted(0)
    }

    fun addArticle(info: Info) {
        list.add(0, info)
        when (itemCount) {
            0 -> {
                list.add(info)
                notifyItemInserted(0)
            }
            1 -> {
                list.add(1, info)
                notifyItemInserted(1)
            }
            else -> {
                list.add(itemCount - 1, info)
                notifyItemInserted(itemCount - 1)
            }
        }
    }

    fun addList(info: List<Info>) {
        list.addAll(info)
        notifyDataSetChanged()
    }

    fun update(info: Info) {
        if (list.contains(info))
            return
        list.add(0, info)
        notifyItemInserted(0)
    }

    fun remove(info: Info, position: Int) {
        if (list.contains(info))
            return
        list.remove(info)
        notifyItemRemoved((itemCount - 1) - position)
    }

    fun clear() {
        list.clear()
//        notifyDataSetChanged()
    }

    fun getId(position: Int): String {
        return list[position].id
    }

    fun getTitle(position: Int): String {
        return list[position].title
    }

    fun getUrl(position: Int): String {
        return list[position].url
    }

    fun getBody(position: Int): String {
        return list[position].body
    }

    fun getImage(position: Int): String {
        return list[position].user.profile_image_url
    }

    /**
     * SwipeRefreshでリストの更新をする用に用意してあるけど、イマイチうまく行ってない
     * ここではまず今入ってるデータを最新データを比較して、無い物を取り除いてから新しく追加されたデータがあれば追加するようにしている
     */
    fun refresh(realm: Realm) {
        realm.beginTransaction()
        val result: RealmResults<Article> = realm.where(Article::class.java).findAllAsync()
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

        fun bindItems(item: Info) {
            val itemTitle = itemView.findViewById(R.id.item_title) as TextView
            val itemImage = itemView.findViewById(R.id.item_image) as ImageView
            itemTitle.text = item.title
            Picasso.with(context).load(item.user.profile_image_url).into(itemImage)
        }
    }
}