package com.example.radiotoday.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.radiotoday.R
import com.example.radiotoday.databinding.ActivitySeeAllBinding
import com.example.radiotoday.ui.adapters.AudioPlaylistAdapter
import com.example.radiotoday.ui.adapters.SeeAllAdapter
import com.example.radiotoday.ui.viewmodels.SeeAllViewModel
import com.example.radiotoday.utils.ResultType
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SeeAllActivity : AppCompatActivity(), SeeAllAdapter.ItemClickListener {

    private lateinit var binding: ActivitySeeAllBinding
    private lateinit var seeAllAdapter: SeeAllAdapter
    private val seeAllViewModel by viewModels<SeeAllViewModel>()
    private var layoutManager = GridLayoutManager(this, 2)
    lateinit var catName: String
    lateinit var contentType: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySeeAllBinding.inflate(layoutInflater)
        setContentView(binding.root)


        catName = intent.getStringExtra("catname").toString()
        contentType = intent.getStringExtra("contenttype").toString()


        seeAllAdapter = SeeAllAdapter(this, this)
        binding.rvSeeAll.layoutManager = layoutManager
        binding.rvSeeAll.adapter = seeAllAdapter

        seeAllViewModel.fetchSeeAllData("folk","album", "1")
        seeAllViewModel.seeAllData.observe(this){
            when(it){
                is ResultType.Loading -> {

                }
                is ResultType.Success -> {
                    val playlistData = it.data

                    if (contentType=="2"){
                        seeAllAdapter.seeAllPlaylistData = playlistData.contents
                    }else{
                        Toast.makeText(this,"Coming Soon!", Toast.LENGTH_SHORT).show()
                    }

                    binding.tvSeeAllTitle.text = catName
                    seeAllAdapter.notifyDataSetChanged()
                }

                else -> {}
            }
        }

    }

    override fun onItemClickListener(position: Int) {

    }
}