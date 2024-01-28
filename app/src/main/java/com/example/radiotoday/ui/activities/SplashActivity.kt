package com.example.radiotoday.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import com.example.radiotoday.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var calendar: Calendar
    private lateinit var footer: TextView
    override fun onCreate(savedInstanceState: Bundle?)  {
        val splashDelay: Long = 2000
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        calendar = Calendar.getInstance()
        val year: Int = calendar.get(Calendar.YEAR)

        footer = findViewById(R.id.footer)
        footer.text = getString(R.string.footer_text, year.toString())

        CoroutineScope(Dispatchers.Main).launch {
            delay(splashDelay)

            val introScreenShown = getIntroScreenShownStatus()

            if (!introScreenShown) {
                val intent = Intent(this@SplashActivity, IntroScreenActivity::class.java)
                setIntroScreenShownStatus(true)
                startActivity(intent)
            } else {
                val intent = Intent(this@SplashActivity, MainActivity::class.java)
                startActivity(intent)
            }
            finish()
        }
    }

    private fun getIntroScreenShownStatus(): Boolean {
        val sharedPreferences = getPreferences(MODE_PRIVATE)
        return sharedPreferences.getBoolean("introScreenShown", false)
    }

    private fun setIntroScreenShownStatus(shown: Boolean) {
        val sharedPreferences = getPreferences(MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("introScreenShown", shown)
        editor.apply()
    }
}