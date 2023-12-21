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
import com.example.radiotoday.data.models.audio.Content

class VideoPlaylistAdapter(private val context: Context, private val cardClickListener : VideoPlaylistAdapter.CardClickListener):
    RecyclerView.Adapter<VideoPlaylistAdapter.VideoPlaylistViewHolder>()  {

    var videoPlaylistData : ArrayList<Content> = ArrayList()
    inner class VideoPlaylistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val posterImage : ImageView = itemView.findViewById(R.id.iv_playerPoster)
        val title : TextView = itemView.findViewById(R.id.tv_AudioTitle)
        val description : TextView = itemView.findViewById(R.id.tv_AudioDescription)
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): VideoPlaylistAdapter.VideoPlaylistViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_video, parent, false)

        val viewHolder = VideoPlaylistViewHolder(itemView)

        itemView.setOnClickListener {
            cardClickListener.onCardClickListener(viewHolder.adapterPosition)
        }

        return viewHolder
    }

    override fun onBindViewHolder(
        holder: VideoPlaylistAdapter.VideoPlaylistViewHolder,
        position: Int
    ) {
        val playlistItem = videoPlaylistData[position]
        Log.i("TAG", "onBindViewHolder: $playlistItem")

        Glide.with(context)
            .load(playlistItem.image_location)
            .placeholder(R.drawable.player_logo)
            .error(R.drawable.no_img)
            .into(holder.posterImage)
        holder.title.text = playlistItem.albumname
        holder.description.text = playlistItem.artistname
    }

    override fun getItemCount(): Int {
        return videoPlaylistData.size
    }

    interface CardClickListener {
        fun onCardClickListener(position: Int)
    }
}