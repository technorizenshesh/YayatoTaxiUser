package com.yayatotaxi.model

import java.io.Serializable
import java.util.ArrayList

class ModelVehicalList : Serializable {

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
        var driver_id: String? = null
        var car_type_id: String? = null
        var car_number: String? = null
        var brand: String? = null
        var car_model: String? = null
        var service_type: String? = null
        var car_image: String? = null
        var year_of_manufacture: String? = null
        var status: String? = null
        var car_seats: String? = null
        var car_color: String? = null
        var license_front: String? = null
        var license_back: String? = null
        var date_time: String? = null
        var type: String? = null
        var address: String? = null
        var lat: String? = null
        var lon: String? = null
        var start_time: String? = null
        var end_time: String? = null
        var model_name: String? = null
        var brand_name: String? = null
        var car_name: String? = null
        var request_detailss: ArrayList<RequestDetailss>? = null

        class RequestDetailss : Serializable {
            var id: String? = null
            var car_detail_id: String? = null
            var driver_id: String? = null
            var status: String? = null
            var date_time: String? = null
        }

    }

}