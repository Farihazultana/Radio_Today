package com.example.radiotoday.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.radiotoday.R
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


    }
}