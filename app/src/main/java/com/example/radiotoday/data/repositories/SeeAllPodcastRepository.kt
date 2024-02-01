package com.example.radiotoday.data.repositories

import android.util.Log
import com.example.radiotoday.data.models.home.HomeResponse
import com.example.radiotoday.data.services.ApiServices
import com.example.radiotoday.utils.ResultType
import javax.inject.Inject

class SeeAllPodcastRepository @Inject constructor(private val apiServices: ApiServices){

    suspend fun getSeeAllPodcastData(section_code: String): ResultType<HomeResponse> {

        try {
            val response = apiServices.postSeeAllPosdcastData(section_code, "")
            Log.i("TAGS", "successful api call: ${response.code()}")
            if (response.isSuccessful) {
                val data = response.body()
                if (data != null) {
                    Log.i("TAGS", "getSeeAllPodcastData: ${data.content}")
                }
                if (data != null) {
                    return ResultType.Success(data)
                }
            }
            return ResultType.Error(Exception("Failed to fetch See-All Podcast data"))
        } catch (e: Exception) {
            return ResultType.Error(e)
        }

    }
}