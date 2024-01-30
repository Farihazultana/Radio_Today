package com.example.radiotoday.ui.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.radiotoday.data.models.showDetails.ShowDetailsResponse
import com.example.radiotoday.data.repositories.ShowDetailsRepository
import com.example.radiotoday.utils.ResultType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShowDetailsViewModel @Inject constructor(private val showDetailsRepository: ShowDetailsRepository): ViewModel(){
    private val _showDetailsPlaylistData  = MutableLiveData<ResultType<ShowDetailsResponse>>(ResultType.Loading)
    val showDetailsPlaylistData: MutableLiveData<ResultType<ShowDetailsResponse>> = _showDetailsPlaylistData
    fun fetchShowDetailsPlaylistData(sectionCode : String, id : String, empty : String) {
        viewModelScope.launch {
            try {
                val result = showDetailsRepository.getShowDetailsPlaylistData(sectionCode, id, empty)
                Log.i("TAGY", "fetchShowDetailsData: $result")
                _showDetailsPlaylistData.value = result
            } catch (e: Exception) {
                _showDetailsPlaylistData.value = ResultType.Error(e)
            }
        }
    }
}