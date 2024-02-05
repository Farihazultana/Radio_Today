package com.example.radiotoday.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.radiotoday.data.models.MainResponse
import com.example.radiotoday.data.repositories.SettingsRepository
import com.example.radiotoday.utils.ResultType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(private val settingsRepository: SettingsRepository): ViewModel(){
    private val _settingsData = MutableLiveData<ResultType<MainResponse>>(ResultType.Loading)
    val settingsData: LiveData<ResultType<MainResponse>> = _settingsData

    fun fetchSettingsData(type: String){
        viewModelScope.launch {
            try {
                val result = settingsRepository.getSettingsData(type)
                _settingsData.value = result
            }catch (e: Exception){
                _settingsData.value = ResultType.Error(e)
                Log.i("Login", "fetchSettingsData: $e")
            }
        }
    }
}