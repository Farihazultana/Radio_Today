package com.example.radiotoday.data.repositories

import com.example.radiotoday.data.models.login.LogoutResponse
import com.example.radiotoday.data.services.ApiServices
import com.example.radiotoday.utils.ResultType
import javax.inject.Inject

class LogoutRepository @Inject constructor(private val apiServices: ApiServices) {
    suspend fun getLogoutData(token: String) : ResultType<LogoutResponse> {
        try {
            val response = apiServices.postLogoutData(token, "")
            if (response.isSuccessful){
                val data = response.body()
                if (data != null){
                    return ResultType.Success(data)
                }
            }
            return ResultType.Error(Throwable(response.errorBody()?.string()))
        }catch (e: Exception){
            return ResultType.Error(e)
        }
    }
}