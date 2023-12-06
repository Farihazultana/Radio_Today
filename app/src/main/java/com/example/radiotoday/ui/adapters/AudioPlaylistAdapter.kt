package com.example.radiotoday.ui.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.radiotoday.R
import com.example.radiotoday.data.models.audio.Content

class AudioPlaylistAdapter(private val cardClickListener : CardClickListener):
    RecyclerView.Adapter<AudioPlaylistAdapter.AudioPlaylistViewHolder>() {

    var audioPlaylistData : ArrayList<Content> = ArrayList()

    inner class AudioPlaylistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val posterImage : ImageView = itemView.findViewById(R.id.iv_playerPoster)
        val title : TextView = itemView.findViewById(R.id.tv_PlayingContent)
        val duration : TextView = itemView.findViewById(R.id.tv_duration)
        val playedAgoTime : TextView = itemView.findViewById(R.id.tv_timePlayedAgo)
        val stationName : TextView = itemView.findViewById(R.id.tv_RadioStationName)
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AudioPlaylistAdapter.AudioPlaylistViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_playlist, parent, false)

        return AudioPlaylistViewHolder(itemView)
    }

    override fun onBindViewHolder(
        holder: AudioPlaylistViewHolder,
        position: Int
    ) {
        val playlistItem = audioPlaylistData[position]
        Log.i("TAG", "onBindViewHolder: $playlistItem")

        holder.title.text = playlistItem.albumname
        holder.duration.text = playlistItem.duration
        holder.stationName.text = playlistItem.artistname
        holder.playedAgoTime.text = "0.00" //default as no 2nd duration time  is given in Api
    }

    override fun getItemCount(): Int {
        return audioPlaylistData.size
    }

    interface CardClickListener {
        fun onCardClickListener(position: Int)
    }
}