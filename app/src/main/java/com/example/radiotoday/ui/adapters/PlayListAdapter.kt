package com.example.radiotoday.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.radiotoday.R
import com.example.radiotoday.data.models.audio.PlaylistContent

class PlayListAdapter (private val playlist : ArrayList<PlaylistContent>) :
    RecyclerView.Adapter<PlayListAdapter.PlayListViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlayListViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_playlist, parent, false)
        return PlayListViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PlayListAdapter.PlayListViewHolder, position: Int) {

        val currentItem = playlist[position]
        holder.posterImage.setImageResource(currentItem.contentPoster)
        holder.title.text = currentItem.title
        holder.duration.text = currentItem.duration
        holder.timePlayedAgo.text = currentItem.playedAgoTime
        holder.stationName.text = currentItem.stationname
    }

    override fun getItemCount(): Int {
        return playlist.size
    }

    class PlayListViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        var posterImage : ImageView = itemView.findViewById(R.id.iv_playerPoster)
        var title : TextView = itemView.findViewById(R.id.tv_PlayingContent)
        var duration : TextView = itemView.findViewById(R.id.tv_duration)
        var timePlayedAgo : TextView = itemView.findViewById(R.id.tv_timePlayedAgo)
        var stationName : TextView = itemView.findViewById(R.id.tv_RadioStationName)
    }
}