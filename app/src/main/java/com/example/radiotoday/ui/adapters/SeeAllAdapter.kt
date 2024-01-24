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
import com.example.radiotoday.data.models.seeAll.ContentSeeAll

class SeeAllAdapter (private val context: Context, private val listener: ItemClickListener, private val catName: String): RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    var seeAllPlaylistData : ArrayList<ContentSeeAll> = ArrayList()

    val TYPE_CONTENT = 1
    val TYPE_PROMOTIONS = 2
    val TYPE_ANNOUNCERS = 3
    inner class SeeAllViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val posterImage: ImageView = itemView.findViewById(R.id.iv_ChildContent)
        val title: TextView = itemView.findViewById(R.id.title_textView)
        val description: TextView = itemView.findViewById(R.id.tvDescription)
    }
    inner class PromotionsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var posterImage: ImageView = itemView.findViewById(R.id.ivSeeAlPoster)
        var title: TextView = itemView.findViewById(R.id.tvTitle)
        var description: TextView = itemView.findViewById(R.id.tvDescription)
    }

    inner class AnnouncersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val posterImage: ImageView = itemView.findViewById(R.id.iv_ChildContent)
        val title: TextView = itemView.findViewById(R.id.tvTitleAnnouncer)
        val description: TextView = itemView.findViewById(R.id.tvDescription)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        /*val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_seeall, parent, false)

        val viewHolder = SeeAllViewHolder(itemView)

        return viewHolder*/


        val inflater = LayoutInflater.from(parent.context)


        return when(viewType){
            TYPE_CONTENT -> {
                val itemView = inflater.inflate(R.layout.item_seeall_content, parent, false)
                SeeAllViewHolder(itemView)
            }
            TYPE_PROMOTIONS -> {
                val itemView = inflater.inflate(R.layout.item_seeall_promotion, parent, false)
                PromotionsViewHolder(itemView)
            }
            TYPE_ANNOUNCERS -> {
                val itemView = inflater.inflate(R.layout.item_seeall_announcers,parent, false)
                AnnouncersViewHolder(itemView)
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val playlistItem = seeAllPlaylistData[position]

        if (holder is SeeAllViewHolder){
            Glide.with(context)
                .load(playlistItem.image)
                .placeholder(R.drawable.no_img)
                .error(R.drawable.no_img)
                .into(holder.posterImage)

            holder.title.text = playlistItem.title
            holder.description.text = playlistItem.artists

            holder.itemView.setOnClickListener {
                listener.onItemClickListener(position, playlistItem)
            }
        }

        if(holder is PromotionsViewHolder){
            Glide.with(context)
                .load(playlistItem.image)
                .placeholder(R.drawable.no_img)
                .error(R.drawable.no_img)
                .into(holder.posterImage)

            holder.title.text = playlistItem.title
            holder.description.text = playlistItem.artists

            holder.itemView.setOnClickListener {
                listener.onItemClickListener(position, playlistItem)
            }
        }

        if(holder is AnnouncersViewHolder){
            Glide.with(context)
                .load(playlistItem.image)
                .placeholder(R.drawable.no_img)
                .error(R.drawable.no_img)
                .into(holder.posterImage)

            holder.title.text = playlistItem.title
            holder.description.text = playlistItem.artists

            holder.itemView.setOnClickListener {
                listener.onItemClickListener(position, playlistItem)
            }
        }


    }

    override fun getItemCount(): Int {
        return seeAllPlaylistData.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (catName == "promotions"){
            TYPE_PROMOTIONS
        }else if (catName == "rjs"){
            TYPE_ANNOUNCERS
        } else{
            TYPE_CONTENT
        }
    }

    interface ItemClickListener {
        fun onItemClickListener(position: Int, playlistItem: ContentSeeAll)

    }
}




