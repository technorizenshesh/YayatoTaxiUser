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


    @FormUrlEncoded
    @POST("add_car_request")
    fun add_car_request(
/*
        @Field("status") status: String,
*/
        @Field("car_detail_id") car_detail_id: String,
        @Field("driver_id") driver_id: String,
          /*  @Field("lat") lat: String,
            @Field("lon") lon: String*/


    ): Call<ResponseBody>





    @FormUrlEncoded
    @POST("get_car_type_list")
    fun getCarList(
        @Field("picuplat") picuplat: String,
        @Field("pickuplon") pickuplon: String,
        @Field("droplat") droplat: String,
        @Field("droplon") droplon: String
    ): Call<ResponseBody>



    @FormUrlEncoded
    @POST("booking_request")
    fun newBookingRequest(
        @Field("user_id") user_id: String,
        @Field("car_type_id") cartype_id: String,
        @Field("picuplat") picuplat: String,
        @Field("pickuplon") pickuplon: String,
        @Field("booktype") booktype: String,
        @Field("amount") amount: String,
        @Field("picuplocation") picuplocation: String,
        @Field("dropofflocation") dropofflocation: String,
        @Field("droplat") droplat: String,
        @Field("droplon") droplon: String,
        @Field("picklatertime") picklatertime: String,
        @Field("picklaterdate") picklaterdate: String,
        @Field("timezone")timezone: String
/*
        @Field("seats_avaliable_pool") seats_avaliable_pool: String
*/
    ): Call<ResponseBody>



    @FormUrlEncoded
    @POST("get_current_booking")
    fun getCurrentTaxiBooking(
        @Field("user_id") user_id: String,
        @Field("type") type: String
    ): Call<ResponseBody>


    @FormUrlEncoded
    @POST("get_lat_lon")
    fun get_lat_lon(@Field("user_id") user_id: String): Call<ResponseBody>

}