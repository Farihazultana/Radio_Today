package com.example.radiotoday.data.repositories

import com.example.radiotoday.data.models.home.HomeResponse
import com.example.radiotoday.data.models.login.LoginResponse
import com.example.radiotoday.data.services.ApiServices
import com.example.radiotoday.utils.ResultType
import javax.inject.Inject

class LogInRepository @Inject constructor(private val apiServices: ApiServices){
    suspend fun getLogInData(email: String, password: String) : ResultType<LoginResponse>{
        try {
            val response = apiServices.postLoginData(email, password)
            if (response.isSuccessful){
                val data = response.body()
                if (data != null){
                    return ResultType.Success(data)
                }
            }
            return ResultType.Error(Exception("Failed to fetch Login Data"))

        }catch (e: Exception){
            return ResultType.Error(e)
        }
    }

}