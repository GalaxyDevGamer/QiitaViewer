package galaxy.qiitaviewer.presentation.adapter

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
import galaxy.qiitaviewer.domain.entity.Article
import kotlinx.android.synthetic.main.article_cell.view.*
import java.util.*

class SearchAdapter(val context: Context?, private val listener: RecyclerListener) : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    var result: MutableList<Article> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.article_cell, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = result[position]
        Picasso.with(context).load(item.user.profile_image_url).into(holder.thumbnail)
        holder.title.text = item.title
        holder.itemView.setOnClickListener {
            listener.onClick(item)
        }
    }

    override fun getItemCount() = result.size

    fun update(results: List<Article>) {
        result.clear()
        result.addAll(results)
        notifyDataSetChanged()
    }

    inner class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        val thumbnail: ImageView = mView.article_thumbnail
        val title: TextView = mView.article_title
    }
}