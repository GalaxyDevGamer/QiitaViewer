package galaxy.qiitaviewer.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import galaxy.qiitaviewer.R
import galaxy.qiitaviewer.callback.RecyclerListener
import galaxy.qiitaviewer.data.Article
import galaxy.qiitaviewer.helper.ArticleManager
import kotlinx.android.synthetic.main.article_cell.view.*

/**
 * [RecyclerView.Adapter] that can display a [Article] and makes a call to the
 * specified [RecyclerListener].
 */
class StockAdapter(val context: Context, private val listener: RecyclerListener) : RecyclerView.Adapter<StockAdapter.ViewHolder>() {

    var list = ArticleManager.instance.stock

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.article_cell, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        Picasso.with(context).load(item.user.profile_image_url).into(holder.thumbnail)
        holder.title.text = item.title
        holder.itemView.setOnClickListener {
            listener.onClick(item)
        }
    }

    override fun getItemCount(): Int = list.size

    inner class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        val thumbnail: ImageView = mView.article_thumbnail
        val title: TextView = mView.article_title
    }
}
