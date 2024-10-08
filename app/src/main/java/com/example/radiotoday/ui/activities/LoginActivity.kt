package com.example.radiotoday.ui.activities

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.radiotoday.R
import com.example.radiotoday.data.models.ErrorResponse
import com.example.radiotoday.databinding.ActivityLoginBinding
import com.example.radiotoday.ui.viewmodels.ForgetPasswordViewModel
import com.example.radiotoday.ui.viewmodels.LoginViewModel
import com.example.radiotoday.ui.viewmodels.RegistrationViewModel
import com.example.radiotoday.utils.AppUtils
import com.example.radiotoday.utils.AppUtils.LogInStatus
import com.example.radiotoday.utils.AppUtils.LogInToken
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
import com.google.android.gms.common.api.Result
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.json.JSONException
import java.lang.reflect.Type


@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    private val loginViewModel by viewModels<LoginViewModel>()
    private val registrationViewModel by viewModels<RegistrationViewModel>()
    private val forgetPasswordViewModel by viewModels<ForgetPasswordViewModel>()

    private lateinit var enteredPhone: String
    private lateinit var enteredPassword: String
    private lateinit var enteredConfirmedpassword: String
    private lateinit var enteredName: String
    private lateinit var enteredEmail: String

    private lateinit var dialog: Dialog

    private val myIntent by lazy { Intent(this, MainActivity::class.java) }

    private val _requestCodeSignIn = 1000
    private var gso: GoogleSignInOptions? = null
    private var gsc: GoogleSignInClient? = null

    private val callbackManager = CallbackManager.Factory.create()


    private var loginObserve = false
    private var signUpObserve = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        observeEmailLogin()
        binding.btnLogin.setOnClickListener {
            handleLogin()
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

        observeRegistration()
        binding.btnRegistration.setOnClickListener {
            handleRegistration()
        }

        binding.tvLogin.setOnClickListener {
            binding.layoutLogin.visibility = View.VISIBLE
            binding.layoutRegistration.visibility = View.GONE
        }

        forgetPasswordObserve()
        binding.tvForget.setOnClickListener {
            dialog = AppUtils.setDialog(this, R.layout.dialog_forget_password)
            val btnSenRequest = dialog.findViewById<Button>(R.id.btnSendRequest)
            btnSenRequest.setOnClickListener {
                val enteredEmail = dialog.findViewById<TextInputEditText>(R.id.input_Reg_username).text.toString()
                if (enteredEmail.isNotEmpty()){
                    forgetPasswordApiCall(enteredEmail)
                }
            }
        }

    }

    private fun forgetPasswordApiCall(emailText: String){
        forgetPasswordViewModel.fetchForgetPasswordData(emailText)
    }

    private fun forgetPasswordObserve(){
        lifecycleScope.launch {
            forgetPasswordViewModel.forgetPasswordData.observe(this@LoginActivity){
                when(it){
                    is ResultType.Success -> {
                        val result = it.data
                        Toast.makeText(this@LoginActivity, result.message, Toast.LENGTH_SHORT).show()

                    }
                    is ResultType.Error -> {

                    }
                    else ->{

                    }
                }
            }
        }
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
                                //Toast.makeText(this@LoginActivity, "Google Sign-In Successful!", Toast.LENGTH_LONG).show()
                                //finish()
                                SharedPreferencesUtil.saveData(this, LogInStatus, true)

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
                            Log.e(
                                "GoogleSignIn",
                                "Error during Google Sign-In: ${apiException.statusCode}",
                                apiException
                            )
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

            Log.i(
                "SignIn",
                "onActivityResult: $displayName $personEmail, $userID, $firstname, $lastname, $imageUri"
            )

            //SharedPreferencesUtil.saveData(this@LoginActivity, UsernameInputKey, userID ?: "")

            //SharedPreferencesUtil.saveSocialLogInData(this, loginData)

            //SocialmediaLoginUtil().fetchSocialLogInData(this, "google", userID!!, firstname!!, lastname!!, personEmail!!, imageUri.toString())


        }
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
                                Log.d(
                                    "FacebookProfile",
                                    "ID: $id, Name: $fullname, Email: , Image URL: $profileImage"
                                )

                                /*SharedPreferencesUtil.saveData(this@LoginActivity, UsernameInputKey, id ?: "")
                                *//*SharedPreferencesUtil.saveData(this@LoginActivity, AppUtils.FBSignIN_Fullname, fullname ?: "")
                                SharedPreferencesUtil.saveData(this@LoginActivity, AppUtils.FBSignIn_ImgUri, profileImage ?: "")*//*

                                val loginData = SocialLoginData("", "", "", profileImage, fullname)
                                SharedPreferencesUtil.saveSocialLogInData(this@LoginActivity, loginData)

                                SocialmediaLoginUtil().fetchSocialLogInData(this@LoginActivity, "facebook",id,fullname, "","", profileImage )*/

                            } catch (e: JSONException) {
                                Log.e("FacebookProfile", "onSuccess: $e")
                            }

                        } else {
                            Log.e("FacebookProfile", "Error in GraphRequest: ${response.error}")
                        }
                    }
                    val parameters = Bundle()
                    parameters.putString("fields", "id,name,email,picture.type(large)")
                    request.parameters = parameters
                    request.executeAsync()

                    SharedPreferencesUtil.saveData(this@LoginActivity, LogInStatus, true)

                    myIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(myIntent)

                }
            }
        })
    }

    private fun handleLogin() {
        enteredEmail = binding.inputUsername.text.toString()

        enteredPassword = binding.inputPassword.text.toString()
        loginViewModel.fetchLoginData(enteredEmail, enteredPassword)

        loginObserve = true

    }

    private fun observeEmailLogin() {
        loginViewModel.loginData.observe(this) {
            when (it) {
                is ResultType.Loading -> {
                    if (loginObserve){
                        binding.pbLogin.visibility = View.VISIBLE
                    }

                }

                is ResultType.Success -> {
                    binding.pbLogin.visibility = View.GONE

                    val logInResponse = it.data
                    //Toast.makeText(this, logInResponse.message, Toast.LENGTH_SHORT).show()

                    SharedPreferencesUtil.saveData(this, LogInStatus, true)
                    SharedPreferencesUtil.saveData(this, LogInToken, logInResponse.content.token)
                    Log.i("Login", "observeEmailLogin: ${logInResponse.content.token}")


                    myIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(myIntent)

                }

                is ResultType.Error -> {
                    binding.pbLogin.visibility = View.GONE

                    val gson = Gson()
                    val type: Type = object : TypeToken<ErrorResponse?>() {}.type
                    val errorResponse = gson.fromJson<ErrorResponse>(it.exception.message, type)

                    Toast.makeText(this, errorResponse.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun handleRegistration() {
        enteredName = binding.inputRegName.text.toString()
        enteredEmail = binding.inputRegUsername.text.toString()
        enteredPhone = binding.inputRegPhone.text.toString()
        enteredPassword = binding.inputRegPassword.text.toString()
        enteredConfirmedpassword = binding.inputRegReEnterPassword.text.toString()

        if(enteredName.length < 3){
            Toast.makeText(this, "Please Enter a Valid Name", Toast.LENGTH_LONG).show()
        }else{
            registrationViewModel.fetchRegistrationData(enteredName, enteredEmail, enteredPhone, enteredPassword, enteredConfirmedpassword)
        }


        signUpObserve = true

    }

    private fun observeRegistration() {
        registrationViewModel.registrationData.observe(this) {
            when (it) {
                is ResultType.Loading -> {
                    if (signUpObserve){
                        binding.pbRegistration.visibility = View.VISIBLE
                    }

                }

                is ResultType.Success -> {
                    binding.pbRegistration.visibility = View.GONE

                    val logInResponse = it.data
                    Toast.makeText(this, logInResponse.message, Toast.LENGTH_SHORT).show()

                    binding.layoutRegistration.visibility = View.GONE
                    binding.layoutLogin.visibility = View.VISIBLE

                }

                is ResultType.Error -> {
                    binding.pbRegistration.visibility = View.GONE

                    val gson = Gson()
                    val type: Type = object : TypeToken<ErrorResponse?>() {}.type
                    val errorResponse = gson.fromJson<ErrorResponse>(it.exception.message, type)

                    Toast.makeText(this, errorResponse.message, Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

    private fun emailValidation(enteredEmail: String): Boolean {
        val email = enteredEmail.trim()
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        return email.matches(emailPattern.toRegex())
    }

}