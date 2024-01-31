package com.example.radiotoday.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.radiotoday.R
import com.example.radiotoday.data.models.SubContent
import com.example.radiotoday.databinding.ActivityPodcastBinding
import com.example.radiotoday.ui.adapters.ParentSeeAllPodcastAdapter
import com.example.radiotoday.ui.viewmodels.SeeAllPodcastViewModel
import com.example.radiotoday.utils.ResultType
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PodcastActivity : AppCompatActivity(), ParentSeeAllPodcastAdapter.ItemClickListener {
    private lateinit var parentSeeAllPodcastAdapter: ParentSeeAllPodcastAdapter
    private val podcastViewModel by viewModels<SeeAllPodcastViewModel>()
    private lateinit var binding: ActivityPodcastBinding

    private lateinit var sectionCode: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPodcastBinding.inflate(layoutInflater)
        setContentView(binding.root)

        parentSeeAllPodcastAdapter = ParentSeeAllPodcastAdapter(this)
        binding.rvHomeMain.layoutManager = LinearLayoutManager(this)
        binding.rvHomeMain.adapter = parentSeeAllPodcastAdapter

        sectionCode = intent.getStringExtra("catname").toString()
        Log.i("Podcast", "onCreate: $sectionCode")

        podcastViewModel.fetchSeeAllPodcastData(sectionCode)
        podcastViewModel.seeAllPodcastData.observe(this) {
            when (it) {
                is ResultType.Loading -> {

                }

                is ResultType.Success -> {

                    val podcastData = it.data.content
                    parentSeeAllPodcastAdapter.seeAllPodcastData = podcastData
                    this.parentSeeAllPodcastAdapter.notifyDataSetChanged()

                    binding.tvToolBarTitle.text = sectionCode

                }

                else -> {}
            }
        }
    }

    override fun onItemClickListener(
        position: Int,
        currentItem: SubContent,
        currentSection: String
    ) {
        val intent = Intent(this, ShowDetailsActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        intent.putExtra("section_code", currentSection)
        intent.putExtra("id", currentItem.id)
        startActivity(intent)
    }
}