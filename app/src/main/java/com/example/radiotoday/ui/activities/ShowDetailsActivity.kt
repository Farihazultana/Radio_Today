package com.example.radiotoday.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.radiotoday.data.models.showDetails.SimilarArtist
import com.example.radiotoday.databinding.ActivityShowDetailsBinding
import com.example.radiotoday.ui.adapters.ShowDetailsAdapter
import com.example.radiotoday.ui.viewmodels.ShowDetailsViewModel
import com.example.radiotoday.utils.ResultType
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShowDetailsActivity : AppCompatActivity(), ShowDetailsAdapter.CardClickListener {
    private lateinit var binding: ActivityShowDetailsBinding
    private lateinit var showDetailsAdapter: ShowDetailsAdapter
    private val showDetailsViewModel by viewModels<ShowDetailsViewModel> ()
    private lateinit var albumCode: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }

        showDetailsAdapter = ShowDetailsAdapter(this, this)
        binding.rvPlaylist.layoutManager = LinearLayoutManager(this)
        binding.rvPlaylist.adapter = showDetailsAdapter

        albumCode = intent.getStringExtra("ALBUM_CODE").toString()
        Log.i("albumCode", "albumCode: $albumCode")

        showDetailsViewModel.fetchShowDetailsPlaylistData(albumCode)
        showDetailsViewModel.showDetailsPlaylistData.observe(this){
            when(it){
                is ResultType.Loading -> {

                }
                is ResultType.Success -> {
                    val playlistData = it.data
                    showDetailsAdapter.showDetailsPlaylistData = playlistData[0].similarartist
                    this.showDetailsAdapter.notifyDataSetChanged()

                }

                else -> {}
            }
        }

    }

    override fun onCardClickListener(position: Int, playlistItem: SimilarArtist) {
        Toast.makeText(this, "coming Soon!", Toast.LENGTH_SHORT).show()
    }
}