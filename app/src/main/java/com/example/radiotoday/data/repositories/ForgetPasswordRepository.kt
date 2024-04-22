package com.example.radiotoday.data.repositories

import android.util.Log
import com.example.radiotoday.data.models.MainResponse
import com.example.radiotoday.data.services.ApiServices
import com.example.radiotoday.utils.ResultType
import javax.inject.Inject

class ForgetPasswordRepository @Inject constructor(private val apiServices: ApiServices){
    suspend fun getForgetPasswordData(page: String) : ResultType<MainResponse>{
        try {
            val response = apiServices.postForgetPasswordData(page)
            if (response.isSuccessful){
                val data = response.body()
                if (data != null){
                    Log.i("TAGY", "getForgetPasswordData: ${data.message}")
                }
                if (data != null){
                    return ResultType.Success(data)
                }
            }
            return ResultType.Error(Exception("Failed to fetch Forget Password Data"))
        }catch (e: Exception){
            return ResultType.Error(e)
        }
    }
}