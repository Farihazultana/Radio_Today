package com.example.radiotoday.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.radiotoday.data.models.home.HomeResponse
import com.example.radiotoday.data.repositories.HomeRepository
import com.example.radiotoday.utils.ResultType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val homeRepository: HomeRepository) : ViewModel(){
    private val _homeData = MutableLiveData<ResultType<HomeResponse>>(ResultType.Loading)
    val homeData: LiveData<ResultType<HomeResponse>> = _homeData

    fun fetchHomeData(page_slug: String, from_source: String){
        viewModelScope.launch {
            try {
                val result = homeRepository.getHomeData(page_slug, from_source)
                _homeData.value = result
            }catch (e: Exception){
                _homeData.value = ResultType.Error(e)
                Log.i("Home", "fetchHomeData: $e")
            }
        }
    }
}