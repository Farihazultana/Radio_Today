package com.example.radiotoday.ui.fragments


import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.radiotoday.data.models.SongList
import com.example.radiotoday.data.models.SubContent
import com.example.radiotoday.data.models.seeAll.SeeAllResponse
import com.example.radiotoday.databinding.FragmentAudioBinding
import com.example.radiotoday.ui.adapters.AudioPlaylistAdapter
import com.example.radiotoday.ui.viewmodels.AudioViewModel
import com.example.radiotoday.ui.interfaces.ItemClickListener
import com.example.radiotoday.ui.interfaces.OnBackAction
import com.example.radiotoday.utils.ResultType
import com.example.radiotoday.ui.interfaces.PlayerClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AudioFragment : Fragment(), ItemClickListener {
    private lateinit var binding: FragmentAudioBinding
    private lateinit var audioAdapter: AudioPlaylistAdapter
    private val audioViewModel by viewModels<AudioViewModel>()
    private lateinit var layoutManager: GridLayoutManager

    private lateinit var playlistData: SeeAllResponse

    var playerClickListener: PlayerClickListener? = null


    private var isLoading = false
    private var isLastpage = false
    private var currentPage = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAudioBinding.inflate(layoutInflater, container, false)

        binding.ivBack.setOnClickListener {
            onBackAction.onBackListener()
        }

        binding.tvAudioIntroDescription.setShowingLine(2)
        binding.tvAudioIntroDescription.addShowMoreText("More")
        binding.tvAudioIntroDescription.addShowLessText("Less")
        binding.tvAudioIntroDescription.setShowMoreColor(Color.RED)
        binding.tvAudioIntroDescription.setShowLessTextColor(Color.RED)

        audioAdapter = AudioPlaylistAdapter(requireContext(), this)
        binding.rvPlaylist.layoutManager = customGridLayoutManager(1)
        binding.rvPlaylist.adapter = audioAdapter

        observeSeeAllData()

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
        audioViewModel.fetchAudioPlaylistData(currentPage.toString())
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

    private fun observeSeeAllData() {
        audioViewModel.audioPlaylistData.observe(viewLifecycleOwner) {
            when (it) {
                is ResultType.Loading -> {

                    binding.shimmerFrameLayout.visibility = View.VISIBLE

                }

                is ResultType.Success -> {
                    binding.shimmerFrameLayout.visibility = View.GONE
                    playlistData = it.data

                    val playlistContent = playlistData.content.content


                    if (currentPage == 1) {
                        audioAdapter.audioPlaylistData = playlistContent


                    } else {
                        if (!audioAdapter.audioPlaylistData.containsAll(playlistContent)) {

                            audioAdapter.audioPlaylistData =
                                audioAdapter.audioPlaylistData.plus(playlistContent) as ArrayList<SubContent>


                        }
                    }

                    isLoading = false
                    //checking last page
                    isLastpage = playlistContent.isEmpty()
                    currentPage++
                    audioAdapter.notifyDataSetChanged()
                }

                is ResultType.Error -> {
                    val errorMessage = it.exception.message
                    Log.e("AudioFragment", "Error fetching playlist data: $errorMessage")
                }

            }
        }
    }

    private fun customGridLayoutManager(span: Int): GridLayoutManager {
        layoutManager = GridLayoutManager(requireActivity(), span)
        return layoutManager
    }

    override fun onItemClickListener(position: Int) {
        Log.d("AudioFragment", "Clicked on position: $position")

        SongList.setSongList(audioAdapter.audioPlaylistData, position)

        PlayerFragment.onPlayAction.initializePlayer()


        if (position >= 0 && position < audioAdapter.itemCount) {

            playerClickListener?.onPlayerClickListener()

        } else {
            Log.e("AudioFragment", "Invalid position: $position")
        }

        audioAdapter.notifyDataSetChanged()
    }

    companion object {

        lateinit var onBackAction: OnBackAction
        fun onBackAction(setBackAction: OnBackAction) {
            this.onBackAction = setBackAction
        }

    }


}