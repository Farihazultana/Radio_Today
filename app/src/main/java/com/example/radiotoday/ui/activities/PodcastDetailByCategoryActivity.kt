package com.example.radiotoday.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.radiotoday.data.models.MainResponse
import com.example.radiotoday.data.models.SubContent
import com.example.radiotoday.databinding.ActivitySeeAllBinding
import com.example.radiotoday.ui.adapters.PodcastDetailAdapter
import com.example.radiotoday.ui.viewmodels.PodcastDetailByCategoryViewModel
import com.example.radiotoday.utils.ResultType
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PodcastDetailByCategoryActivity : AppCompatActivity(),
    PodcastDetailAdapter.ItemClickListener {
    private lateinit var binding: ActivitySeeAllBinding

    private lateinit var playlistData: MainResponse
    private lateinit var seeAllAdapter: PodcastDetailAdapter
    private val podcastDetailViewModel by viewModels<PodcastDetailByCategoryViewModel>()
    private lateinit var layoutManager: GridLayoutManager

    private lateinit var type: String

    private var isLoading = false
    private var isLastpage = false
    private var currentPage = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySeeAllBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolBarBackIcon.setOnClickListener {
            onBackPressed()
        }

        type = intent.getStringExtra("podcast_type")?: ""
        binding.tvToolBarTitle.text = type


        seeAllAdapter = PodcastDetailAdapter(this, this, "podcasts")
        binding.rvSeeAll.layoutManager = customGridLayoutManager(3)
        binding.rvSeeAll.adapter = seeAllAdapter


        observeSeeAllData()
        showSeeAll()

    }

    private fun customGridLayoutManager(span: Int): GridLayoutManager {
        layoutManager = GridLayoutManager(this, span)
        return layoutManager
    }

    private fun loadSeeAllData() {
        podcastDetailViewModel.fetchPodcastDetailByCategoryData(type, currentPage.toString())
    }

    private fun showSeeAll() {
        loadSeeAllData()  //Initial data load

        binding.rvSeeAll.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                if (!isLoading && !isLastpage) {
                    if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0) {
                        isLoading = true
                        //currentPage++
                        loadSeeAllData()
                    }
                }
            }
        })
    }

    private fun observeSeeAllData() {
        podcastDetailViewModel.podcastDetailByCategoryData.observe(this) {
            when (it) {
                is ResultType.Loading -> {
                    /*if (sectionCode == "promotions") {
                        binding.shimmerFrameLayoutRectangles.visibility = View.VISIBLE
                    } else {
                        binding.shimmerFrameLayoutSquare.visibility = View.VISIBLE
                    }*/

                    binding.shimmerFrameLayoutSquare.visibility = View.VISIBLE

                }

                is ResultType.Success -> {
                    binding.shimmerFrameLayoutSquare.visibility = View.GONE
                    //binding.shimmerFrameLayoutRectangles.visibility = View.GONE
                    playlistData = it.data
                    /*var playlistContent: ArrayList<SubContent> = ArrayList()

                    for (item in playlistData.content){
                        playlistContent = item.content
                    }*/
                    val playlistContent = playlistData.content.content

                    if (currentPage == 1) {
                        seeAllAdapter.seeAllPlaylistData = playlistContent as ArrayList<SubContent>


                    } else {
                        if (!seeAllAdapter.seeAllPlaylistData.containsAll(playlistContent)) {

                            seeAllAdapter.seeAllPlaylistData =
                                seeAllAdapter.seeAllPlaylistData.plus(playlistContent) as ArrayList<SubContent>


                        }
                    }


                    /*binding.tvToolBarTitle.text = seeAllTitle
                    Log.i("SeeAll", "observeSeeAllData: $seeAllTitle")*/


                    isLoading = false
                    //checking last page
                    isLastpage = playlistContent.isEmpty()
                    currentPage++
                    seeAllAdapter.notifyDataSetChanged()
                }

                is ResultType.Error -> {

                }
            }
        }
    }

    override fun onItemClickListener(position: Int, playlistItem: SubContent) {
        val intent = Intent(this, ShowDetailsActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        intent.putExtra("section_code", "podcasts")
        intent.putExtra("id", playlistItem.id)
        startActivity(intent)
    }
}