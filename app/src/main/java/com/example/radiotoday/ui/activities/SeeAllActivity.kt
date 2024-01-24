package com.example.radiotoday.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.radiotoday.data.models.seeAll.ContentSeeAll
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
    lateinit var catName: String
    lateinit var contentType: String

    private var isLoading = false
    private var isLastpage = false
    private var currentPage = 1
    lateinit var albumCode: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySeeAllBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolBarBackIcon.setOnClickListener {
            onBackPressed()
        }

        catName = intent.getStringExtra("catname").toString()
        Log.i("section", "onCreate: $catName")
        contentType = intent.getStringExtra("contenttype").toString()


        seeAllAdapter = SeeAllAdapter(this, this, catName)
        if (catName == "Podcasts"){
            binding.rvSeeAll.layoutManager = CustomGridLayoutManager(2)
        }else{
            binding.rvSeeAll.layoutManager = CustomGridLayoutManager(3)
        }

        binding.rvSeeAll.adapter = seeAllAdapter


        observeSeeAllData()
        if (catName.isNotEmpty()){
            loadSeeAllData()  //Initial data load

            binding.rvSeeAll.addOnScrollListener(object : RecyclerView.OnScrollListener(){
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

    private fun CustomGridLayoutManager(span : Int): GridLayoutManager {
        layoutManager = GridLayoutManager(this, span)
        return layoutManager
    }

    private fun loadSeeAllData() {
        Log.i("catname", "loadSeeAllData: $catName")
        seeAllViewModel.fetchSeeAllData(catName,  currentPage.toString())

    }

    private fun observeSeeAllData() {
        seeAllViewModel.seeAllData.observe(this) {
            when (it) {
                is ResultType.Loading -> {
                    if(catName == "Band"){
                        binding.shimmerFrameLayoutRectangles.visibility = View.VISIBLE
                    }else{
                        binding.shimmerFrameLayoutSquare.visibility = View.VISIBLE
                    }

                }

                is ResultType.Success -> {
                    binding.shimmerFrameLayoutSquare.visibility = View.GONE
                    binding.shimmerFrameLayoutRectangles.visibility = View.GONE
                    playlistData = it.data

                    if(currentPage == 1){
                        seeAllAdapter.seeAllPlaylistData = playlistData.content.content as ArrayList<ContentSeeAll>
                        /*if (contentType == "2") {
                            seeAllAdapter.seeAllPlaylistData = playlistData.content.content as ArrayList<ContentSeeAll>
                        } else {
                            Toast.makeText(this, "Coming Soon!", Toast.LENGTH_SHORT).show()
                        }*/
                    }else{
                        if(!seeAllAdapter.seeAllPlaylistData.containsAll(playlistData.content.content)){
                            seeAllAdapter.seeAllPlaylistData = seeAllAdapter.seeAllPlaylistData.plus(playlistData.content.content) as ArrayList<ContentSeeAll>

                        }
                    }


                    binding.tvToolBarTitle.text = catName

                    isLoading = false
                    //checking last page
                    isLastpage = playlistData.content.content.isEmpty()
                    currentPage++
                    seeAllAdapter.notifyDataSetChanged()
                }

                else -> {}
            }
        }
    }

    override fun onItemClickListener(position: Int, playlistItem: ContentSeeAll) {

        val intent = Intent(this, ShowDetailsActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        intent.putExtra("ALBUM_CODE", playlistItem.content)
        intent.putExtra("TITLE_IMG", playlistItem.image)
        intent.putExtra("TITLE", playlistItem.title)
        intent.putExtra("SUBTITLE", playlistItem.artists)
        startActivity(intent)

    }


}