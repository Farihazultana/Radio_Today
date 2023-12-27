package com.example.radiotoday.ui.fragments

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.radiotoday.R
import com.example.radiotoday.audioPlayer.MusicService
import com.example.radiotoday.audioPlayer.PlayerController
import com.example.radiotoday.databinding.FragmentAudioBinding
import com.example.radiotoday.ui.activities.LoginActivity
import com.example.radiotoday.ui.adapters.AudioPlaylistAdapter
import com.example.radiotoday.ui.viewmodels.AudioViewModel
import com.example.radiotoday.utils.OnBackAction
import com.example.radiotoday.utils.ResultType
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AudioFragment : Fragment(), AudioPlaylistAdapter.CardClickListener {
    private lateinit var binding: FragmentAudioBinding
    private lateinit var audioAdapter: AudioPlaylistAdapter
    private val audioViewModel by viewModels<AudioViewModel>()

    var mPlayerController: PlayerController? = null
    private var mMusicService: MusicService? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        mPlayerController = MusicService.getMediaPlayerHolder()


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAudioBinding.inflate(layoutInflater,container,false)

        binding.ivBack.setOnClickListener {
            onBackAction.onBackListener()
        }

        binding.tvAudioIntroDescription.setShowingLine(2)
        binding.tvAudioIntroDescription.addShowMoreText("More")
        binding.tvAudioIntroDescription.addShowLessText("Less")
        binding.tvAudioIntroDescription.setShowMoreColor(Color.RED)
        binding.tvAudioIntroDescription.setShowLessTextColor(Color.RED)

        audioAdapter = AudioPlaylistAdapter(requireContext(), this)
        binding.rvPlaylist.layoutManager = LinearLayoutManager(requireActivity())
        binding.rvPlaylist.adapter = audioAdapter

        audioViewModel.fetchAudioPlaylistData("")
        audioViewModel.audioPlaylistData.observe(requireActivity()){
            when(it){
                is ResultType.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is ResultType.Success -> {
                    val playlistData = it.data
                    audioAdapter.audioPlaylistData = playlistData.contents
                    audioAdapter.notifyDataSetChanged()
                    binding.progressBar.visibility = View.GONE
                }

                else -> {}
            }
        }

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.playitem_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.item_share ->{
                return true
            }
            R.id.item_download ->{
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCardClickListener(position: Int) {
        Log.d("AudioFragment", "Clicked on position: $position")

        if (position >= 0 && position < audioAdapter.itemCount) {
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        } else {
            Log.e("AudioFragment", "Invalid position: $position")
        }

        audioAdapter.notifyDataSetChanged()
    }

    companion object{

        lateinit var onBackAction: OnBackAction
        fun onBackAction(setBackAction: OnBackAction){
            this.onBackAction = setBackAction
        }

    }

}