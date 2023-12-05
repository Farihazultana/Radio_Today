package com.example.radiotoday.data.services

import com.example.radiotoday.data.models.home.HomeResponse
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiServices {
    @FormUrlEncoded
    @POST("bangladhol_json_app_home.php")
    suspend fun postHomeData(
        @Field("userId") userId: String,
    ): Response<HomeResponse>
}