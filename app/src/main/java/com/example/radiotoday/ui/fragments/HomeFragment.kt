package com.example.radiotoday.ui.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.radiotoday.data.models.home.Content
import com.example.radiotoday.databinding.FragmentHomeBinding
import com.example.radiotoday.ui.activities.SeeMoreActivity
import com.example.radiotoday.ui.adapters.ParentHomeAdapter
import com.example.radiotoday.ui.viewmodels.HomeViewModel
import com.example.radiotoday.utils.ResultType
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment(), ParentHomeAdapter.ItemClickListener {
    private lateinit var parentHomeAdapter: ParentHomeAdapter
    private val homeViewModel by viewModels<HomeViewModel>()
    private lateinit var binding: FragmentHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater,container,false)

        parentHomeAdapter = ParentHomeAdapter(requireContext(), this)
        binding.rvVerticalHome.layoutManager = LinearLayoutManager(requireActivity())
        binding.rvVerticalHome.adapter = parentHomeAdapter

        homeViewModel.fetchHomeData("")
        homeViewModel.homeData.observe(requireActivity()){
            when(it){
                is ResultType.Loading -> {

                }
                is ResultType.Success -> {
                    val homeData = it.data
                    parentHomeAdapter.homeData = homeData
                    parentHomeAdapter.notifyDataSetChanged()
                }

                else -> {}
            }
        }

        return binding.root
    }

    override fun onItemClickListener(position: Int, currentItem: Content) {
        val intent = Intent(activity, SeeMoreActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        intent.putExtra("ALBUM_CODE", currentItem.albumcode)
        startActivity(intent)
    }

}