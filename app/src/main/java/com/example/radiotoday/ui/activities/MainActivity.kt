package com.example.radiotoday.ui.activities

import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.RequestOptions.bitmapTransform
import com.bumptech.glide.request.target.Target
import com.example.radiotoday.R
import com.example.radiotoday.databinding.ActivityMainBinding
import com.example.radiotoday.ui.fragments.AudioFragment
import com.example.radiotoday.ui.fragments.HomeFragment
import com.example.radiotoday.ui.fragments.NewsFragment
import com.example.radiotoday.ui.fragments.SettingsFragment
import com.example.radiotoday.ui.fragments.SongsFragment
import com.example.radiotoday.ui.fragments.VideoFragment
import com.example.radiotoday.utils.BitmapTransformation
import com.example.radiotoday.utils.BlurTransformation
import com.example.radiotoday.utils.OnBackAction
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), OnBackAction {
    private var currentState: Int = R.id.start
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        AudioFragment.onBackAction(this)
        VideoFragment.onBackAction(this)
        SettingsFragment.onBackAction(this)

        replaceFragment(HomeFragment())
        binding.bottomNavigationView.itemIconTintList=null
        binding.bottomNavigationView.setOnItemSelectedListener {
            handleNavigation(it)
            true
        }


        binding.layoutMiniPlayer.setOnClickListener {
            val songsFragment = SongsFragment()
            songsFragment.show(supportFragmentManager,songsFragment.tag)
        }

    }

    private fun handleNavigation(it: MenuItem) {
        when (it.itemId) {
            R.id.home -> {
                binding.layoutMiniPlayer.visibility = View.VISIBLE
                replaceFragment(HomeFragment())

            }

            R.id.audio -> {
                binding.layoutMiniPlayer.visibility = View.VISIBLE
                replaceFragment(AudioFragment())
            }

            R.id.video -> {
                binding.layoutMiniPlayer.visibility = View.VISIBLE
                replaceFragment(VideoFragment())
            }

            R.id.news -> {
                binding.layoutMiniPlayer.visibility = View.VISIBLE
                replaceFragment(NewsFragment())
            }

            R.id.settings -> {
                binding.layoutMiniPlayer.visibility = View.GONE
                replaceFragment(SettingsFragment())
            }

            else -> {}
        }
    }


    private fun replaceFragment(fragment : Fragment){

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout,fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()


    }

    override fun onBackListener() {
        replaceFragment(HomeFragment())
        binding.bottomNavigationView.selectedItemId = R.id.home
    }

}