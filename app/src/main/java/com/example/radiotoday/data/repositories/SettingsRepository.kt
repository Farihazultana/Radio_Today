package com.example.radiotoday.data.repositories

import android.util.Log
import com.example.radiotoday.data.models.MainResponse
import com.example.radiotoday.data.services.ApiServices
import com.example.radiotoday.utils.ResultType
import javax.inject.Inject

class SettingsRepository @Inject constructor(private val apiServices: ApiServices) {
    suspend fun getSettingsData(type : String) : ResultType<MainResponse> {

        try {
            val response = apiServices.postSettingsData(type, "")
            Log.i("TAGY", "successful api call: ${response.code()}")
            if (response.isSuccessful) {
                val data = response.body()
                if (data != null) {
                    Log.i("TAGY", "getSettingsData: ${data.content.type}")
                }
                if (data != null) {
                    return ResultType.Success(data)
                }
            }
            return ResultType.Error(Throwable(response.errorBody()?.string()))

        }catch (e: Exception){
            return ResultType.Error(e)
        }
    }
}