package com.example.radiotoday.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.radiotoday.data.models.ForgetResposnse
import com.example.radiotoday.data.models.MainResponse
import com.example.radiotoday.data.repositories.ForgetPasswordRepository
import com.example.radiotoday.utils.ResultType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgetPasswordViewModel @Inject constructor(private val forgetRepository: ForgetPasswordRepository): ViewModel(){
    private val _forgetPasswordData = MutableLiveData<ResultType<ForgetResposnse>>(ResultType.Loading)
    val forgetPasswordData: LiveData<ResultType<ForgetResposnse>> = _forgetPasswordData

    fun fetchForgetPasswordData(email: String){
        viewModelScope.launch {
            try {
                val result = forgetRepository.getForgetPasswordData(email)
                Log.i("TAG", "fetchForgetPasswordData: $result")

                _forgetPasswordData.value = result
            }catch (e: Exception){
                _forgetPasswordData.value = ResultType.Error(e)
            }
        }
    }
}