package com.example.radiotoday.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.example.radiotoday.R
import com.example.radiotoday.data.models.ErrorResponse
import com.example.radiotoday.databinding.FragmentSettingsBinding
import com.example.radiotoday.ui.activities.AlarmActivity
import com.example.radiotoday.ui.activities.ContactActivity
import com.example.radiotoday.ui.activities.LoginActivity
import com.example.radiotoday.ui.activities.ProfileActivity
import com.example.radiotoday.ui.viewmodels.LogoutViewModel
import com.example.radiotoday.utils.AppUtils
import com.example.radiotoday.utils.AppUtils.LogInStatus
import com.example.radiotoday.utils.OnBackAction
import com.example.radiotoday.utils.ResultType
import com.example.radiotoday.utils.SharedPreferencesUtil
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint
import java.lang.reflect.Type
@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding
    private lateinit var signInClient: SignInClient

    private val logoutViewModel by viewModels<LogoutViewModel> ()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(layoutInflater,container, false)


        binding.toolBarBackIcon.setOnClickListener {
            onBackAction.onBackListener()
        }

        profileImgClickOn()

        setAlarmClickOn()

        contactUsClickOn()

        observeLogout()

        checkLogInStatus()

        binding.layoutLog.setOnClickListener {


            if (binding.tvLog.text == "Logout"){
                val dialog =  AppUtils.setDialog(requireActivity(), R.layout.logout_dialog)

                val btnLogout = dialog.findViewById<Button>(R.id.btnLogout)
                val btnCancel = dialog.findViewById<Button>(R.id.btnCancel)


                btnLogout.setOnClickListener {
                    logout()

                    dialog.dismiss()
                }


                btnCancel.setOnClickListener { dialog.dismiss() }
            }else if (binding.tvLog.text == "Login"){

                val intent = Intent(requireContext(), LoginActivity::class.java)

                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                //binding.tvLog.text = "Logout"

            }

        }

        return binding.root
    }

    private fun checkLogInStatus() {
        val loginStatus =
            SharedPreferencesUtil.getData(requireContext(), LogInStatus, false) as Boolean
        Log.i("Login", "onCreateView: $loginStatus")
        if (loginStatus) {
            //now User is Logged in so, needs to Logout

            binding.tvLog.text = "Logout"
        } else {
            binding.tvLog.text = "Login"
        }
    }

    private fun contactUsClickOn() {
        binding.layoutContactUs.setOnClickListener {
            val intent = Intent(requireContext(), ContactActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setAlarmClickOn() {
        binding.layoutSetAlarm.setOnClickListener {
            val intent = Intent(requireContext(), AlarmActivity::class.java)
            startActivity(intent)
        }
    }

    private fun profileImgClickOn() {
        binding.cvProfileImg.setOnClickListener {
            val intent = Intent(requireContext(), ProfileActivity::class.java)
            startActivity(intent)
        }
    }

    private fun logout(){
        val token = SharedPreferencesUtil.getData(requireActivity(), AppUtils.LogInToken, "")
        Log.i("Login", "logout: $token")
        logoutViewModel.fetchLogoutData("Bearer $token")

        //Google Logout
        /*if (isSignInClientInitialized()) {

            signInClient.signOut().addOnFailureListener {
                Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
            }.addOnCompleteListener {
                Toast.makeText(context, "Logout completed!", Toast.LENGTH_SHORT).show()
                binding.tvLog.text = "Login"
                binding.ivLog.setImageResource(R.drawable.ic_login)
            }


        } else {
            Toast.makeText(requireContext(), "OneTapClient is not initialized", Toast.LENGTH_SHORT)
                .show()
        }

        //Facebook logout
        LoginManager.getInstance().logOut()*/
    }

    private fun observeLogout() {
        logoutViewModel.logoutData.observe(requireActivity()) {
            when (it) {
                is ResultType.Loading -> {

                }

                is ResultType.Success -> {
                    val logInResponse = it.data
                    Toast.makeText(requireActivity(), logInResponse.message, Toast.LENGTH_SHORT)
                        .show()

                    binding.tvLog.text = "Login"
                    binding.ivLog.setImageResource(R.drawable.ic_login)
                }

                is ResultType.Error -> {
                    val gson = Gson()
                    val type: Type = object : TypeToken<ErrorResponse?>() {}.type
                    val errorResponse = gson.fromJson<ErrorResponse>(it.exception.message, type)

                    Toast.makeText(requireContext(), errorResponse.message, Toast.LENGTH_SHORT).show()


                }
            }
        }
    }

    private fun isSignInClientInitialized(): Boolean {
        return try {
            signInClient = Identity.getSignInClient(requireActivity())
            return true
        } catch (e: UninitializedPropertyAccessException) {
            false
        }
    }

    companion object{

        lateinit var onBackAction: OnBackAction
        fun onBackAction(setBackAction: OnBackAction){
            this.onBackAction = setBackAction
        }

    }


}