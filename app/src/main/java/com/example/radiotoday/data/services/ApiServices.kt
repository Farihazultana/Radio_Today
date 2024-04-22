package com.example.radiotoday.data.services

import com.example.radiotoday.data.models.MainResponse
import com.example.radiotoday.data.models.home.HomeResponse
import com.example.radiotoday.data.models.login.LogoutResponse
import com.example.radiotoday.data.models.seeAll.SeeAllResponse
import com.example.radiotoday.data.models.showDetails.ShowDetailsResponse
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiServices {
    @FormUrlEncoded
    @POST("api/sections")
    suspend fun postHomeData(
        @Header("fromsrc") fromsrc : String,
        @Header("deviceId") deviceId : String,
        @Header("softwareVersion") softwareVersion : String,
        @Header("brand") brand : String,
        @Header("model") model : String,
        @Header("sdkVersion") sdkVersion : String,
        @Header("versionCode") versionCode : String,
        @Header("release") release : String,


        @Field("page_slug") page_slug: String,
        @Field("from_source") from_source: String
    ): Response<HomeResponse>


    @FormUrlEncoded
    @POST("api/{section_code}")
    suspend fun postSeeAllData(
        /*@Header("fromsrc") fromsrc : String,
        @Header("deviceId") deviceId : String,
        @Header("softwareVersion") softwareVersion : String,
        @Header("brand") brand : String,
        @Header("model") model : String,
        @Header("sdkVersion") sdkVersion : String,
        @Header("versionCode") versionCode : String,
        @Header("release") release : String,*/


        @Path("section_code") section_code: String,
        @Field("page") page: String,
    ): Response<SeeAllResponse>


    @FormUrlEncoded
    @POST("api/{section_code}")
    suspend fun postSeeAllPosdcastData(
        /*@Header("fromsrc") fromsrc : String,
        @Header("deviceId") deviceId : String,
        @Header("softwareVersion") softwareVersion : String,
        @Header("brand") brand : String,
        @Header("model") model : String,
        @Header("sdkVersion") sdkVersion : String,
        @Header("versionCode") versionCode : String,
        @Header("release") release : String,*/


        @Path("section_code") section_code: String,
        @Field("empty") empty: String,


    ): Response<HomeResponse>


    @FormUrlEncoded
    @POST("api/podcasts/type")
    @Headers("Accept: application/json")
    suspend fun postPosdcastDetailByCategoryData(
        /*@Header("fromsrc") fromsrc : String,
        @Header("deviceId") deviceId : String,
        @Header("softwareVersion") softwareVersion : String,
        @Header("brand") brand : String,
        @Header("model") model : String,
        @Header("sdkVersion") sdkVersion : String,
        @Header("versionCode") versionCode : String,
        @Header("release") release : String,*/

        @Field("type") category: String,
        @Field("page") page: String,

        ): Response<MainResponse>


    @FormUrlEncoded
    @POST("api/{section_code}/{id}")
    suspend fun postShowDetailsData(
        /*@Header("fromsrc") fromsrc : String,
        @Header("deviceId") deviceId : String,
        @Header("softwareVersion") softwareVersion : String,
        @Header("brand") brand : String,
        @Header("model") model : String,
        @Header("sdkVersion") sdkVersion : String,
        @Header("versionCode") versionCode : String,
        @Header("release") release : String,*/


        @Path("section_code") section_code: String,
        @Path("id") id: String,
        @Field("empty") empty: String,

    ): Response<ShowDetailsResponse>


    @FormUrlEncoded
    @POST("api/login")
    @Headers("Accept: application/json")
    suspend fun postLoginData(
        @Field("email") email: String,
        @Field("password") password: String,

    ): Response<MainResponse>


    @FormUrlEncoded
    @POST("api/register")
    @Headers("Accept: application/json")
    suspend fun postRegistrationData(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("phone") phone: String,
        @Field("password") password: String,
        @Field("password_confirmation") passwordConfirmation: String,

        ): Response<MainResponse>


    @FormUrlEncoded
    @POST("api/logout")
    @Headers("Accept: application/json")
    suspend fun postLogoutData(

        @Header("Authorization") token: String,
        @Field("empty") empty: String

        ): Response<LogoutResponse>

    @FormUrlEncoded
    @POST("api/settings/{type}")
    @Headers("Accept: application/json")
    suspend fun postSettingsData(

        @Path("type") type: String,
        @Field("empty") empty: String,

        ): Response<MainResponse>


    @FormUrlEncoded
    @POST("api/songs")
    suspend fun postAudioPlaylistData(
        @Field("page") page: String,
    ): Response<SeeAllResponse>


    @FormUrlEncoded
    @POST("api/videos")
    suspend fun postVideoPlaylistData(
        @Field("page") page: String,
    ): Response<SeeAllResponse>


    @FormUrlEncoded
    @POST("api/news")
    suspend fun postNewsData(
        @Field("page") page: String,
    ): Response<SeeAllResponse>


    @FormUrlEncoded
    @POST("api/forgot-password")
    suspend fun postForgetPasswordData(
        @Field("email") email: String
    ): Response<MainResponse>


}