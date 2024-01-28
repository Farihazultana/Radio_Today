package com.example.radiotoday.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.radiotoday.data.models.SubContent
import com.example.radiotoday.data.models.seeAll.SeeAllResponse
import com.example.radiotoday.databinding.ActivitySeeAllBinding
import com.example.radiotoday.ui.adapters.SeeAllAdapter
import com.example.radiotoday.ui.viewmodels.SeeAllViewModel
import com.example.radiotoday.utils.ResultType
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SeeAllActivity : AppCompatActivity(), SeeAllAdapter.ItemClickListener {

    private lateinit var playlistData: SeeAllResponse
    private lateinit var binding: ActivitySeeAllBinding
    private lateinit var seeAllAdapter: SeeAllAdapter
    private val seeAllViewModel by viewModels<SeeAllViewModel>()
    private lateinit var layoutManager: GridLayoutManager
    private lateinit var sectionCode: String
    private var id: String? = null
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


        seeAllAdapter = SeeAllAdapter(this, this, sectionCode)
        if (sectionCode == "promotions") {
            binding.rvSeeAll.layoutManager = CustomGridLayoutManager(2)
        } else {
            binding.rvSeeAll.layoutManager = CustomGridLayoutManager(3)
        }

        binding.rvSeeAll.adapter = seeAllAdapter


        observeSeeAllData()
        if (sectionCode.isNotEmpty()) {
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

    }

    private fun CustomGridLayoutManager(span: Int): GridLayoutManager {
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
                    val playlistContent = playlistData.content.content

                    if (currentPage == 1) {
                        seeAllAdapter.seeAllPlaylistData = playlistContent as ArrayList<SubContent>
                        /*if (contentType == "2") {
                            seeAllAdapter.seeAllPlaylistData = playlistData.content.content as ArrayList<ContentSeeAll>
                        } else {
                            Toast.makeText(this, "Coming Soon!", Toast.LENGTH_SHORT).show()
                        }*/
                        for (item in playlistContent){
                            id = item.id
                        }
                    } else {
                        if (!seeAllAdapter.seeAllPlaylistData.containsAll(playlistContent)) {

                            seeAllAdapter.seeAllPlaylistData = seeAllAdapter.seeAllPlaylistData.plus(playlistContent) as ArrayList<SubContent>

                            for (item in playlistContent){
                                id = item.id
                            }

                        }
                    }


                    binding.tvToolBarTitle.text = seeAllTitle


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

        val intent = Intent(this, ShowDetailsActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        intent.putExtra("section_code", sectionCode)
        intent.putExtra("id", id)
        intent.putExtra("TITLE", playlistItem.title)
        intent.putExtra("SUBTITLE", playlistItem.artists)
        startActivity(intent)

    }


}