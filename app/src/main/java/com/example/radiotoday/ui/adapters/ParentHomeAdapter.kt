package com.example.radiotoday.ui.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.models.SlideModel
import com.example.radiotoday.R
import com.example.radiotoday.data.models.home.Content
import com.example.radiotoday.data.models.home.HomeResponseItem
import com.example.radiotoday.ui.activities.SeeAllActivity

class ParentHomeAdapter(private val context: Context, private val listener: ItemClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), ChildHomeAdapter.ItemClickListener{

    var homeData : List<HomeResponseItem> = ArrayList()


    private val TYPE_BANNER = 0
    private val TYPE_CONTENT = 1

    inner class BannerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageSlider : ImageSlider = itemView.findViewById(R.id.image_slider)
    }

    inner class ContentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        lateinit var childListAdapter: ChildHomeAdapter
        val rvHor: RecyclerView = itemView.findViewById(R.id.rvHorizontal_Home)
        val title: TextView = itemView.findViewById(R.id.tv_Title)
        val seeAll: ImageView = itemView.findViewById(R.id.seeAll)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when(viewType){
            TYPE_BANNER -> {
                val itemView = inflater.inflate(R.layout.row_item_banner_parent_home, parent, false)
                BannerViewHolder(itemView)
            }

            TYPE_CONTENT -> {
                val itemView = inflater.inflate(R.layout.row_item_content_parent_home, parent, false)
                ContentViewHolder(itemView)
            }


            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun getItemCount(): Int {
        return homeData.size
    }

    override fun getItemViewType(position: Int): Int {
        val currentItem = homeData[position]
        return if (currentItem.contentviewtype == "4"){
            TYPE_BANNER
        }else{
            TYPE_CONTENT
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentItem = homeData[position]
        var imageList:ArrayList<SlideModel> = ArrayList()

        when (holder){
            is BannerViewHolder -> {
                for(i in currentItem.contents){
                    imageList.add(SlideModel(i.image_location, i.catname))
                }
                holder.imageSlider.setImageList(imageList)
            }
            is ContentViewHolder -> {
                holder.rvHor.visibility = View.VISIBLE
                holder.childListAdapter = ChildHomeAdapter(currentItem.contentviewtype, currentItem.contenttype, currentItem.contents, this)
                holder.rvHor.layoutManager = LinearLayoutManager(holder.rvHor.context, LinearLayoutManager.HORIZONTAL,false)
                holder.title.text = currentItem.catname

                holder.rvHor.adapter = holder.childListAdapter

                if(currentItem.catcode == "recent"){
                    if(currentItem.contents.isEmpty()){
                        holder.seeAll.visibility = View.GONE
                    }else{
                        holder.seeAll.visibility = View.VISIBLE
                    }

                } else {
                    holder.seeAll.visibility = View.VISIBLE
                    holder.seeAll.setOnClickListener {
                        val intent = Intent(holder.itemView.context, SeeAllActivity::class.java)
                        intent.putExtra("catname", currentItem.catname)
                        intent.putExtra("contenttype", currentItem.contenttype)
                        holder.itemView.context.startActivity(intent)
                    }
                }

            }
        }
    }

    override fun onItemClickListener(position: Int, currentItem: Content) {
        listener.onItemClickListener(position, currentItem)
    }

    interface ItemClickListener {
        fun onItemClickListener(position: Int, currentItem: Content)

    }
}