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
import com.example.radiotoday.data.models.showDetails.SimilarArtist

class ShowDetailsAdapter(private val context: Context, private val cardClickListener : CardClickListener):
    RecyclerView.Adapter<ShowDetailsAdapter.ShowDetailsPlaylistViewHolder>()  {


    var showDetailsPlaylistData : ArrayList<SimilarArtist> = ArrayList()
    inner class ShowDetailsPlaylistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title : TextView = itemView.findViewById(R.id.tvShowDetailsTitle)
        val duration : TextView = itemView.findViewById(R.id.tvDuration)
        val description : TextView = itemView.findViewById(R.id.tv_ShowDetailsDescription)
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ShowDetailsAdapter.ShowDetailsPlaylistViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_show_details, parent, false)

        val viewHolder = ShowDetailsPlaylistViewHolder(itemView)


        return viewHolder
    }

    override fun onBindViewHolder(holder: ShowDetailsAdapter.ShowDetailsPlaylistViewHolder, position: Int) {
        val playlistItem = showDetailsPlaylistData[position]

        holder.title.text = playlistItem.albumname
        holder.duration.text = playlistItem.release
        holder.description.text = playlistItem.artistname

        holder.itemView.setOnClickListener {
            cardClickListener.onCardClickListener(position, playlistItem)
        }

    }

    override fun getItemCount(): Int {
        return showDetailsPlaylistData.size
    }

    interface CardClickListener {
        fun onCardClickListener(position: Int, playlistItem: SimilarArtist)
    }
}