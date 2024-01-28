package com.example.radiotoday.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.radiotoday.data.models.SubContent
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
    private lateinit var sectionCode: String
    private lateinit var id: String
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

        sectionCode = intent.getStringExtra("section_code").toString()
        Log.i("showDetails", "albumCode: $sectionCode")
        id = intent.getStringExtra("id").toString()
        Log.i("showDetails", "id: $id")

        showDetailsViewModel.fetchShowDetailsPlaylistData(sectionCode,id, "")
        showDetailsViewModel.showDetailsPlaylistData.observe(this){
            when(it){
                is ResultType.Loading -> {
                    binding.shimmerFrameLayout.visibility = View.VISIBLE
                }
                is ResultType.Success -> {
                    val playlistData = it.data.content
                    showDetailsAdapter.showDetailsPlaylistData = arrayListOf<SubContent>(playlistData)
                    this.showDetailsAdapter.notifyDataSetChanged()

                    binding.shimmerFrameLayout.visibility = View.GONE

                }

                else -> {}
            }
        }

    }

    override fun onCardClickListener(position: Int, playlistItem: SubContent) {
        Toast.makeText(this, "coming Soon!", Toast.LENGTH_SHORT).show()
    }
}