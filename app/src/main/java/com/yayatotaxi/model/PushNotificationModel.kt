package com.yayatotaxi.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class PushNotificationModel : Serializable {
//    {message={"driver_id":null,"driver_lastname":null,"rating":null,"booktype":"now","shareride_type":"","picuplocation":"332, Vijay Nagar Square, near Freito Cafe, Scheme 54 PU4, Indore, Madhya Pradesh 452011, India\n","req_datetime":"2022-03-02 11:08:13","result":"successful","driver_firstname":null,"notifi_type":"USER","picklatertime":"","review":null,"picklaterdate":"","request_id":"21","driver_img":"https:\/\/technorizen.com\/yayato\/uploads\/images\/","key":"your booking request is Accept","driver_image":null,"status":"Accept"}}
    @SerializedName("driver_id")
    @Expose
    var driver_id: String? = null

    @SerializedName("driver_lastname")
    @Expose
    var driver_lastname: String? = null

    @SerializedName("booktype")
    @Expose
    var booktype: String? = null

    @SerializedName("shareride_type")
    @Expose
    var shareride_type: String? = null

    @SerializedName("picuplocation")
    @Expose
    var picuplocation: String? = null

    @SerializedName("req_datetime")
    @Expose
    var req_datetime: String? = null

    @SerializedName("result")
    @Expose
    var result: String? = null

    @SerializedName("driver_firstname")
    @Expose
    var driver_firstname: String? = null

    @SerializedName("notifi_type")
    @Expose
    var notifi_type: String? = null

    @SerializedName("picklatertime")
    @Expose
    var picklatertime: String? = null

    @SerializedName("review")
    @Expose
    var review: String? = null

    @SerializedName("picklaterdate")
    @Expose
    var picklaterdate: String? = null

    @SerializedName("request_id")
    @Expose
    var request_id: String? = null

    @SerializedName("driver_img")
    @Expose
    var driver_img: String? = null

    @SerializedName("key")
    @Expose
    var key: String? = null

    @SerializedName("driver_image")
    @Expose
    var driver_image: String? = null

    @SerializedName("status")
    @Expose
    var status: String? = null
}