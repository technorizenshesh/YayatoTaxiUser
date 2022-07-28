package com.yayatotaxi.model

import java.io.Serializable
import java.util.ArrayList

class ModelTaxiRequest : Serializable {

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
        var code: String? = null
        var title: String? = null
        var user_id: String? = null
        var driver_id: String? = null
        var driver_ids: String? = null
        var picuplocation: String? = null
        var dropofflocation: String? = null
        var picuplat: String? = null
        var pickuplon: String? = null
        var droplat: String? = null
        var droplon: String? = null
        var shareride_type: String? = null
        var booktype: String? = null
        var seats_avaliable_pool:String?=null
        var car_type_id: String? = null
        var car_seats:String? = null
        var booked_seats: String? = null
        var req_datetime: String? = null
        var timezone: String? = null
        var picklatertime: String? = null
        var picklaterdate: String? = null
        var route_img: String? = null
        var start_time: String? = null
        var end_time: String? = null
        var wt_start_time: String? = null
        var wt_end_time: String? = null
        var waiting_status: String? = null
        var accept_time: String? = null
        var waiting_cnt:String? = null
        var apply_code:String? = null
        var payment_type:String? = null
        var card_id: String? = null
        var status: String? = null
        var payment_status: String? = null
        var cancel_reaison: String? = null
        var amount: String? = null
        var service_name: String? = null
        var pool_request_id:String? = null
        var user_details: ArrayList<ModelUserDetail.Result>? = null
        var driver_details: ArrayList<ModelUserDetail.Result>? = null
        var user_review_rating: String? = null
        var distance: String? = null
        var estimate_time: String? = null
        var car_name: String? = null
        var pool_details: ArrayList<PoolDetails>? = null
        var car_number:String?=null
        class PoolDetails : Serializable {
            var id: String? = null
            var user_id: String? = null
            var booking_request_id: String? = null
            var date: String? = null
            var time: String? = null
            var noofseats: String? = null
            var pickuplocation: String? = null
            var pickuplocation_lat: String? = null
            var pickuplocation_lon: String? = null
            var droplocation: String? = null
            var droplocation_lat:String? = null
            var droplocation_lon: String? = null
            var status: String? = null
        }
    }

}