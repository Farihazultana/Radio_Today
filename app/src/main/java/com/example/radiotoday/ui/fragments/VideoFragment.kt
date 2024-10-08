package com.example.radiotoday.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.radiotoday.data.models.SongList
import com.example.radiotoday.data.models.SubContent
import com.example.radiotoday.data.models.seeAll.SeeAllResponse
import com.example.radiotoday.databinding.FragmentVideoBinding
import com.example.radiotoday.ui.adapters.VideoPlaylistAdapter
import com.example.radiotoday.ui.viewmodels.VideoViewModel
import com.example.radiotoday.ui.interfaces.ItemClickListener
import com.example.radiotoday.ui.interfaces.OnBackAction
import com.example.radiotoday.utils.ResultType
import com.example.radiotoday.ui.interfaces.PlayerClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VideoFragment : Fragment(), ItemClickListener {
    private lateinit var binding: FragmentVideoBinding
    private lateinit var videoAdapter: VideoPlaylistAdapter
    private val videoViewModel by viewModels<VideoViewModel>()

    private lateinit var layoutManager: GridLayoutManager
    private lateinit var playlistData: SeeAllResponse

    var playerClickListener: PlayerClickListener? = null

    private var isLoading = false
    private var isLastpage = false
    private var currentPage = 1

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
        binding.rvPlaylist.layoutManager = customGridLayoutManager(1)
        binding.rvPlaylist.adapter = videoAdapter


        observeVideoData()

        return binding.root
    }

    override fun onResume() {
        isLoading = false
        isLastpage = false
        currentPage = 1

        showSeeAll()

        super.onResume()
    }

    private fun loadSeeAllData() {
        videoViewModel.fetchVideoPlaylistData(currentPage.toString())
    }

    private fun showSeeAll() {
        loadSeeAllData()  //Initial data load

        binding.rvPlaylist.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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

    private fun observeVideoData() {
        videoViewModel.videoPlaylistData.observe(requireActivity()) {
            when (it) {
                is ResultType.Loading -> {
                    binding.shimmerFrameLayout.visibility = View.VISIBLE
                }

                is ResultType.Success -> {
                    playlistData = it.data
                    val playlistContent= playlistData.content.content

                    if (currentPage == 1) {
                        videoAdapter.videoPlaylistData = playlistContent


                    } else {
                        if (!videoAdapter.videoPlaylistData.containsAll(playlistContent)) {

                            videoAdapter.videoPlaylistData =
                                videoAdapter.videoPlaylistData.plus(playlistContent) as ArrayList<SubContent>


                        }
                    }

                    isLoading = false
                    //checking last page
                    isLastpage = playlistContent.isEmpty()
                    currentPage++
                    videoAdapter.notifyDataSetChanged()
                    binding.shimmerFrameLayout.visibility = View.GONE
                }

                else -> {}
            }
        }
    }

    private fun customGridLayoutManager(span: Int): GridLayoutManager {
        layoutManager = GridLayoutManager(requireActivity(), span)
        return layoutManager
    }

    override fun onItemClickListener(position: Int) {
        Log.d("VideoFragment", "Clicked on position: $position")

        SongList.setSongList(videoAdapter.videoPlaylistData, position)

        if (position >= 0 && position < videoAdapter.itemCount) {
            playerClickListener?.onPlayerClickListener()

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