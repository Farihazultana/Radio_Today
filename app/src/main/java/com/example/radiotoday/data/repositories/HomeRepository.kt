package com.example.radiotoday.data.repositories

import com.example.radiotoday.data.models.home.HomeResponse
import com.example.radiotoday.data.models.home.HomeResponseX
import com.example.radiotoday.data.services.ApiServices
import com.example.radiotoday.utils.ResultType
import javax.inject.Inject

class HomeRepository @Inject constructor(private val apiServices: ApiServices){

    suspend fun getHomeData(page_slug: String, from_source: String) : ResultType<HomeResponseX>{
        try {
            val response = apiServices.postHomeData(page_slug, from_source)
            if (response.isSuccessful){
                val data = response.body()
                if (data != null){
                    return ResultType.Success(data)
                }
            }
            return ResultType.Error(Exception("Failed to fetch home data"))
        }catch (e: Exception){
            return ResultType.Error(e)
        }
    }
}