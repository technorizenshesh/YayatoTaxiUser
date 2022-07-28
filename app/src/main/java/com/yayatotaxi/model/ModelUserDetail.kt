package com.yayatotaxi.model

import java.io.Serializable
import java.util.ArrayList

class ModelUserDetail : Serializable {

    private var message: String? = null
    private var result: ArrayList<ModelTaxiRequest.Result>? = null
    private var status: String? = null

    fun setMessage(message: String?) {
        this.message = message
    }

    fun getMessage(): String? {
        return message
    }



    fun setStatus(status: String?) {
        this.status = status
    }

    fun getStatus(): String? {
        return status
    }

    class Result : Serializable {
        var id: String? = null
        var user_name: String? = null
        var profile_image: String? = null
        var email: String? = null
        var mobile: String? = null
        var password: String? = null
        var register_id: String? = null
        var you_have_vehicle: String? = null
        var workplace: String? = null
        var work_lon: String? = null
        var work_lat: String? = null
        var driver_lisence_img: String? = null
        var car_regist_img: String? = null
        var vehicle_regist_img: String? = null
        var date_time: String? = null
        var cust_id: String? = null
        var stripe_account_id: String? = null
        var ios_register_id: String? = null
        var stripe_account_login_link: String? = null
        var first_name: String? = null
        var last_name: String? = null
        var phone_code: String? = null
        var image: String? = null
        var social_id: String? = null
        var lat: String? = null
        var lon: String? = null
        var status: String? = null
        var address: String? = null
        var zipcode: String? = null
        var country: String? = null
        var state: String? = null
        var city: String? = null
        var type: String? = null
        var car_id: String? = null
        var car_type_id: String? = null
        var online_status: String? = null
        var wallet: String? = null
        var verify_code: String? = null
        var lang: String? = null
        var promo_code: String? = null
        var amount: String? = null
        var license: String? = null
        var car_model: String? = null
        var year_of_manufacture: String? = null
        var car_color: String? = null
        var car_number: String? = null
        var car_image: String? = null
        var car_document: String? = null
        var insurance: String? = null
        var document: String? = null
        var distance: String? = null
        var step: String? = null
        var brand: String? = null
    }

}