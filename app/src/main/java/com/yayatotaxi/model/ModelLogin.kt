package com.yayatotaxi.model

import android.graphics.drawable.Drawable
import java.io.Serializable
import com.bumptech.glide.request.RequestOptions

import com.bumptech.glide.Glide

import androidx.databinding.BindingAdapter
import com.bumptech.glide.request.target.Target
import de.hdodenhof.circleimageview.CircleImageView


class ModelLogin : Serializable {

    private var message: String? = null
    private var result: Result? = null
    private var status: String? = null

    fun setMessage(message: String?) {
        this.message = message
    }

    fun getMessage(): String? {
        return message
    }

    fun setResult(result: Result?) {
        this.result = result
    }

    fun getResult(): Result? {
        return result
    }

    fun setStatus(status: String?) {
        this.status = status
    }

    fun getStatus(): String? {
        return status
    }

    class Result {
        var id: String? = null
        var first_name: String? = null
        var last_name: String? = null
        var user_name: String? = null
        var car_type_id: String? = null
        var email: String? = null
        var mobile: String? = null
        var password: String? = null
        var city: String? = null
        var image: String? = null
        var step: String? = null
        var register_id: String? = null
        var date_time: String? = null
        var social_id: String? = null
        var lat: String? = null
        var lon: String? = null
        var address: String? = null
        var zipcode: String? = null
        var country: String? = null
        var state: String? = null
        var ios_register_id: String? = null
        var stripe_account_login_link: String? = null
        var wallet: String? = null
        var verify_code: String? = null
        var phone_code: String? = null
        var cust_id: String? = null
        var lang: String? = null
        var stripe_account_id: String? = null
        var promo_code: String? = null
        var amount: String? = null
        var car_document: String? = null
        var insurance: String? = null
        var document: String? = null
        var status: String? = null
        var type: String? = null
        var online_status: String? = null
        var driver_lisence: String? = null
        var pan_card_imag: String? = null
        var workplace: String? = null
        var work_lat: String? = null
        var work_lon: String? = null
        var basic_car: String? = null
        var normal_car: String? = null
        var luxurious_car: String? = null
        var pool_car: String? = null
        var social_status: String? = null




    }

}