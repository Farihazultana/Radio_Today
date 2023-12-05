package com.example.radiotoday.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.Profile
import android.util.Log
import android.view.View
import androidx.constraintlayout.motion.widget.MotionLayout
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
    private var currentState: Int = R.id.start
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        replaceFragment(HomeFragment())
        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.home -> {
                    //binding.layoutMiniPlayer.visibility = View.VISIBLE
                    replaceFragment(HomeFragment())
                }
                R.id.audio -> {
                    //binding.layoutMiniPlayer.visibility = View.VISIBLE
                    replaceFragment(AudioFragment())
                }
                R.id.video -> {
                    //binding.layoutMiniPlayer.visibility = View.VISIBLE
                    replaceFragment(VideoFragment())
                }
                R.id.news -> {
                    //binding.layoutMiniPlayer.visibility = View.VISIBLE
                    replaceFragment(NewsFragment())
                }
                R.id.settings -> {
                    //binding.layoutMiniPlayer.visibility = View.GONE
                    replaceFragment(SettingsFragment())
                }
                else -> {}
            }
            true
        }

        /*binding.layoutMiniPlayer.setOnClickListener {
            val songsFragment = SongsFragment()
            songsFragment.show(supportFragmentManager,songsFragment.tag)
        }*/

        binding.motionLayout.setTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionStarted(motionLayout: MotionLayout, i: Int, i1: Int) {
                Log.i("MotionLayout", "Started: $i")
                currentState = motionLayout.currentState
            }

            override fun onTransitionChange(motionLayout: MotionLayout, i: Int, i1: Int, v: Float) {

                Log.i("MotionLayout", "Change: $i")
            }

            override fun onTransitionCompleted(motionLayout: MotionLayout, i: Int) {

                currentState = motionLayout.currentState

                Log.i("MotionLayout", "Completed: ${motionLayout.currentState}")
            }

            override fun onTransitionTrigger(
                motionLayout: MotionLayout,
                i: Int,
                b: Boolean,
                v: Float
            ) {

                Log.i("MotionLayout", "Trigger: $b")
            }
        })

        binding.ivDown.setOnClickListener {
            binding.motionLayout.transitionToStart()
        }
    }

    private fun replaceFragment(fragment : Fragment){

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout,fragment)
        fragmentTransaction.commit()


    }

    override fun onBackPressed() {

        if (currentState == R.id.end){
            binding.motionLayout.transitionToStart()
        } else {
            super.onBackPressed()
        }


    }
}