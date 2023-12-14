package com.example.radiotoday.ui.activities

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.radiotoday.R
import com.example.radiotoday.databinding.ActivityLoginBinding
import com.example.radiotoday.utils.AppUtils.LogInStatus
import com.example.radiotoday.utils.SharedPreferencesUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes
import com.google.android.gms.common.api.ApiException

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var dialog: Dialog

    private lateinit var enteredPhone: String
    private lateinit var enteredPassword: String
    private var phoneText: String? = null

    private val _requestCodeSignIn = 1000
    private var gso: GoogleSignInOptions? = null
    private var gsc: GoogleSignInClient? = null

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

        binding.btnLoginWithGoogle.setOnClickListener {
            googleLogIn()
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

    private fun googleLogIn() {
        // Initialize GoogleSignInOptions
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id)) // Use your web client ID
            .requestEmail()
            .build()

        // Initialize GoogleSignInClient
        gsc = GoogleSignIn.getClient(this, gso!!)

        // gsc to get the sign-in intent
        val signInIntent = gsc!!.signInIntent
        startActivityForResult(signInIntent, _requestCodeSignIn)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            _requestCodeSignIn -> {
                if (resultCode == RESULT_OK) {
                    // User successfully signed in
                    val task = gsc!!.silentSignIn()
                    task.addOnCompleteListener { task ->
                        try {
                            if (task.isSuccessful) {
                                // Successful sign-in
                                updateViewWithAccount()
                                Toast.makeText(
                                    this@LoginActivity,
                                    "Google Sign-In Successful!",
                                    Toast.LENGTH_LONG
                                ).show()
                                finish()
                            } else {
                                val exception = task.exception
                                if (exception is ApiException) {
                                    when (exception.statusCode) {
                                        GoogleSignInStatusCodes.SIGN_IN_CANCELLED -> {
                                            Toast.makeText(
                                                this@LoginActivity,
                                                "Google Sign-In was canceled by the user",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                        GoogleSignInStatusCodes.SIGN_IN_FAILED -> {
                                            Toast.makeText(
                                                this@LoginActivity,
                                                "Google Sign-In failed. Please try again later.",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                        else -> {
                                            Toast.makeText(
                                                this@LoginActivity,
                                                "Google Sign-In encountered an error. Please try again later.",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    }
                                }
                            }
                        } catch (apiException: ApiException) {
                            Log.e("GoogleSignIn", "Error during Google Sign-In: ${apiException.statusCode}", apiException)
                            Toast.makeText(
                                this@LoginActivity,
                                "An error occurred during Google Sign-In. Please try again.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        "Google Sign-In failed. Please try again later.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }


    private fun updateViewWithAccount() {
        val acct = GoogleSignIn.getLastSignedInAccount(this)
        if (acct != null) {
            val displayName = acct.displayName
            val personEmail = acct.email
            val firstname = acct.givenName
            val lastname = acct.familyName
            val imageUri = acct.photoUrl
            val userID = acct.id

            //val loginData = SocialLoginData(personEmail.toString(), firstname.toString(), lastname.toString(), imageUri.toString(), displayName.toString())

            Log.i("SignIn", "onActivityResult: $displayName $personEmail, $userID, $firstname, $lastname, $imageUri")

            //SharedPreferencesUtil.saveData(this@LoginActivity, UsernameInputKey, userID ?: "")

            //SharedPreferencesUtil.saveSocialLogInData(this, loginData)

            //SocialmediaLoginUtil().fetchSocialLogInData(this, "google", userID!!, firstname!!, lastname!!, personEmail!!, imageUri.toString())


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