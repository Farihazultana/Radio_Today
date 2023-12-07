package com.example.radiotoday.ui.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.radiotoday.data.models.audio.AudioResponse
import com.example.radiotoday.data.models.seeMore.SeeMoreResponse
import com.example.radiotoday.data.repositories.SeeMoreRepository
import com.example.radiotoday.utils.ResultType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SeeMoreViewModel @Inject constructor(private val seeMoreRepository: SeeMoreRepository): ViewModel(){
    private val _seeMorePlaylistData  = MutableLiveData<ResultType<SeeMoreResponse>>(ResultType.Loading)
    val seeMorePlaylistData: MutableLiveData<ResultType<SeeMoreResponse>> = _seeMorePlaylistData
    fun fetchSeeMorePlaylistData(albumcode : String) {
        viewModelScope.launch {
            try {
                val result = seeMoreRepository.getSeeMorePlaylistData(albumcode )
                Log.i("TAGY", "fetchAudioPlaylistData: $result")
                _seeMorePlaylistData.value = result
            } catch (e: Exception) {
                _seeMorePlaylistData.value = ResultType.Error(e)
            }
        }
    }
}