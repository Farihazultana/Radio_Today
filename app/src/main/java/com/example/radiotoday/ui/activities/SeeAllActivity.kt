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
import com.example.radiotoday.data.models.seeAll.Content
import com.example.radiotoday.data.models.seeAll.SeeAllResponse
import com.example.radiotoday.databinding.ActivitySeeAllBinding
import com.example.radiotoday.ui.adapters.ChildHomeAdapter
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
    private var layoutManager = GridLayoutManager(this, 2)
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
        contentType = intent.getStringExtra("contenttype").toString()


        seeAllAdapter = SeeAllAdapter(this, this, catName)
        binding.rvSeeAll.layoutManager = layoutManager
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

    private fun loadSeeAllData() {
        Log.i("catname", "loadSeeAllData: $catName")
        seeAllViewModel.fetchSeeAllData(catName, "album", currentPage.toString())

    }

    private fun observeSeeAllData() {
        seeAllViewModel.seeAllData.observe(this) {
            when (it) {
                is ResultType.Loading -> {
                    binding.pbSeeAll.visibility = View.VISIBLE
                }

                is ResultType.Success -> {
                    binding.pbSeeAll.visibility = View.GONE
                    playlistData = it.data

                    if(currentPage == 1){
                        if (contentType == "2") {
                            seeAllAdapter.seeAllPlaylistData = playlistData.contents
                        } else {
                            Toast.makeText(this, "Coming Soon!", Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        if(!seeAllAdapter.seeAllPlaylistData.containsAll(playlistData.contents)){
                            seeAllAdapter.seeAllPlaylistData = seeAllAdapter.seeAllPlaylistData.plus(playlistData.contents) as ArrayList<Content>

                        }
                    }


                    binding.tvToolBarTitle.text = catName

                    isLoading = false
                    //checking last page
                    isLastpage = playlistData.contents.isEmpty()
                    currentPage++
                    seeAllAdapter.notifyDataSetChanged()
                }

                else -> {}
            }
        }
    }

    override fun onItemClickListener(position: Int, playlistItem: Content) {

        val intent = Intent(this, SeeMoreActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        intent.putExtra("ALBUM_CODE", playlistItem.albumcode)
        intent.putExtra("TITLE_IMG", playlistItem.image_location)
        intent.putExtra("TITLE", playlistItem.albumname)
        intent.putExtra("SUBTITLE", playlistItem.artistname)
        startActivity(intent)

    }


}