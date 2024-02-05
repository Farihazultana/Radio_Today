package com.example.radiotoday.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.radiotoday.data.models.MainResponse
import com.example.radiotoday.data.repositories.RegistrationRepository
import com.example.radiotoday.utils.ResultType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(private val registrationRepository: RegistrationRepository): ViewModel(){
    private val _registrationData = MutableLiveData<ResultType<MainResponse>>(ResultType.Loading)
    val registrationData: LiveData<ResultType<MainResponse>> = _registrationData

    fun fetchRegistrationData(name: String, email:String, phone: String, password: String, password_confirmation: String){
        viewModelScope.launch {
            try {
                val result = registrationRepository.getRegistrationData(name,email,phone,password, password_confirmation)
                _registrationData.value = result
            }catch (e: Exception){
                _registrationData.value = ResultType.Error(e)
                Log.i("Login", "fetchRegistrationData: $e")
            }
        }
    }
}