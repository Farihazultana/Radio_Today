package com.example.radiotoday.ui.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.radiotoday.data.models.audio.AudioResponse
import com.example.radiotoday.data.repositories.AudioRepository
import com.example.radiotoday.utils.ResultType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AudioViewModel @Inject constructor(private val audioRepository: AudioRepository):ViewModel(){
    private val _audioPlaylistData = MutableLiveData<ResultType<AudioResponse>>(ResultType.Loading)
    val audioPlaylistData: MutableLiveData<ResultType<AudioResponse>> = _audioPlaylistData
    fun fetchAudioPlaylistData(userId: String) {
        viewModelScope.launch {
            try {
                val result = audioRepository.getAudioPlaylistData(userId)
                Log.i("TAGY", "fetchAudioPlaylistData: $result")
                _audioPlaylistData.value = result
            } catch (e: Exception) {
                _audioPlaylistData.value = ResultType.Error(e)
            }
        }
    }
}