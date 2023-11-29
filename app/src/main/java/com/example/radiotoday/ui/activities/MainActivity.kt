package com.example.radiotoday.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.Profile
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.radiotoday.R
import com.example.radiotoday.databinding.ActivityMainBinding
import com.example.radiotoday.ui.fragments.AudioFragment
import com.example.radiotoday.ui.fragments.HomeFragment
import com.example.radiotoday.ui.fragments.NewsFragment
import com.example.radiotoday.ui.fragments.SettingsFragment
import com.example.radiotoday.ui.fragments.SongsFragment
import com.example.radiotoday.ui.fragments.VideoFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        replaceFragment(HomeFragment())
        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.home -> {
                    binding.cvMiniPlayer.visibility = View.VISIBLE
                    replaceFragment(HomeFragment())
                }
                R.id.audio -> {
                    binding.cvMiniPlayer.visibility = View.VISIBLE
                    replaceFragment(AudioFragment())
                }
                R.id.video -> {
                    binding.cvMiniPlayer.visibility = View.VISIBLE
                    replaceFragment(VideoFragment())
                }
                R.id.news -> {
                    binding.cvMiniPlayer.visibility = View.VISIBLE
                    replaceFragment(NewsFragment())
                }
                R.id.settings -> {
                    binding.cvMiniPlayer.visibility = View.GONE
                    replaceFragment(SettingsFragment())
                }
                else -> {}
            }
            true
        }

        binding.cvMiniPlayer.setOnClickListener {
            val songsFragment = SongsFragment()
            songsFragment.show(supportFragmentManager,songsFragment.tag)
        }
    }

    private fun replaceFragment(fragment : Fragment){

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout,fragment)
        fragmentTransaction.commit()


    }
}