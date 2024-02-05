package com.example.radiotoday.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.radiotoday.data.models.MainResponse
import com.example.radiotoday.data.repositories.LogInRepository
import com.example.radiotoday.utils.ResultType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val logInRepository: LogInRepository): ViewModel() {
    private val _loginData = MutableLiveData<ResultType<MainResponse>>(ResultType.Loading)
    val loginData: LiveData<ResultType<MainResponse>> = _loginData

    fun fetchLoginData(email: String, password: String){
        viewModelScope.launch {
            try {
                val result = logInRepository.getLogInData(email, password)
                _loginData.value = result
            }catch (e: Exception){
                _loginData.value = ResultType.Error(e)
                Log.i("Login", "fetchLoginData: $e")
            }
        }
    }

}