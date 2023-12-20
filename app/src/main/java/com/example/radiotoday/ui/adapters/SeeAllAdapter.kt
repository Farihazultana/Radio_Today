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

class SeeAllAdapter (private val context: Context, private val listener: ItemClickListener, private val catName: String): RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    var seeAllPlaylistData : ArrayList<Content> = ArrayList()

    val TYPE_CONTENT = 1
    val TYPE_PODCAST = 22
    inner class SeeAllViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        var posterImage: ImageView = itemView.findViewById(R.id.ivSeeAlPoster)
        var title: TextView = itemView.findViewById(R.id.tvTitle)
        var description: TextView = itemView.findViewById(R.id.tvDescription)

    }

    inner class PodcastViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val posterImage: ImageView = itemView.findViewById(R.id.iv_ChildContent)
        val title: TextView = itemView.findViewById(R.id.title_textView)
        val description: TextView = itemView.findViewById(R.id.tvDescription)

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        /*val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_seeall, parent, false)

        val viewHolder = SeeAllViewHolder(itemView)

        return viewHolder*/


        val inflater = LayoutInflater.from(parent.context)


        return when(viewType){
            TYPE_CONTENT -> {
                val itemView = inflater.inflate(R.layout.item_seeall, parent, false)
                SeeAllViewHolder(itemView)
            }
            TYPE_PODCAST -> {
                val itemView = inflater.inflate(R.layout.item_seeall_podcast,parent, false)
                PodcastViewHolder(itemView)
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val playlistItem = seeAllPlaylistData[position]

        if (holder is SeeAllViewHolder){
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

        if(holder is PodcastViewHolder){
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


    }

    override fun getItemCount(): Int {
        return seeAllPlaylistData.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (catName == "Band"){
            TYPE_PODCAST
        }
        else{
            TYPE_CONTENT
        }
    }

    interface ItemClickListener {
        fun onItemClickListener(position: Int, playlistItem: Content)

    }
}




