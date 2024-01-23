package com.example.radiotoday.data.services

import com.example.radiotoday.data.models.audio.AudioResponse
import com.example.radiotoday.data.models.home.HomeResponseX
import com.example.radiotoday.data.models.seeAll.SeeAllResponseX
import com.example.radiotoday.data.models.showDetails.ShowDetailsResponse
import com.example.radiotoday.data.models.video.VideoResponse
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Param
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiServices {
    @FormUrlEncoded
    @POST("api/sections")
    suspend fun postHomeData(
        @Field("page_slug") page_slug: String,
        @Field("from_source") from_source: String
    ): Response<HomeResponseX>

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
    @POST("api/")
    suspend fun postSeeAllData(
        @Param("section_code") section_code: String,
        @Field("page") page: String,
    ): Response<SeeAllResponseX>

    @FormUrlEncoded
    @POST("bangladhol_json_album_single.php")
    suspend fun postShowDetailsData(
        @Field("albumcode") albumcode: String,
    ): Response<ShowDetailsResponse>


}