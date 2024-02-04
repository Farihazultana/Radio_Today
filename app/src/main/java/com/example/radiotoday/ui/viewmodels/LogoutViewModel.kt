package com.example.radiotoday.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.radiotoday.data.models.login.LoginResponse
import com.example.radiotoday.data.repositories.LogoutRepository
import com.example.radiotoday.utils.ResultType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class LogoutViewModel @Inject constructor(private val logoutRepository: LogoutRepository): ViewModel() {
    private val _logoutData = MutableLiveData<ResultType<LoginResponse>>(ResultType.Loading)
    val logoutData: LiveData<ResultType<LoginResponse>> = _logoutData

    fun fetchLogoutData(token: String){
        viewModelScope.launch {
            try {
                val result = logoutRepository.getLogoutData(token)
                _logoutData.value = result
            }catch (e: Exception){
                _logoutData.value = ResultType.Error(e)
                Log.i("Login", "fetchLogoutData: $e")
            }
        }
    }

}