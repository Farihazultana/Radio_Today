package com.example.radiotoday.ui.activities

import android.R.attr.type
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
import com.example.radiotoday.utils.OnBackAction
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), OnBackAction, HomeFragment.SongClickListener, SongsFragment.SongDismissListener{

    private lateinit var binding: ActivityMainBinding

    private lateinit var activeFragment: Fragment
    private var homeFragment = HomeFragment()
    private var audioFragment = AudioFragment()
    private var videoFragment = VideoFragment()
    private var newsFragment = NewsFragment()
    private var settingsFragment = SettingsFragment()

    private var doubleBackToExitPressedOnce = true

    private lateinit var fragmentManager: FragmentManager

    private var playerClicked = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        AudioFragment.onBackAction(this)
        VideoFragment.onBackAction(this)
        SettingsFragment.onBackAction(this)
        NewsFragment.onBackAction(this)

        homeFragment.songClickListener = this

        replaceFragment(homeFragment)
        binding.bottomNavigationView.itemIconTintList=null
        binding.bottomNavigationView.setOnItemSelectedListener {
            handleNavigation(it)
            true
        }


        binding.layoutMiniPlayer.setOnClickListener {
            if (!playerClicked){
                gotoPlayer()
            }
        }

        getFCMToken(intent)
        val value = intent.getStringExtra("section")
        Log.i("FCM", "getFCMToken value Main: $value")

    }


    private fun handleNavigation(it: MenuItem) {
        when (it.itemId) {
            R.id.home -> {
                binding.layoutMiniPlayer.visibility = View.VISIBLE
                replaceFragment(homeFragment)
            }

            R.id.audio -> {
                binding.layoutMiniPlayer.visibility = View.VISIBLE
                replaceFragment(audioFragment)
            }

            R.id.video -> {
                binding.layoutMiniPlayer.visibility = View.VISIBLE
                replaceFragment(videoFragment)
            }

            R.id.news -> {
                binding.layoutMiniPlayer.visibility = View.VISIBLE
                replaceFragment(newsFragment)
            }

            R.id.settings -> {
                binding.layoutMiniPlayer.visibility = View.GONE
                replaceFragment(settingsFragment)
            }

            else -> {}
        }
    }


    private fun replaceFragment(fragment : Fragment){

        activeFragment = fragment

        fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout,fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    override fun onBackListener() {
        replaceFragment(homeFragment)
        binding.bottomNavigationView.selectedItemId = R.id.home
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (activeFragment != homeFragment ){
            replaceFragment(homeFragment)
            binding.bottomNavigationView.selectedItemId = R.id.home

        } else{
            if (doubleBackToExitPressedOnce) {
                doubleBackToExitPressedOnce = false
                Handler(Looper.getMainLooper()).postDelayed({
                    doubleBackToExitPressedOnce = true
                }, 2000)
                Toast.makeText(this, "Press again to exit app", Toast.LENGTH_SHORT).show()
            } else {
                super.onBackPressed()
                finish()
            }
        }

    }

    override fun onSongClickListener() {
        if (!playerClicked){
            gotoPlayer()
        }

    }

    private fun gotoPlayer() {
        playerClicked = true
        val songsFragment = SongsFragment()
        songsFragment.dismissListener = this
        songsFragment.show(supportFragmentManager, songsFragment.tag)
    }

    private fun getFCMToken(intent: Intent?) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Log.i("NotificationFCM", "getFCMToken: $token")
                var deviceID = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
                Log.i("NotificationFCM", "Device ID: $deviceID ")


            } else {

            }
        }
    }

    override fun onSongDismissListener() {
        playerClicked = false
    }

}