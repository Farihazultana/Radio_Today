package com.example.radiotoday.ui.activities

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.radiotoday.R
import com.example.radiotoday.databinding.ActivityLoginBinding
import com.example.radiotoday.utils.AppUtils.LogInStatus
import com.example.radiotoday.utils.SharedPreferencesUtil

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var dialog: Dialog

    private lateinit var enteredPhone: String
    private lateinit var enteredPassword: String
    private var phoneText: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {

            phoneLoginValidation()

            val loginResult = SharedPreferencesUtil.getData(this,LogInStatus, "")

            if (loginResult == "successful"){
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)

                Toast.makeText(
                    this@LoginActivity,
                    "Login Successful",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }



        binding.tvRegister.setOnClickListener {
            binding.layoutLogin.visibility = View.GONE
            binding.layoutRegistration.visibility = View.VISIBLE
        }
        binding.tvRegRegister.setOnClickListener {
            binding.layoutLogin.visibility = View.VISIBLE
            binding.layoutRegistration.visibility = View.GONE
        }


        binding.tvForget.setOnClickListener {
            setDialog()
            dialog.show()
        }

    }

    private fun phoneLoginValidation() {
        enteredPhone = binding.inputUsername.text.toString()
        enteredPassword = binding.inputPassword.text.toString()

        if (enteredPhone.isNotEmpty() && enteredPassword.isNotEmpty() && enteredPhone.length == 11) {
            phoneText = "88$enteredPhone"

            SharedPreferencesUtil.saveData(this, LogInStatus, "successful")
        } else {
            if (enteredPhone.isEmpty() || enteredPassword.isEmpty()) {
                SharedPreferencesUtil.saveData(this, LogInStatus, "fail")
                Toast.makeText(
                    this@LoginActivity,
                    "Phone number or Password must not be empty! Please input first.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                SharedPreferencesUtil.saveData(this, LogInStatus, "fail")
                Toast.makeText(
                    this@LoginActivity,
                    "Phone number should be 11 digits",
                    Toast.LENGTH_LONG
                ).show()

            }
        }
    }

    private fun setDialog() {
        dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_forget_password)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.setCancelable(true)
        dialog.window!!.attributes!!.windowAnimations = R.style.animation

    }
}