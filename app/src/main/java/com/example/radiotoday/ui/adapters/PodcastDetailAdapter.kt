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
import com.example.radiotoday.data.models.SubContent
import com.example.radiotoday.ui.activities.PodcastDetailByCategoryActivity

class PodcastDetailAdapter(private val context: Context, private val listener: PodcastDetailByCategoryActivity, private val catName: String): RecyclerView.Adapter<PodcastDetailAdapter.SeeAllViewHolder>(){

    var seeAllPlaylistData : ArrayList<SubContent> = ArrayList()

    inner class SeeAllViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val posterImage: ImageView = itemView.findViewById(R.id.iv_ChildContent)
        val title: TextView = itemView.findViewById(R.id.title_textView)
        val description: TextView = itemView.findViewById(R.id.tvDescription)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PodcastDetailAdapter.SeeAllViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_seeall_content, parent, false)

        val viewHolder = SeeAllViewHolder(itemView)

        return viewHolder
    }

    override fun getItemCount(): Int {
        return seeAllPlaylistData.size
    }

    override fun onBindViewHolder(holder: SeeAllViewHolder, position: Int) {
        val playlistItem = seeAllPlaylistData[position]
        Glide.with(context)
            .load(playlistItem.image)
            .placeholder(R.drawable.no_img)
            .error(R.drawable.no_img)
            .into(holder.posterImage)


        holder.title.text = playlistItem.title
        holder.description.text = playlistItem.id

        holder.itemView.setOnClickListener {
            listener.onItemClickListener(position, playlistItem)
        }
    }

    interface ItemClickListener {
        fun onItemClickListener(position: Int, playlistItem: SubContent)

    }
}