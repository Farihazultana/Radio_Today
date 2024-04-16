package com.example.radiotoday.ui.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.radiotoday.R
import com.example.radiotoday.data.models.SubContent
import org.w3c.dom.Text

class NewsAdapter (private val context: Context, private val cardClickListener: CardClickListener): RecyclerView.Adapter<NewsAdapter.NewsViewHolder>(){

    var newsData : ArrayList<SubContent> = ArrayList()

    inner class NewsViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val posterImage : ImageView = itemView.findViewById(R.id.ivNewsPoster)
        val description : TextView = itemView.findViewById(R.id.tv_Title)
        val date : TextView = itemView.findViewById(R.id.tv_Date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_audio, parent, false)

        val viewHolder = NewsViewHolder(itemView)

        itemView.setOnClickListener {
            cardClickListener.onCardClickListener(viewHolder.adapterPosition)
        }

        return viewHolder
    }

    override fun getItemCount(): Int {
        return newsData.size
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val playlistItem = newsData[position]
        Log.i("TAG", "onBindViewHolder: $playlistItem")

        Glide.with(context)
            .load(playlistItem.image)
            .placeholder(R.drawable.player_logo)
            .error(R.drawable.no_img)
            .into(holder.posterImage)
        holder.date.text = playlistItem.date
        holder.description.text = playlistItem.description
    }

    interface CardClickListener {
        fun onCardClickListener(position: Int)
    }
}
