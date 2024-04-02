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
import com.example.radiotoday.ui.activities.MainActivity
import com.example.radiotoday.ui.activities.ProfileActivity
import com.example.radiotoday.ui.activities.SettingsInfoActivity
import com.example.radiotoday.ui.viewmodels.LogoutViewModel
import com.example.radiotoday.utils.AppUtils
import com.example.radiotoday.utils.AppUtils.IntroScreenStatus
import com.example.radiotoday.utils.AppUtils.LogInStatus
import com.example.radiotoday.utils.OnBackAction
import com.example.radiotoday.utils.ResultType
import com.example.radiotoday.utils.SharedPreferencesUtil
import com.facebook.login.LoginManager
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

    private val logoutViewModel by viewModels<LogoutViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentSettingsBinding.inflate(layoutInflater, container, false)

        observeLogout()

        binding.toolBarBackIcon.setOnClickListener {
            onBackAction.onBackListener()
        }

        binding.cvProfileImg.setOnClickListener {
            goToProfileActivity()
        }

        binding.layoutSetAlarm.setOnClickListener {
            goToAlarmActivity()
        }

        binding.layoutContactUs.setOnClickListener {
            goToContactUsActivity()
        }

        binding.layoutAbout.setOnClickListener {
            goToSettingsInfoActivity("about_us")
        }

        binding.layoutPrivacy.setOnClickListener {
            goToSettingsInfoActivity("privacy_policy")
        }

        binding.layoutConditions.setOnClickListener {
            goToSettingsInfoActivity("terms_condition")
        }

        binding.layoutFAQS.setOnClickListener {
            goToSettingsInfoActivity("faqs")
        }

        binding.layoutLog.setOnClickListener {
            actionLoginLogout()
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        checkLogInStatus()
    }

    private fun observeLogout() {
        logoutViewModel.logoutData.observe(requireActivity()) {
            when (it) {
                is ResultType.Loading -> {

                }

                is ResultType.Success -> {
                    SharedPreferencesUtil.clear(requireActivity())
                    SharedPreferencesUtil.saveData(requireActivity(), IntroScreenStatus, true)
                    goToMainActivity()
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
    private fun checkLogInStatus() {
        val logInStatusString = SharedPreferencesUtil.getData(requireActivity(), LogInStatus, "false") as String
        val isLoggedIn = logInStatusString.toBoolean()


        if (isLoggedIn) {
            binding.tvLog.text = getString(R.string.logout)
            binding.ivLog.setImageResource(R.drawable.ic_logout)
            binding.cvProfileImg.visibility = View.VISIBLE
        } else {
            binding.tvLog.text = getString(R.string.login)
            binding.ivLog.setImageResource(R.drawable.ic_login)
            binding.cvProfileImg.visibility = View.GONE
        }
    }

    private fun actionLoginLogout() {
        val logInStatusString = SharedPreferencesUtil.getData(requireActivity(), LogInStatus, "false") as String
        val isLoggedIn = logInStatusString.toBoolean()
        if (isLoggedIn) {
            val dialog = AppUtils.setDialog(requireActivity(), R.layout.logout_dialog)

            val btnLogout = dialog.findViewById<Button>(R.id.btnLogout)
            val btnCancel = dialog.findViewById<Button>(R.id.btnCancel)

            btnLogout.setOnClickListener {
                logout()
                dialog.dismiss()
            }

            btnCancel.setOnClickListener { dialog.dismiss() }

        } else {
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
    }

    private fun goToSettingsInfoActivity(type: String) {
        val intent = Intent(requireContext(), SettingsInfoActivity::class.java)
        intent.putExtra("type", type)
        startActivity(intent)
    }

    private fun goToContactUsActivity() {
        val intent = Intent(requireContext(), ContactActivity::class.java)
        startActivity(intent)
    }

    private fun goToAlarmActivity() {
        val intent = Intent(requireContext(), AlarmActivity::class.java)
        startActivity(intent)
    }

    private fun goToProfileActivity() {
        val intent = Intent(requireContext(), ProfileActivity::class.java)
        startActivity(intent)
    }

    private fun logout() {
        //for email login
        val token = SharedPreferencesUtil.getData(requireActivity(), AppUtils.LogInToken, "")
        Log.i("Login", "logout: $token")

        if (token.toString().isNotEmpty()){
            logoutViewModel.fetchLogoutData("Bearer $token")
        }


        //Google Logout
        else if (isSignInClientInitialized()) {

            signInClient.signOut().addOnFailureListener {
                Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
            }.addOnCompleteListener {
                //Toast.makeText(context, "Logout completed!", Toast.LENGTH_SHORT).show()

                SharedPreferencesUtil.clear(requireActivity())
                SharedPreferencesUtil.saveData(requireActivity(), IntroScreenStatus, true)
                goToMainActivity()

                binding.tvLog.text = "Login"
                binding.ivLog.setImageResource(R.drawable.ic_login)
            }


        } else {
            Toast.makeText(requireContext(), "OneTapClient is not initialized", Toast.LENGTH_SHORT).show()
        }

        //Facebook logout
        LoginManager.getInstance().logOut()

    }

    private fun goToMainActivity() {
        val intent = Intent(requireContext(), MainActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
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




