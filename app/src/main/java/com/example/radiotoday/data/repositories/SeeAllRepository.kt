package com.example.radiotoday.data.repositories

import android.util.Log
import com.example.radiotoday.data.models.seeAll.SeeAllResponse
import com.example.radiotoday.data.services.ApiServices
import com.example.radiotoday.utils.ResultType
import javax.inject.Inject

class SeeAllRepository @Inject constructor(private val apiServices: ApiServices) {
    suspend fun getSeeAllData(section_code: String, page: String): ResultType<SeeAllResponse> {
        try {
            val response = apiServices.postSeeAllData(section_code, page)
            Log.i("TAGS", "successful api call: ${response.code()}")
            if (response.isSuccessful) {
                val data = response.body()
                if (data != null) {
                    Log.i("TAGS", "getSeeAllData: ${data.content}")
                }
                if (data != null) {
                    return ResultType.Success(data)
                }
            }
            return ResultType.Error(Exception("Failed to fetch See-All data"))
        } catch (e: Exception) {
            return ResultType.Error(e)
        }
    }
}
