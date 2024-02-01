package com.example.radiotoday.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.radiotoday.R
import com.example.radiotoday.databinding.ActivityLoginBinding
import com.example.radiotoday.ui.viewmodels.LoginViewModel
import com.example.radiotoday.utils.AppUtils
import com.example.radiotoday.utils.AppUtils.LogInStatus
import com.example.radiotoday.utils.ResultType
import com.example.radiotoday.utils.SharedPreferencesUtil
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.FacebookSdk
import com.facebook.GraphRequest
import com.facebook.appevents.AppEventsLogger
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes
import com.google.android.gms.common.api.ApiException
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONException

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel by viewModels<LoginViewModel>()

    private lateinit var enteredPhone: String
    private lateinit var enteredPassword: String
    private var phoneText: String? = null

    private val myIntent by lazy { Intent(this, MainActivity::class.java) }

    private val _requestCodeSignIn = 1000
    private var gso: GoogleSignInOptions? = null
    private var gsc: GoogleSignInClient? = null

    private val callbackManager = CallbackManager.Factory.create()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {

            phoneLoginValidation()

            val loginResult = SharedPreferencesUtil.getData(this,LogInStatus, "")

            if (loginResult == "successful"){

                myIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(myIntent)

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

        FacebookSdk.sdkInitialize(applicationContext)
        AppEventsLogger.activateApp(application)
        binding.btnLoginWithFB.setOnClickListener {
            facebookLoginIntegration()
            LoginManager.getInstance().logInWithReadPermissions(this, listOf("public_profile"))
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
            AppUtils.setDialog(this, R.layout.dialog_forget_password)
        }

    }

    private fun phoneLoginValidation() {
        enteredPhone = binding.inputUsername.text.toString().trim()
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        enteredPassword = binding.inputPassword.text.toString()

        if (enteredPhone.matches(emailPattern.toRegex())) {

            Toast.makeText(this,"Valid email address", Toast.LENGTH_SHORT).show()

            loginViewModel.fetchLoginData(enteredPhone, enteredPassword)
            loginViewModel.loginData.observe(this){
                when(it){
                    is ResultType.Loading -> {

                    }

                    is ResultType.Success -> {
                        val logInResult = it.data.content
                        if(logInResult.token != null){
                            SharedPreferencesUtil.saveData(this, LogInStatus, "successful")
                        }

                    }

                    is ResultType.Error -> {

                    }
                }
            }

        } else {

            SharedPreferencesUtil.saveData(this, LogInStatus, "fail")
            Toast.makeText(this,"Invalid email address", Toast.LENGTH_SHORT).show()

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

    private fun facebookLoginIntegration() {
        // Facebook login callback
        LoginManager.getInstance().registerCallback(callbackManager, object :
            FacebookCallback<LoginResult> {
            override fun onCancel() {
                Log.i("FacebookProfile", "onCancel: ")
            }

            override fun onError(error: FacebookException) {
                Log.i("FacebookProfile", "onError: $error")
            }

            override fun onSuccess(result: LoginResult) {
                // Handle successful login
                // After successful login, retrieve the user's profile information
                Log.i("FacebookProfile", "onSuccess: ")
                val accessToken = AccessToken.getCurrentAccessToken()
                if (accessToken != null && !accessToken.isExpired) {
                    // Request the user's profile information
                    val request = GraphRequest.newMeRequest(accessToken) { obj, response ->
                        Log.i("FacebookProfile", "onSuccess: $response")
                        if (response?.error == null) {
                            try {
                                val id = obj?.getString("id").toString()
                                //val email = obj?.getString("email")
                                val fullname = obj?.getString("name").toString()
                                //val birthday = obj?.getString("birthday")

                                // Access the profile picture URL
                                val pictureObj = obj?.getJSONObject("picture")
                                val pictureData = pictureObj?.getJSONObject("data")
                                val profileImage = pictureData?.getString("url").toString()

                                // Log the profile information
                                Log.d("FacebookProfile", "ID: $id, Name: $fullname, Email: , Image URL: $profileImage")

                                /*SharedPreferencesUtil.saveData(this@LoginActivity, UsernameInputKey, id ?: "")
                                *//*SharedPreferencesUtil.saveData(this@LoginActivity, AppUtils.FBSignIN_Fullname, fullname ?: "")
                                SharedPreferencesUtil.saveData(this@LoginActivity, AppUtils.FBSignIn_ImgUri, profileImage ?: "")*//*

                                val loginData = SocialLoginData("", "", "", profileImage, fullname)
                                SharedPreferencesUtil.saveSocialLogInData(this@LoginActivity, loginData)

                                SocialmediaLoginUtil().fetchSocialLogInData(this@LoginActivity, "facebook",id,fullname, "","", profileImage )*/

                            }catch (e : JSONException){
                                Log.e("FacebookProfile", "onSuccess: $e")
                            }

                        }else {
                            Log.e("FacebookProfile", "Error in GraphRequest: ${response.error}")
                        }
                    }
                    val parameters = Bundle()
                    parameters.putString("fields", "id,name,email,picture.type(large)")
                    request.parameters = parameters
                    request.executeAsync()

                }
            }
        })
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
                                //finish()
                                myIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                                startActivity(myIntent)
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

        //facebook
        callbackManager.onActivityResult(requestCode, resultCode, data);
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

}