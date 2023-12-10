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
import com.example.radiotoday.data.models.seeMore.Similarartist

class SeeMoreAdapter(private val context: Context, private val cardClickListener : CardClickListener):
    RecyclerView.Adapter<SeeMoreAdapter.SeeMorePlaylistViewHolder>()  {


    var seeMorePlaylistData : ArrayList<Similarartist> = ArrayList()
    inner class SeeMorePlaylistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val posterImage : ImageView = itemView.findViewById(R.id.iv_playerPoster)
        val title : TextView = itemView.findViewById(R.id.tv_PlayingContent)
        val duration : TextView = itemView.findViewById(R.id.tv_duration)
        val playedAgoTime : TextView = itemView.findViewById(R.id.tv_timePlayedAgo)
        val stationName : TextView = itemView.findViewById(R.id.tv_RadioStationName)
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SeeMoreAdapter.SeeMorePlaylistViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_playlist, parent, false)

        val viewHolder = SeeMorePlaylistViewHolder(itemView)


        return viewHolder
    }

    override fun onBindViewHolder(holder: SeeMoreAdapter.SeeMorePlaylistViewHolder, position: Int) {
        val playlistItem = seeMorePlaylistData[position]

        Glide.with(context)
            .load(playlistItem.image_location)
            .placeholder(R.drawable.no_img)
            .error(R.drawable.no_img)
            .into(holder.posterImage)
        holder.title.text = playlistItem.albumname
        holder.duration.text = playlistItem.release
        holder.stationName.text = playlistItem.artistname
        holder.playedAgoTime.text = "0.00"

        holder.itemView.setOnClickListener {
            cardClickListener.onCardClickListener(position, playlistItem)
        }

    }

    override fun getItemCount(): Int {
        return seeMorePlaylistData.size
    }

    interface CardClickListener {
        fun onCardClickListener(position: Int, playlistItem: Similarartist)
    }
}