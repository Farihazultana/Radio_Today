package com.example.radiotoday.ui.activities

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.radiotoday.R
import com.example.radiotoday.data.models.SubContent
import com.example.radiotoday.data.services.MusicPlayerService
import com.example.radiotoday.databinding.ActivityMainBinding
import com.example.radiotoday.ui.fragments.AudioFragment
import com.example.radiotoday.ui.fragments.HomeFragment
import com.example.radiotoday.ui.fragments.NewsFragment
import com.example.radiotoday.ui.fragments.SettingsFragment
import com.example.radiotoday.ui.fragments.PlayerFragment
import com.example.radiotoday.ui.fragments.PlayerFragment.Companion.onPlayAction
import com.example.radiotoday.ui.fragments.VideoFragment
import com.example.radiotoday.ui.interfaces.OnBackAction
import com.example.radiotoday.ui.interfaces.PlayerClickListener
import com.example.radiotoday.utils.NotificationUtils
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), OnBackAction, PlayerClickListener, PlayerFragment.PlayerDismissListener{

    private lateinit var binding: ActivityMainBinding


    private lateinit var service: MusicPlayerService
    private var isServiceBound = false
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val musicPlayerBinder = service as MusicPlayerService.MusicPlayerBinder
            this@MainActivity.service = musicPlayerBinder.getService()
            isServiceBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isServiceBound = false
        }

    }

    private lateinit var activeFragment: Fragment
    private var homeFragment = HomeFragment()
    private var audioFragment = AudioFragment()
    private var videoFragment = VideoFragment()
    private var newsFragment = NewsFragment()
    private var settingsFragment = SettingsFragment()
    private val playerFragment = PlayerFragment()

    private var doubleBackToExitPressedOnce = true

    private lateinit var fragmentManager: FragmentManager

    private var playerClicked = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val type  = intent.getStringExtra("FCMType")
        Log.i("FCMType", "onNewIntent Main: $type")
        if (!type.isNullOrEmpty()) {
            val intent = when (type) {
                "profile" -> Intent(applicationContext, ProfileActivity::class.java)
                "alarm" -> Intent(applicationContext, AlarmActivity::class.java)
                else -> Intent(applicationContext, MainActivity::class.java)
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
            finish()
        }

        AudioFragment.onBackAction(this)
        VideoFragment.onBackAction(this)
        SettingsFragment.onBackAction(this)
        NewsFragment.onBackAction(this)

        homeFragment.playerClickListener = this
        audioFragment.playerClickListener = this
        videoFragment.playerClickListener = this
        newsFragment.playerClickListener = this

        //audioFragment.setSongSelectionListener(this)

        replaceFragment(homeFragment)
        binding.bottomNavigationView.itemIconTintList=null
        binding.bottomNavigationView.setOnItemSelectedListener {
            handleNavigation(it)
            true
        }

        binding.layoutMiniPlayer.cvMiniPlayer.setOnClickListener {
            if (!playerClicked){
                gotoPlayer()
            }
        }

        val playBtnMini = binding.layoutMiniPlayer.ivPlayMini
        //val pauseBtnMini = miniPlayer.findViewById<ImageView>(R.id.iv_pauseMini)
        //val nextBtnMini = miniPlayer.findViewById<ImageView>(R.id.iv_playNextMini)
        //val prevBtnMini = miniPlayer.findViewById<ImageView>(R.id.iv_playPrevMini)


        getFCMToken(intent)
        val value = intent.getStringExtra("section")
        Log.i("FCM", "getFCMToken value Main: $value")

    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val type = intent.getStringExtra("FCMType")
        Log.i("FCMType", "onNewIntent Main: $type")
        if (!type.isNullOrEmpty()) {
            val redirectIntent = when (type) {
                "profile" -> Intent(applicationContext, ProfileActivity::class.java)
                "alarm" -> Intent(applicationContext, AlarmActivity::class.java)
                else -> Intent(applicationContext, MainActivity::class.java)
            }
            redirectIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(redirectIntent)
            finish()
        }

        if (intent.hasExtra("fragment")) {
            val fragmentType = intent.getStringExtra("fragment")
            Log.i("FType", "onCreate: $fragmentType")
            if (fragmentType == "songs") {
                playerClicked = true

                playerFragment.dismissListener = this
                replaceFragment(playerFragment)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        bindService(Intent(this, MusicPlayerService::class.java), serviceConnection, Context.BIND_AUTO_CREATE)
        startMusicPlayerService()
    }

    override fun onStop() {
        super.onStop()
        if (isServiceBound) {
            unbindService(serviceConnection)
            isServiceBound = false
        }
    }

    override fun onDestroy() {
        stopService(intent)

        if (onPlayAction.isPlaying()){
            onPlayAction.releasePlayer()
        }
        super.onDestroy()
    }


    private fun handleNavigation(it: MenuItem) {
        when (it.itemId) {
            R.id.home -> {
                binding.layoutMiniPlayer.cvMiniPlayer.visibility = View.VISIBLE
                replaceFragment(homeFragment)
            }

            R.id.audio -> {
                binding.layoutMiniPlayer.cvMiniPlayer.visibility = View.VISIBLE
                replaceFragment(audioFragment)
            }

            R.id.video -> {
                binding.layoutMiniPlayer.cvMiniPlayer.visibility = View.VISIBLE
                replaceFragment(videoFragment)
            }

            R.id.news -> {
                binding.layoutMiniPlayer.cvMiniPlayer.visibility = View.VISIBLE
                replaceFragment(newsFragment)
            }

            R.id.settings -> {
                binding.layoutMiniPlayer.cvMiniPlayer.visibility = View.GONE
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

    override fun onPlayerClickListener() {
        if (!playerClicked){
            gotoPlayer()
        }

    }

    private fun startMusicPlayerService() {
        intent = Intent(this, MusicPlayerService::class.java)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationUtils.createNotificationChannel(this)
            ContextCompat.startForegroundService(this, intent)
        } else {
            stopService(intent)
        }
    }

    private fun gotoPlayer() {
        playerClicked = true
        playerFragment.dismissListener = this
        playerFragment.show(supportFragmentManager, playerFragment.tag)
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

    override fun onPlayerDismissListener() {
        playerClicked = false
        activeFragment.onResume()
    }

}