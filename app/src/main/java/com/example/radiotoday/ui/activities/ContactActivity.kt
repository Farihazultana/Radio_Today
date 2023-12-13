package com.example.radiotoday.ui.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.radiotoday.databinding.ActivityContactBinding


class ContactActivity : AppCompatActivity() {
    private lateinit var binding: ActivityContactBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolBarBackIcon.setOnClickListener {
            onBackPressed()
        }

        binding.layoutContactNumber.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.setData(Uri.parse("tel:0195495301"))
            startActivity(intent)
        }

        binding.layoutEmailAddress.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(Intent.EXTRA_EMAIL, "help@radiotoday.com")
            intent.type = "message/rfc822"
            startActivity(Intent.createChooser(intent,"Choose an email client"))
        }

        binding.layoutInstagram.setOnClickListener {
            openSocialMediaApp("com.instagram.android", "https://www.instagram.com/")
        }

        binding.layoutTwitter.setOnClickListener {
            openSocialMediaApp("com.twitter.android", "https://twitter.com/")
        }

        binding.layoutFB.setOnClickListener {
            openSocialMediaApp("com.facebook.katana", "https://www.facebook.com/")
        }


    }

    private fun openSocialMediaApp(packageName: String, webUrl: String) {
        val pm = packageManager
        try {
            val intent = pm.getLaunchIntentForPackage(packageName)

            if (intent != null) {
                startActivity(intent)
            } else {

                val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(webUrl))
                startActivity(webIntent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}