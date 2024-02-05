package com.example.radiotoday.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        calendar = Calendar.getInstance()
        val year: Int = calendar.get(Calendar.YEAR)


        binding.footer.text = getString(R.string.footer_text, year.toString())

        CoroutineScope(Dispatchers.Main).launch {
            delay(splashDelay)

            val introScreenShown = SharedPreferencesUtil.getData(this@SplashActivity, AppUtils.IntroScreenStatus, false)

            if (introScreenShown != true) {
                val intent = Intent(this@SplashActivity, IntroScreenActivity::class.java)

                startActivity(intent)
            } else {
                val intent = Intent(this@SplashActivity, MainActivity::class.java)
                startActivity(intent)
            }
            finish()
        }
    }
}