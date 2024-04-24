package com.example.facial.adapter

import android.app.Dialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.facial.data.entity.User
import com.example.facial.R
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date



class UserAdapter(private var list: List<User>) : RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    private lateinit var dialog: Dialog

    fun setDialog(dialog: Dialog){
        this.dialog = dialog
    }

    interface Dialog {
        fun onClick(position: Int)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var emotion: TextView = view.findViewById(R.id.emotion)
        var diary: TextView = view.findViewById(R.id.diary)
        var createdAt: TextView = view.findViewById(R.id.createdAt)
        var cardView: CardView = view.findViewById(R.id.cvNote)

        init {
            view.setOnClickListener {
                dialog.onClick(layoutPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_user, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = list[position]
        holder.emotion.text = user.emotion
        holder.diary.text = user.diary
        holder.createdAt.text = getFormattedDate(user.createdAt)

        // Change cardView color based on emotion
        val context = holder.itemView.context
        when (user.emotion) {
            "angry" -> holder.cardView.setCardBackgroundColor(context.getColor(R.color.angry_note))
            "sad" -> holder.cardView.setCardBackgroundColor(context.getColor(R.color.sad_note))
            else -> holder.cardView.setCardBackgroundColor(context.getColor(R.color.happy_note))
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun updateList(newList: List<User>) {
        list = newList
        notifyDataSetChanged()
    }

    private fun getFormattedDate(timestamp: Long): String {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        return dateFormat.format(Date(timestamp))
    }
}
