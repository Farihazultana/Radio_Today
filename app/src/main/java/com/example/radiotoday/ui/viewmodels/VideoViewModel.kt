package com.example.radiotoday.ui.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.radiotoday.data.models.audio.AudioResponse
import com.example.radiotoday.data.models.video.VideoResponse
import com.example.radiotoday.data.repositories.AudioRepository
import com.example.radiotoday.data.repositories.VideoRepository
import com.example.radiotoday.utils.ResultType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoViewModel @Inject constructor(private val videoRepository: VideoRepository): ViewModel(){
    private val _videoPlaylistData = MutableLiveData<ResultType<VideoResponse>>(ResultType.Loading)
    val videoPlaylistData: MutableLiveData<ResultType<VideoResponse>> = _videoPlaylistData
    fun fetchVideoPlaylistData(userId: String) {
        viewModelScope.launch {
            try {
                val result = videoRepository.getVideoPlaylistData(userId)
                Log.i("TAGY", "fetchVideoPlaylistData: $result")
                _videoPlaylistData.value = result
            } catch (e: Exception) {
                _videoPlaylistData.value = ResultType.Error(e)
            }
        }
    }
}