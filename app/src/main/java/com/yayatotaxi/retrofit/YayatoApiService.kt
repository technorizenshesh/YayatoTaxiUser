package com.yayatotaxi.retrofit

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface YayatoApiService {



    @Multipart
    @POST("signup")
    fun signUpUserCallApi(
        @Part("first_name") first_name: RequestBody,
        @Part("last_name") last_name: RequestBody,
        @Part("email") email: RequestBody,
        @Part("mobile") mobile: RequestBody,
        @Part("address") address: RequestBody,
        @Part("register_id") register_id: RequestBody,
        @Part("lat") lat: RequestBody,
        @Part("lon") lon: RequestBody,
        @Part("password") password: RequestBody,
        @Part("type") type: RequestBody,
        @Part("step") step: RequestBody,
        @Part file1: MultipartBody.Part
    ): Call<ResponseBody>


    @FormUrlEncoded
    @POST("login")
    fun loginApiCall(@FieldMap params: Map<String, String>): Call<ResponseBody>


    @FormUrlEncoded
    @POST("get_profile")
    fun profileApiCall(@FieldMap params: Map<String, String>): Call<ResponseBody>





    @FormUrlEncoded
    @POST("forgot_password")
    fun forgotPass(@FieldMap params: Map<String, String>): Call<ResponseBody>


    @Multipart
    @POST("update_profile")
    fun updateDriverCallApi(
        @Part("first_name") first_name: RequestBody,
        @Part("last_name") last_name: RequestBody,
        @Part("email") email: RequestBody,
        @Part("mobile") mobile: RequestBody,
        @Part("address") address: RequestBody,
        @Part("lat") lat: RequestBody,
        @Part("lon") lon: RequestBody,
        @Part("type") type: RequestBody,
        @Part("id") id: RequestBody,
        @Part file1: MultipartBody.Part
    ): Call<ResponseBody>



    @FormUrlEncoded
    @POST("get_nearbuy_partner")
    fun get_car_detial(
        @Field("start_date") start_date: String,
        @Field("end_date") end_date: String,
        @Field("lat") lat: String,
        @Field("lon") lon: String

    ): Call<ResponseBody>


}