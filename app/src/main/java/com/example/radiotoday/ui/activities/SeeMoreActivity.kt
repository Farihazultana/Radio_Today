package com.example.radiotoday.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.radiotoday.R
import com.example.radiotoday.databinding.ActivitySeeMoreBinding
import com.example.radiotoday.ui.adapters.AudioPlaylistAdapter
import com.example.radiotoday.ui.adapters.SeeMoreAdapter
import com.example.radiotoday.ui.viewmodels.SeeMoreViewModel
import com.example.radiotoday.utils.ResultType
import com.example.radiotoday.utils.SharedPreferencesUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SeeMoreActivity : AppCompatActivity(), SeeMoreAdapter.CardClickListener {
    private lateinit var binding: ActivitySeeMoreBinding
    private lateinit var seeMoreAdapter: SeeMoreAdapter
    private val seeMoreViewModel by viewModels<SeeMoreViewModel> ()
    private lateinit var albumCode: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySeeMoreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        seeMoreAdapter = SeeMoreAdapter(this, this)
        binding.rvPlaylist.layoutManager = LinearLayoutManager(this)
        binding.rvPlaylist.adapter = seeMoreAdapter

        albumCode = SharedPreferencesUtil.getData(this,"ALBUM_CODE", "").toString()
        Log.i("albumCode", "albumCode: $albumCode")

        seeMoreViewModel.fetchSeeMorePlaylistData("SNG00240")
        seeMoreViewModel.seeMorePlaylistData.observe(this){
            when(it){
                is ResultType.Loading -> {

                }
                is ResultType.Success -> {
                    val playlistData = it.data
                    seeMoreAdapter.seeMorePlaylistData = playlistData[0].similarartist
                    seeMoreAdapter.notifyDataSetChanged()
                }

                else -> {}
            }
        }
    }

    override fun onCardClickListener(position: Int) {

    }
}