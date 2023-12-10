package com.example.radiotoday.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.radiotoday.R
import com.example.radiotoday.data.models.seeAll.Content

class SeeAllAdapter (private val context: Context, private val listener: ItemClickListener): RecyclerView.Adapter<SeeAllAdapter.SeeAllViewHolder>(){

    var seeAllPlaylistData : ArrayList<Content> = ArrayList()
    inner class SeeAllViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        var posterImage: ImageView = itemView.findViewById(R.id.ivSeeAlPoster)
        var title: TextView = itemView.findViewById(R.id.tvTitle)
        var description: TextView = itemView.findViewById(R.id.tvDescription)

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeeAllViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_seeall, parent, false)

        val viewHolder = SeeAllViewHolder(itemView)

        return viewHolder
    }

    override fun onBindViewHolder(holder: SeeAllViewHolder, position: Int) {
        val playlistItem = seeAllPlaylistData[position]

        Glide.with(context)
            .load(playlistItem.image_location)
            .placeholder(R.drawable.no_img)
            .error(R.drawable.no_img)
            .into(holder.posterImage)

        holder.title.text = playlistItem.albumname
        holder.description.text = playlistItem.artistname

        holder.itemView.setOnClickListener {
            listener.onItemClickListener(position, playlistItem)
        }
    }

    override fun getItemCount(): Int {
        return seeAllPlaylistData.size
    }

    interface ItemClickListener {
        fun onItemClickListener(position: Int, playlistItem: Content)

    }
}




