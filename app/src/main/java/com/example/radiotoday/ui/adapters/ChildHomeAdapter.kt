package com.example.radiotoday.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.radiotoday.R
import com.example.radiotoday.data.models.home.Content

class ChildHomeAdapter(
    private var contentViewType: String,
    private var contentType : String,
    private var catName : String,
    private var contentData: List<Content>,
    private val listener: ItemClickListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val TYPE_CONTENT = 1
    val TYPE_CONTINUE_WATCHING = 3
    val Type_ARTIST = 33
    val TYPE_NEWRELEASE = 11
    val TYPE_BROADCAST = 22


    inner class ContentViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val image:ImageView? = itemView.findViewById(R.id.iv_ChildContent)
        val title: TextView? = itemView.findViewById(R.id.title_textView)
        val premiumTag : TextView? = itemView.findViewById(R.id.premiumTextView)
        val descriptionText: TextView? = itemView.findViewById(R.id.tvDescription)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)


        return when(viewType){
            TYPE_CONTENT -> {
                val itemView = inflater.inflate(R.layout.col_item_content_child_home, parent, false)
                ContentViewHolder(itemView)
            }
            TYPE_NEWRELEASE -> {
                val itemView = inflater.inflate(R.layout.col_item_previous_show_child_home,parent, false)
                ContentViewHolder(itemView)
            }
            Type_ARTIST -> {
                val itemView = inflater.inflate(R.layout.col_item_artist_child_home,parent, false)
                ContentViewHolder(itemView)
            }
            TYPE_BROADCAST -> {
                val itemView = inflater.inflate(R.layout.col_item_broadcast_child_home,parent, false)
                ContentViewHolder(itemView)
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun getItemCount(): Int {
        return contentData.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (contentViewType == "2"){
            TYPE_CONTINUE_WATCHING
        }else if (contentType == "1"){
            TYPE_NEWRELEASE
        }
        else if (contentType == "3"){
            Type_ARTIST
        }else if (catName == "Band"){
            TYPE_BROADCAST
        }
        else{
            TYPE_CONTENT
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentItem = contentData[position]

        if (holder is ContentViewHolder) {
            holder.title?.text = currentItem.albumname
            holder.descriptionText?.text = currentItem.artistname

            currentItem.contentid
            /*if (currentItem.contenttype == "playlist") {
                val drawableStart = R.drawable.ic_listicon
                holder.descriptionText?.setCompoundDrawablesWithIntrinsicBounds(
                    drawableStart, 0, 0, 0
                )
                holder.descriptionText?.text = "Episodes-${currentItem.epcount}"


            } else {
                val drawableStart = R.drawable.ic_clock
                holder.descriptionText?.setCompoundDrawablesWithIntrinsicBounds(
                    drawableStart, 0, 0, 0
                )
                holder.descriptionText?.text = currentItem.length2
            }

            if (currentItem.isfree == "0") {
                holder.premiumTag?.visibility = View.VISIBLE
            } else {
                holder.premiumTag?.visibility = View.GONE
            }*/

            holder.image?.let {
                Glide.with(it.context).load(currentItem.image_location)
                    .placeholder(R.drawable.no_img).into(holder.image)
            }

            holder.itemView.setOnClickListener {
                listener.onItemClickListener(position, currentItem)
            }
        }
    }

    interface ItemClickListener {
        fun onItemClickListener(position: Int, currentItem: Content)

    }

}