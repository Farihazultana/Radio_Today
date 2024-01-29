package com.example.radiotoday.ui.fragments

import android.content.Intent
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.radiotoday.R
import com.example.radiotoday.data.models.SubContent
import com.example.radiotoday.databinding.FragmentHomeBinding
import com.example.radiotoday.ui.activities.ShowDetailsActivity
import com.example.radiotoday.ui.adapters.ParentHomeAdapter
import com.example.radiotoday.ui.viewmodels.HomeViewModel
import com.example.radiotoday.utils.ResultType
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment(), ParentHomeAdapter.ItemClickListener {
    private lateinit var parentHomeAdapter: ParentHomeAdapter
    private val homeViewModel by viewModels<HomeViewModel>()
    private lateinit var binding: FragmentHomeBinding
    lateinit var sectionCode: String

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater,container,false)

        parentHomeAdapter = ParentHomeAdapter(this)
        binding.rvHomeMain.layoutManager = LinearLayoutManager(requireActivity())
        binding.rvHomeMain.adapter = parentHomeAdapter

        homeViewModel.fetchHomeData("home", "android")
        homeViewModel.homeData.observe(requireActivity()){
            when(it){
                is ResultType.Loading -> {
                    binding.shimmerFrameLayoutHome.visibility = View.VISIBLE
                }
                is ResultType.Success -> {
                    binding.shimmerFrameLayoutHome.visibility = View.GONE
                    val homeData= it.data.content
                    parentHomeAdapter.homeData = homeData
                    this.parentHomeAdapter.notifyDataSetChanged()


                }

                else -> {}
            }
        }

        return binding.root
    }

    override fun onItemClickListener(position: Int, currentItem: SubContent, currentSection: String) {

        if (currentSection == "songs"){
            val songsFragment = SongsFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .add(R.id.frameLayout, songsFragment)
                .commit()



        }else{
            val intent = Intent(activity, ShowDetailsActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            intent.putExtra("section_code", currentSection)
            intent.putExtra("id", currentItem.id)
            startActivity(intent)
        }

    }

}