package com.example.radiotoday.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.radiotoday.R
import com.example.radiotoday.databinding.ActivitySplashBinding
import com.example.radiotoday.utils.AppUtils
import com.example.radiotoday.utils.SharedPreferencesUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    lateinit var binding: ActivitySplashBinding
    private lateinit var calendar: Calendar
    override fun onCreate(savedInstanceState: Bundle?)  {
        val splashDelay: Long = 2000
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val type  = intent.getStringExtra("section")
        Log.i("FCM", "onNewIntent Splash: $type")

        calendar = Calendar.getInstance()
        val year: Int = calendar.get(Calendar.YEAR)

        binding.footer.text = getString(R.string.footer_text, year.toString())

        Handler(Looper.getMainLooper()).postDelayed({
            val introScreenShown = SharedPreferencesUtil.getData(this@SplashActivity, AppUtils.IntroScreenStatus, false)

            if (introScreenShown != true) {
                val intent = Intent(this@SplashActivity, IntroScreenActivity::class.java)
                startActivity(intent)
            } else {

                if (!type.isNullOrEmpty()) {
                    val intent = when (type) {
                        "profile" -> Intent(applicationContext, ProfileActivity::class.java)
                        "alarm" -> Intent(applicationContext, AlarmActivity::class.java)
                        else -> Intent(applicationContext, MainActivity::class.java)
                    }
                    intent.putExtra("FCMType", type)
                    startActivity(intent)
                } else {
                    val intent = Intent(this@SplashActivity, MainActivity::class.java)
                    intent.putExtra("FCMType", type)
                    startActivity(intent)
                }
            }

            finish()
        }, splashDelay)
    }

}