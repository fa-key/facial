package com.example.facial
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView

import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.facial.data.entity.User


class ArticleAdapter(
    var articles: ArrayList<ArticleModel.Data>,
    private val itemClickListener: OnItemClickListener
) : RecyclerView.Adapter<ArticleAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_article, parent, false)
    )

    override fun getItemCount() = articles.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val article = articles[position]
        holder.textArticle.text = article.title


        holder.itemView.setOnClickListener {
            itemClickListener?.onItemClick(position)
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textArticle = view.findViewById<TextView>(R.id.text_article)
    }

    public fun setDatas(data: List<ArticleModel.Data>) {
        articles.clear()
        articles.addAll(data)
        notifyDataSetChanged()
    }

    fun updateList(newList: ArrayList<ArticleModel.Data>) {
        articles = newList
        notifyDataSetChanged()
    }

}
