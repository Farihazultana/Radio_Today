package com.example.radiotoday.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.radiotoday.databinding.FragmentNewsBinding
import com.example.radiotoday.utils.OnBackAction
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient


class NewsFragment : Fragment() {
    private lateinit var binding: FragmentNewsBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewsBinding.inflate(layoutInflater,container,false)

        binding.toolBarBackIcon.setOnClickListener {
            onBackAction.onBackListener()
        }

        return binding.root
    }

    companion object{

        lateinit var onBackAction: OnBackAction
        fun onBackAction(setBackAction: OnBackAction){
            this.onBackAction = setBackAction
        }

    }


}