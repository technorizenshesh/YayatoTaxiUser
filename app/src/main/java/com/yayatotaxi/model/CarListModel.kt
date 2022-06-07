package com.yayatotaxi.model

import java.io.Serializable
import java.util.ArrayList

class CarListModel {
    private var result: ArrayList<Result>? = null
    private var status: String? = null
    private var message: String? = null

    fun setResult(result: ArrayList<Result>?) {
        this.result = result
    }

    fun getResult(): ArrayList<Result>? {
        return result
    }

    fun setStatus(status: String?) {
        this.status = status
    }

    fun getStatus(): String? {
        return status
    }

    fun setMessage(message: String?) {
        this.message = message
    }

    fun getMessage(): String? {
        return message
    }


    class Result : Serializable {
        var id: String? = null
        var type: String? = null
        var car_type_id: String? = null
        var car_number: String? = null
        var car_model: String? = null
        var brand: String? = null
        var service_type: String? = null
        var car_image: String? = null
        var year_of_manufacture: String? = null
        var status: String? = null
        var lat: String? = null
        var lon: String? = null
        var address: String? = null
        var rate_pre_km: String? = null
        var start_date: String? = null
        var end_date: String? = null
        var start_time: String? = null
        var end_time: String? = null
        var partner_name: String? = null
        var partner_email: String? = null
        var distance: String? = null
        var estimate_time: Int? = null









    }

}
