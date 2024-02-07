package com.example.radiotoday.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.radiotoday.data.models.MainResponse
import com.example.radiotoday.data.repositories.PodcastDetailByCategoryRepository
import com.example.radiotoday.utils.ResultType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PodcastDetailByCategoryViewModel @Inject constructor(private val repository: PodcastDetailByCategoryRepository): ViewModel() {
    private val _podcastDetailByCategoryData = MutableLiveData<ResultType<MainResponse>>(ResultType.Loading)
    val podcastDetailByCategoryData : LiveData<ResultType<MainResponse>> = _podcastDetailByCategoryData
    fun fetchPodcastDetailByCategoryData(type: String, page: String){
        viewModelScope.launch {
            try {
                val result = repository.getPodcastDetailByCategoryData(type, page)
                _podcastDetailByCategoryData.value = result
                Log.i("SeeAll", "fetchPodcastDetailByCategoryData: $result")
            } catch (e: Exception) {
                _podcastDetailByCategoryData.value = ResultType.Error(e)
            }
        }
    }
}