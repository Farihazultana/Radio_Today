package com.example.radiotoday.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.example.radiotoday.R
import com.example.radiotoday.data.models.seeMore.Similarartist
import com.example.radiotoday.databinding.ActivitySeeMoreBinding
import com.example.radiotoday.ui.adapters.SeeMoreAdapter
import com.example.radiotoday.ui.viewmodels.SeeMoreViewModel
import com.example.radiotoday.utils.BlurTransformation
import com.example.radiotoday.utils.ResultType
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SeeMoreActivity : AppCompatActivity(), SeeMoreAdapter.CardClickListener {
    private lateinit var binding: ActivitySeeMoreBinding
    private lateinit var seeMoreAdapter: SeeMoreAdapter
    private val seeMoreViewModel by viewModels<SeeMoreViewModel> ()
    private lateinit var albumCode: String
    private lateinit var titleImg: String
    private lateinit var title : String
    private lateinit var subTitle : String
    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySeeMoreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        seeMoreAdapter = SeeMoreAdapter(this, this)
        binding.rvPlaylist.layoutManager = LinearLayoutManager(this)
        binding.rvPlaylist.adapter = seeMoreAdapter

        albumCode = intent.getStringExtra("ALBUM_CODE").toString()
        Log.i("albumCode", "albumCode: $albumCode")


        Glide.with(this)
            .load(R.drawable.album_cover)
            .apply(RequestOptions.bitmapTransform(BlurTransformation(20, 2)))
            .placeholder(R.drawable.album_cover)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(binding.ivBlurBG)
        /*titleImg = intent.getStringExtra("TITLE_IMG").toString()
        title = intent.getStringExtra("TITLE").toString()
        subTitle = intent.getStringExtra("SUBTITLE").toString()


        Glide.with(this)
            .load(titleImg)
            .placeholder(R.drawable.no_img)
            .fitCenter()
            .error(R.drawable.no_img)
            .into(binding.ivTitlePoster)

        binding.tvTitle.text = title
        binding.tvSubTitle.text = subTitle*/

        seeMoreViewModel.fetchSeeMorePlaylistData(albumCode)
        seeMoreViewModel.seeMorePlaylistData.observe(this){
            when(it){
                is ResultType.Loading -> {

                }
                is ResultType.Success -> {
                    val playlistData = it.data
                    seeMoreAdapter.seeMorePlaylistData = playlistData[0].similarartist
                    this.seeMoreAdapter.notifyDataSetChanged()




                }

                else -> {}
            }
        }
    }

    override fun onCardClickListener(position: Int, playlistItem: Similarartist) {
        Toast.makeText(this, "coming Soon!", Toast.LENGTH_SHORT).show()
    }
}