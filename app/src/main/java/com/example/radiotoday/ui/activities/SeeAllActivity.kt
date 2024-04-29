package com.example.radiotoday.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.radiotoday.data.models.SubContent
import com.example.radiotoday.data.models.seeAll.SeeAllResponse
import com.example.radiotoday.databinding.ActivitySeeAllBinding
import com.example.radiotoday.ui.adapters.ParentHomeAdapter
import com.example.radiotoday.ui.adapters.SeeAllAdapter
import com.example.radiotoday.ui.fragments.PlayerFragment
import com.example.radiotoday.ui.interfaces.HomeItemClickListener
import com.example.radiotoday.ui.viewmodels.SeeAllPodcastViewModel
import com.example.radiotoday.ui.viewmodels.SeeAllViewModel
import com.example.radiotoday.utils.ResultType
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SeeAllActivity : AppCompatActivity(), SeeAllAdapter.ItemClickListener, HomeItemClickListener {

    private lateinit var playlistData: SeeAllResponse
    private lateinit var binding: ActivitySeeAllBinding
    private lateinit var seeAllAdapter: SeeAllAdapter
    private lateinit var parentSeeAllPodcastAdapter: ParentHomeAdapter
    private val seeAllViewModel by viewModels<SeeAllViewModel>()
    private val podcastViewModel by viewModels<SeeAllPodcastViewModel>()
    private lateinit var layoutManager: GridLayoutManager
    private lateinit var sectionCode: String
    private lateinit var seeAllTitle: String
    private lateinit var contentType: String

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

        sectionCode = intent.getStringExtra("catname").toString()
        Log.i("section", "onCreate: $sectionCode")
        seeAllTitle = intent.getStringExtra("name").toString()
        contentType = intent.getStringExtra("contenttype").toString()


        //SeeAll Adapter & recyclerview
        seeAllAdapter = SeeAllAdapter(this, this, sectionCode)
        if (sectionCode == "promotions") {
            binding.rvSeeAll.layoutManager = customGridLayoutManager(2)
        } else {
            binding.rvSeeAll.layoutManager = customGridLayoutManager(3)
        }
        binding.rvSeeAll.adapter = seeAllAdapter


        //SeeAllPodcast Adapter & recyclerview
        if (sectionCode == "podcasts"){
            parentSeeAllPodcastAdapter = ParentHomeAdapter(this)
            binding.rvSeeAll.layoutManager = LinearLayoutManager(this)
            binding.rvSeeAll.adapter = parentSeeAllPodcastAdapter
        }


        if (sectionCode == "podcasts"){
            observeSeeAllPodcastData()
        }else{
            observeSeeAllData()
        }
        if (sectionCode.isNotEmpty()) {
            if(sectionCode == "podcasts"){
                showSeeAllPodcast()
            }else{
                showSeeAll()
            }

        }

    }

    private fun showSeeAllPodcast() {
        podcastViewModel.fetchSeeAllPodcastData(sectionCode)
    }

    private fun observeSeeAllPodcastData() {
        podcastViewModel.seeAllPodcastData.observe(this) {
            when (it) {
                is ResultType.Loading -> {

                }

                is ResultType.Success -> {

                    val podcastData = it.data.content
                    parentSeeAllPodcastAdapter.homeData = podcastData
                    this.parentSeeAllPodcastAdapter.notifyDataSetChanged()

                    binding.tvToolBarTitle.text = seeAllTitle

                }

                else -> {}
            }
        }
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
                    if (visibleItemCount + firstVisibleItemPosition >= totalItemCount
                        && firstVisibleItemPosition >= 0
                    ) {
                        isLoading = true
                        //currentPage++
                        loadSeeAllData()
                    }
                }
            }
        })
    }

    private fun customGridLayoutManager(span: Int): GridLayoutManager {
        layoutManager = GridLayoutManager(this, span)
        return layoutManager
    }

    private fun loadSeeAllData() {
        Log.i("catname", "loadSeeAllData: $sectionCode")
        seeAllViewModel.fetchSeeAllData(sectionCode, currentPage.toString())

    }

    private fun observeSeeAllData() {
        seeAllViewModel.seeAllData.observe(this) {
            when (it) {
                is ResultType.Loading -> {
                    if (sectionCode == "promotions") {
                        binding.shimmerFrameLayoutRectangles.visibility = View.VISIBLE
                    } else {
                        binding.shimmerFrameLayoutSquare.visibility = View.VISIBLE
                    }

                }

                is ResultType.Success -> {
                    binding.shimmerFrameLayoutSquare.visibility = View.GONE
                    binding.shimmerFrameLayoutRectangles.visibility = View.GONE
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

                            seeAllAdapter.seeAllPlaylistData = seeAllAdapter.seeAllPlaylistData.plus(playlistContent) as ArrayList<SubContent>



                        }
                    }


                    binding.tvToolBarTitle.text = seeAllTitle
                    Log.i("SeeAll", "observeSeeAllData: $seeAllTitle")


                    isLoading = false
                    //checking last page
                    isLastpage = playlistContent.isEmpty()
                    currentPage++
                    seeAllAdapter.notifyDataSetChanged()
                }

                else -> {}
            }
        }
    }

    override fun onItemClickListener(position: Int, playlistItem: SubContent) {

        if (sectionCode == "songs"){
            val songsFragment = PlayerFragment()
            songsFragment.show(supportFragmentManager,songsFragment.tag)
        }else{
            val intent = Intent(this, ShowDetailsActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            intent.putExtra("section_code", sectionCode)
            intent.putExtra("id", playlistItem.id)
            startActivity(intent)
        }



    }

    override fun onHomeItemClickListener(position: Int, currentItem: SubContent, currentSection: String) {
        val intent = Intent(this, ShowDetailsActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        intent.putExtra("section_code", sectionCode)
        intent.putExtra("id", currentItem.id)
        startActivity(intent)

    }


}