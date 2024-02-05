package com.example.radiotoday.data.repositories

import com.example.radiotoday.data.models.MainResponse
import com.example.radiotoday.data.services.ApiServices
import com.example.radiotoday.utils.ResultType
import javax.inject.Inject

class RegistrationRepository @Inject constructor(private val apiServices: ApiServices){
    suspend fun getRegistrationData(name: String, email:String, phone: String, password: String, password_confirmation: String) : ResultType<MainResponse>{
        try {
            val response = apiServices.postRegistrationData(name,email,phone,password, password_confirmation)
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