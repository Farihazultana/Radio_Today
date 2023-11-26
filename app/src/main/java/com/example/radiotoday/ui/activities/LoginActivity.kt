package com.example.radiotoday.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.radiotoday.R
import com.example.radiotoday.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvRegister.setOnClickListener {
            binding.layoutLogin.visibility = View.GONE
            binding.layoutRegistration.visibility = View.VISIBLE
        }
        binding.tvRegRegister.setOnClickListener {
            binding.layoutLogin.visibility = View.VISIBLE
            binding.layoutRegistration.visibility = View.GONE
        }

    }
}