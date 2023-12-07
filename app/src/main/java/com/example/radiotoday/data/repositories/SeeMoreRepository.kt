package com.example.radiotoday.data.repositories

import android.util.Log
import com.example.radiotoday.data.models.seeMore.SeeMoreResponse
import com.example.radiotoday.data.services.ApiServices
import com.example.radiotoday.utils.ResultType
import javax.inject.Inject

class SeeMoreRepository @Inject constructor(private val apiServices: ApiServices){
    suspend fun getSeeMorePlaylistData(albumcode : String) : ResultType<SeeMoreResponse>{
        try {
            val response = apiServices.postSeeMoreData(albumcode)
            Log.i("TAGY", "successful api call: ${response.code()}")
            if (response.isSuccessful) {
                val data = response.body()
                if (data != null) {
                    Log.i("TAGY", "getAudioPlaylistData: ${data[0].image_location}")
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