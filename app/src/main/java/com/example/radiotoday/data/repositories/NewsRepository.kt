package com.example.radiotoday.data.repositories

import android.util.Log
import com.example.radiotoday.data.models.seeAll.SeeAllResponse
import com.example.radiotoday.data.services.ApiServices
import com.example.radiotoday.utils.ResultType
import javax.inject.Inject

class NewsRepository @Inject constructor(private val apiServices: ApiServices){

    suspend fun getNewsData(page: String) : ResultType<SeeAllResponse>{
        try {
            val response = apiServices.postNewsData(page)
            Log.i("TAGY", "successful api call: ${response.code()}")
            if (response.isSuccessful) {
                val data = response.body()
                if (data != null) {
                    Log.i("TAGY", "getNewsData: ${data.content}")
                }
                if (data != null) {
                    return ResultType.Success(data)
                }
            }
            return ResultType.Error(Exception("Failed to fetch News data"))

        }catch (e: Exception){
            return ResultType.Error(e)
        }
    }
}