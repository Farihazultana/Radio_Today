package com.example.radiotoday.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.radiotoday.databinding.FragmentNewsBinding
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient


class NewsFragment : Fragment() {
    private lateinit var binding: FragmentNewsBinding

    private lateinit var signInClient: SignInClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewsBinding.inflate(layoutInflater,container,false)

        binding.btnLogout.setOnClickListener {
            if (isSignInClientInitialized()) {

                signInClient.signOut().addOnFailureListener {
                    Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
                }.addOnCompleteListener {
                    Toast.makeText(context, "Logout completed!", Toast.LENGTH_SHORT).show()
                }


            } else {
                Toast.makeText(requireContext(), "OneTapClient is not initialized", Toast.LENGTH_SHORT)
                    .show()
            }
        }


        return binding.root
    }

    private fun isSignInClientInitialized(): Boolean {
        return try {
            signInClient = Identity.getSignInClient(requireActivity())
            return true
        } catch (e: UninitializedPropertyAccessException) {
            false
        }
    }

}