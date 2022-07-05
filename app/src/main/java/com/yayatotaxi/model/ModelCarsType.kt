package com.yayatotaxi.model

import java.io.Serializable
import java.util.ArrayList

class ModelCarsType : Serializable {

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
        var car_name: String? = null
        var car_image: String? = null
        var car_image_selected: String? = null
        var charge: String? = null
        var no_of_seats: String? = null
        var min_charge: String? = null
        var per_km: String? = null
        var hold_charge: String? = null
        var ride_time_charge_permin: String? = null
        var service_tax: String? = null
        var free_time_min: String? = null
        var email_to_print: String? = null
        var scan_and_email: String? = null
        var prepaid: String? = null
        var over_night: String? = null
        var total:String?=null
        var distance:String?=null
        var perMin:String?=null
        var status: String? = null
        var isSelected: Boolean? = false
    }

}