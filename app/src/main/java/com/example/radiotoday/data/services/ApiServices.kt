package com.example.radiotoday.data.services

import com.example.radiotoday.data.models.audio.AudioResponse
import com.example.radiotoday.data.models.home.HomeResponse
import com.example.radiotoday.data.models.seeAll.SeeAllResponse
import com.example.radiotoday.data.models.showDetails.ShowDetailsResponse
import com.example.radiotoday.data.models.video.VideoResponse
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

    @FormUrlEncoded
    @POST("bangladhol_json_app.php")
    suspend fun postAudioPlaylistData(
        @Field("userId") userId: String
    ): Response<AudioResponse>


    @FormUrlEncoded
    @POST("bangladhol_json_app.php")
    suspend fun postVideoPlaylistData(
        @Field("userId") userId: String
    ): Response<VideoResponse>

    @FormUrlEncoded
    @POST("bangladhol_json_album.php")
    suspend fun postSeeAllData(
        @Field("ct") ct: String,
        @Field("type") type: String,
        @Field("page") page: String,
    ): Response<SeeAllResponse>

    @FormUrlEncoded
    @POST("bangladhol_json_album_single.php")
    suspend fun postShowDetailsData(
        @Field("albumcode") albumcode: String,
    ): Response<ShowDetailsResponse>


}