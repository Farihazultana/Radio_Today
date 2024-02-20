package com.example.radiotoday.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.radiotoday.R
import com.example.radiotoday.databinding.ActivityProfileBinding
import com.example.radiotoday.ui.fragments.HomeFragment
import com.example.radiotoday.utils.OnBackAction

class ProfileActivity : AppCompatActivity(){
    private lateinit var binding: ActivityProfileBinding
    lateinit var name : String
    private lateinit var email : String
    private lateinit var phone : String
    private lateinit var address : String

    private var homeFragment = HomeFragment()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolBarBackIcon.setOnClickListener {
            onBackPressed()
        }


        binding.inputName.setText("Alex Smith")
        binding.inputEmail.setText("smith@gmail.com")
        binding.inputPhone.setText("002345678900")
        binding.inputAddress.setText("Vicotoriya Lane, Toronto, Canada")

        name = binding.inputName.text.toString()
        email = binding.inputEmail.text.toString()
        phone = binding.inputPhone.text.toString()
        address = binding.inputAddress.text.toString()

        binding.ivEdit.setOnClickListener {
            binding.ivCloseEdit.visibility = View.VISIBLE
            binding.ivEdit.visibility = View.GONE
            binding.inputName.isEnabled = true
            binding.inputEmail.isEnabled = true
            binding.inputPhone.isEnabled = true
            binding.inputAddress.isEnabled = true

            binding.btnSave.visibility = View.VISIBLE
        }

        binding.ivCloseEdit.setOnClickListener {
            binding.ivCloseEdit.visibility = View.GONE
            binding.ivEdit.visibility = View.VISIBLE
            binding.inputName.isEnabled = false
            binding.inputEmail.isEnabled = false
            binding.inputPhone.isEnabled = false
            binding.inputAddress.isEnabled = false

            Log.i("profile", "onCreate close edit: $name, $email, $phone, $address")

            binding.inputName.setText(name)
            binding.inputEmail.setText(email)
            binding.inputPhone.setText(phone)
            binding.inputAddress.setText(address)

            binding.btnSave.visibility = View.GONE
        }

        binding.btnSave.setOnClickListener {
            binding.ivCloseEdit.visibility = View.GONE
            binding.ivEdit.visibility = View.VISIBLE
            binding.inputName.isEnabled = false
            binding.inputEmail.isEnabled = false
            binding.inputPhone.isEnabled = false
            binding.inputAddress.isEnabled = false

            name = binding.inputName.text.toString()
            binding.tvProfileName.text = name
            email = binding.inputEmail.text.toString()
            phone = binding.inputPhone.text.toString()
            address = binding.inputAddress.text.toString()

            Log.i("profile", "onCreate: $name, $email, $phone, $address")

            binding.btnSave.visibility = View.GONE
        }

        Log.i("profile", "onCreate: $name")

    }

    override fun onBackPressed() {

        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
        finish()
        super.onBackPressed()
    }


    companion object {

        lateinit var onBackAction: OnBackAction
        fun onBackAction(setBackAction: OnBackAction) {
            this.onBackAction = setBackAction
        }

    }

}