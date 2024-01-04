package com.example.radiotoday.ui.fragments

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.radiotoday.R
import com.example.radiotoday.databinding.FragmentVideoBinding
import com.example.radiotoday.ui.activities.LoginActivity
import com.example.radiotoday.ui.adapters.VideoPlaylistAdapter
import com.example.radiotoday.ui.viewmodels.VideoViewModel
import com.example.radiotoday.utils.OnBackAction
import com.example.radiotoday.utils.ResultType
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VideoFragment : Fragment(), VideoPlaylistAdapter.CardClickListener {
    private lateinit var binding: FragmentVideoBinding
    private lateinit var videoAdapter: VideoPlaylistAdapter
    private val videoViewModel by viewModels<VideoViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentVideoBinding.inflate(layoutInflater, container, false)

        binding.ivBack.setOnClickListener {
            onBackAction.onBackListener()
        }

        binding.tvVideoIntroDescription.setShowingLine(2)
        binding.tvVideoIntroDescription.addShowMoreText("More")
        binding.tvVideoIntroDescription.addShowLessText("Less")
        binding.tvVideoIntroDescription.setShowMoreColor(Color.RED)
        binding.tvVideoIntroDescription.setShowLessTextColor(Color.RED)

        videoAdapter = VideoPlaylistAdapter(requireContext(), this)
        binding.rvPlaylist.layoutManager = LinearLayoutManager(requireActivity())
        binding.rvPlaylist.adapter = videoAdapter

        videoViewModel.fetchVideoPlaylistData("")
        videoViewModel.videoPlaylistData.observe(requireActivity()){
            when(it){
                is ResultType.Loading -> {
                    binding.shimmerFrameLayout.visibility = View.VISIBLE
                }
                is ResultType.Success -> {
                    val playlistData = it.data
                    videoAdapter.videoPlaylistData = playlistData.contents
                    videoAdapter.notifyDataSetChanged()
                    binding.shimmerFrameLayout.visibility = View.GONE
                }

                else -> {}
            }
        }

        return binding.root
    }

    override fun onCardClickListener(position: Int) {
        Log.d("VideoFragment", "Clicked on position: $position")

        if (position >= 0 && position < videoAdapter.itemCount) {
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        } else {
            Log.e("VideoFragment", "Invalid position: $position")
        }

        videoAdapter.notifyDataSetChanged()
    }

    companion object{

        lateinit var onBackAction: OnBackAction
        fun onBackAction(setBackAction: OnBackAction){
            this.onBackAction = setBackAction
        }

    }


}