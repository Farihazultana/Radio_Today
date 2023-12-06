package com.example.radiotoday.data.repositories

import android.util.Log
import com.example.radiotoday.data.models.audio.AudioResponse
import com.example.radiotoday.data.services.ApiServices
import com.example.radiotoday.utils.ResultType
import javax.inject.Inject

class AudioRepository @Inject constructor(private val apiServices: ApiServices){

    suspend fun getAudioPlaylistData(userId : String) : ResultType<AudioResponse> {

        try {
            val response = apiServices.postAudioPlaylistData(userId)
            Log.i("TAGY", "successful api call: ${response.code()}")
            if (response.isSuccessful) {
                val data = response.body()
                if (data != null) {
                    Log.i("TAGY", "getAudioPlaylistData: ${data.catname}")
                }
                if (data != null) {
                    return ResultType.Success(data)
                }
            }
            return ResultType.Error(Exception("Failed to fetch AudioPlaylist data"))

        }catch (e: Exception){
            return ResultType.Error(e)
        }
    }

}