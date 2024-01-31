package com.example.radiotoday.data.repositories

import com.example.radiotoday.data.models.home.HomeResponse
import com.example.radiotoday.data.services.ApiServices
import com.example.radiotoday.utils.ResultType
import javax.inject.Inject

class HomeRepository @Inject constructor(private val apiServices: ApiServices){

    suspend fun getHomeData(page_slug: String, from_source: String) : ResultType<HomeResponse>{
        try {
            val response = apiServices.postHomeData( "android", "b462467a9bbf31ad", "1", "dg", "r", "d", "","", page_slug, from_source)
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