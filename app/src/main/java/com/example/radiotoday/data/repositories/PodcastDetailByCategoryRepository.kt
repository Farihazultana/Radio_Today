package com.example.radiotoday.data.repositories

import android.util.Log
import com.example.radiotoday.data.models.MainResponse
import com.example.radiotoday.data.services.ApiServices
import com.example.radiotoday.utils.ResultType
import javax.inject.Inject

class PodcastDetailByCategoryRepository @Inject constructor(private val apiServices: ApiServices){
    suspend fun getPodcastDetailByCategoryData(type: String, page: String): ResultType<MainResponse> {
        try {
            val response = apiServices.postPosdcastDetailByCategoryData(type, page)
            Log.i("TAGS", "successful api call: ${response.code()}")
            if (response.isSuccessful) {
                val data = response.body()
                if (data != null) {
                    Log.i("TAGS", "getPodcastDetailData: ${data.content}")
                }
                if (data != null) {
                    return ResultType.Success(data)
                }
            }
            return ResultType.Error(Exception("Failed to fetch Podcast Detail data"))
        } catch (e: Exception) {
            return ResultType.Error(e)
        }
    }
}