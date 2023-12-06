package com.example.radiotoday.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.radiotoday.R
import com.example.radiotoday.databinding.FragmentAudioBinding
import com.example.radiotoday.ui.activities.LoginActivity
import com.example.radiotoday.ui.adapters.AudioPlaylistAdapter
import com.example.radiotoday.ui.viewmodels.AudioViewModel
import com.example.radiotoday.utils.ResultType
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AudioFragment : Fragment(), AudioPlaylistAdapter.CardClickListener {
    private lateinit var binding: FragmentAudioBinding
    private lateinit var audioAdapter: AudioPlaylistAdapter
    private val audioViewModel by viewModels<AudioViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAudioBinding.inflate(layoutInflater,container,false)


        audioAdapter = AudioPlaylistAdapter(this)
        binding.rvPlaylist.layoutManager = LinearLayoutManager(requireActivity())
        binding.rvPlaylist.adapter = audioAdapter

        audioViewModel.fetchAudioPlaylistData("")
        audioViewModel.audioPlaylistData.observe(requireActivity()){
            when(it){
                is ResultType.Loading -> {

                }
                is ResultType.Success -> {
                    val playlistData = it.data
                    audioAdapter.audioPlaylistData = playlistData.contents
                    audioAdapter.notifyDataSetChanged()
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
            startActivity(intent)
        } else {
            Log.e("AudioFragment", "Invalid position: $position")
        }

        audioAdapter.notifyDataSetChanged()
    }

}