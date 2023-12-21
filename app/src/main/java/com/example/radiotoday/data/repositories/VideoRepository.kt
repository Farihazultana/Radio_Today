package com.example.radiotoday.data.repositories

import android.util.Log
import com.example.radiotoday.data.models.audio.AudioResponse
import com.example.radiotoday.data.models.video.VideoResponse
import com.example.radiotoday.data.services.ApiServices
import com.example.radiotoday.utils.ResultType
import javax.inject.Inject

class VideoRepository  @Inject constructor(private val apiServices: ApiServices){
    suspend fun getVideoPlaylistData(userId : String) : ResultType<VideoResponse> {

        try {
            val response = apiServices.postVideoPlaylistData(userId)
            Log.i("TAGY", "successful api call: ${response.code()}")

            if (response.isSuccessful) {
                val data = response.body()
                if (data != null) {
                    Log.i("TAGY", "getVideoPlaylistData: ${data.catname}")
                }
                if (data != null) {
                    return ResultType.Success(data)
                }
            }

            return ResultType.Error(Exception("Failed to fetch VideoPlaylist data"))

        }catch (e: Exception){
            return ResultType.Error(e)
        }
    }

}