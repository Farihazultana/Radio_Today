package com.example.radiotoday.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.radiotoday.data.models.home.HomeResponse
import com.example.radiotoday.data.repositories.SeeAllPodcastRepository
import com.example.radiotoday.utils.ResultType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SeeAllPodcastViewModel @Inject constructor(private val repository: SeeAllPodcastRepository) : ViewModel() {
    private val _seeAllPodcastData = MutableLiveData<ResultType<HomeResponse>>(ResultType.Loading)
    val seeAllPodcastData : LiveData<ResultType<HomeResponse>> = _seeAllPodcastData
    fun fetchSeeAllPodcastData(section_code: String){
        viewModelScope.launch {
            try {
                val result = repository.getSeeAllPodcastData(section_code)
                _seeAllPodcastData.value = result
                Log.i("SeeAllPodcast", "fetchSeeAllPodcastData: $result")
            } catch (e: Exception) {
                _seeAllPodcastData.value = ResultType.Error(e)
            }
        }
    }

}