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

class ShowDetailsAdapter(private val context: Context, private val cardClickListener : CardClickListener):
    RecyclerView.Adapter<ShowDetailsAdapter.ShowDetailsPlaylistViewHolder>()  {


    var showDetailsPlaylistData : ArrayList<SubContent> = ArrayList()
    inner class ShowDetailsPlaylistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val poster : ImageView = itemView.findViewById(R.id.ivShowdetailsPoster)
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

        /*Glide.with(context)
            .load(playlistItem.image)
            .placeholder(R.drawable.no_img)
            .error(R.drawable.no_img)
            .into(holder.poster)*/

        holder.title.text = playlistItem.title
        holder.duration.text = playlistItem.schedule
        holder.description.text = playlistItem.description

        holder.itemView.setOnClickListener {
            cardClickListener.onCardClickListener(position, playlistItem)
        }

    }

    override fun getItemCount(): Int {
        return showDetailsPlaylistData.size
    }

    interface CardClickListener {
        fun onCardClickListener(position: Int, playlistItem: SubContent)
    }
}