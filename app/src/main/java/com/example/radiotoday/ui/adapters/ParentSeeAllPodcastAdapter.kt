package com.example.radiotoday.ui.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.models.SlideModel
import com.example.radiotoday.R
import com.example.radiotoday.data.models.ContentMain
import com.example.radiotoday.data.models.SubContent
import com.example.radiotoday.ui.activities.SeeAllActivity

class ParentSeeAllPodcastAdapter (private val listener: ItemClickListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), ChildSeeAllPodcastAdapter.ItemClickListener {

    var seeAllPodcastData: ArrayList<ContentMain> = ArrayList()


    private val TYPE_BANNER = 0
    private val TYPE_CONTENT = 1

    inner class BannerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageSlider: ImageSlider = itemView.findViewById(R.id.image_slider)
    }

    inner class ContentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var childListAdapter: ChildSeeAllPodcastAdapter
        val rvHor: RecyclerView = itemView.findViewById(R.id.rvHorizontal_Podcast)
        val title: TextView = itemView.findViewById(R.id.tv_Title)
        //val seeAll: TextView = itemView.findViewById(R.id.seeAll)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            TYPE_BANNER -> {
                val itemView = inflater.inflate(R.layout.item_parent_home_banner, parent, false)
                BannerViewHolder(itemView)
            }

            TYPE_CONTENT -> {
                val itemView =
                    inflater.inflate(R.layout.item_parent_seeallpodcast_content, parent, false)
                ContentViewHolder(itemView)
            }


            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun getItemCount(): Int {
        return seeAllPodcastData.size
    }

    override fun getItemViewType(position: Int): Int {
        val currentItem = seeAllPodcastData[position]
        return if (currentItem.contentviewtype == 4){
            TYPE_BANNER
        }else{
            TYPE_CONTENT
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentItem = seeAllPodcastData[position]

        when (holder) {
            is BannerViewHolder -> {
                val imageList: ArrayList<SlideModel> = ArrayList()
                for (item in currentItem.content) {
                    imageList.add(SlideModel(item?.image ?: "", item?.title ?: ""))
                }
                holder.imageSlider.setImageList(imageList)
            }

            is ContentViewHolder -> {
                holder.rvHor.visibility = View.VISIBLE

                val content = currentItem.content


                holder.childListAdapter = ChildSeeAllPodcastAdapter(
                    currentItem.contentviewtype,
                    content,
                    this
                )


                holder.rvHor.layoutManager = LinearLayoutManager(holder.rvHor.context, LinearLayoutManager.HORIZONTAL, false)

                holder.rvHor.adapter = holder.childListAdapter

                holder.title.text = currentItem.catCode

                /*holder.seeAll.setOnClickListener {
                    val intent = Intent(holder.itemView.context, SeeAllActivity::class.java)
                    intent.putExtra("catname", currentItem.section_code)
                    intent.putExtra("name", currentItem.name)
                    intent.putExtra("contenttype", currentItem.contenttype)
                    holder.itemView.context.startActivity(intent)
                }*/
            }
        }
    }



    override fun onItemClickListener(position: Int, currentItem: SubContent, currentSection: String) {
        listener.onItemClickListener(position, currentItem, currentSection)
    }

    interface ItemClickListener {
        fun onItemClickListener(position: Int, currentItem: SubContent, currentSection: String)

    }
}