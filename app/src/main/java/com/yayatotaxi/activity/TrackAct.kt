package com.yayatotaxi.activity

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.gson.Gson
import com.yayatotaxi.R
import com.yayatotaxi.directionhelpers.FetchURL
import com.yayatotaxi.directionhelpers.TaskLoadedCallback
import com.yayatotaxi.model.ModelLogin
import com.yayatotaxi.model.ModelTaxiRequest
import com.yayatotaxi.retrofit.ApiClient
import com.yayatotaxi.retrofit.YayatoApiService
import com.yayatotaxi.utils.*
import kotlinx.android.synthetic.main.activity_track.*
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TrackAct : AppCompatActivity(), OnMapReadyCallback, TaskLoadedCallback {

    var mContext: Context = this@TrackAct

    lateinit var sharedPref: SharedPref
    lateinit var modelLogin: ModelLogin

    var requestId: String = ""
    var driverID: String = ""

    lateinit var supportMapFragment: SupportMapFragment
    lateinit var googleMap: GoogleMap
    var currentLocationMarker: Marker? = null
    var currentLocation: LatLng? = null
    lateinit var tracker: GPSTracker

    var driverMarker: Marker? = null


    var driverLatLng: LatLng? = null


    private var place1: MarkerOptions? = null
    private var place2: MarkerOptions? = null

    private var currentPolyline: Polyline? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_track)

        sharedPref = SharedPref(mContext)
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS)
        tracker = GPSTracker(mContext)
        currentLocation = LatLng(tracker.latitude, tracker.longitude)

        itIt()
    }

    private fun itIt() {
        supportMapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        supportMapFragment.getMapAsync(this)

        ivCancelTrip.setOnClickListener {
          /*  startActivity(
                Intent(
                    mContext,
                    RideCancellationAct::class.java
                ).putExtra("requestId", requestId).putExtra("driverID", driverID)
            )*/
        }

    }

    override fun onResume() {
        super.onResume()

        sharedPref = SharedPref(mContext)
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS)

        getCurrentTaxiBookingApi()


        val handler = Handler()
        handler.postDelayed(object : Runnable {
            override fun run() {
                //Do something after 20 seconds
                get_lat_lonApi()
                handler.postDelayed(this, 10000)
            }
        }, 40000) //the time is in miliseconds

    }

    override fun onMapReady(maps: GoogleMap) {
        googleMap = maps
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        googleMap.isMyLocationEnabled = true
        showMarkerCurrentLocation(currentLocation!!)

    }

    private fun showMarkerCurrentLocation(currentLocation: LatLng) {
        if (currentLocation != null) {
            if (currentLocationMarker == null) {
                if (googleMap != null) {
//                    val height = 95
//                    val width = 65
//                    val b = BitmapFactory.decodeResource(resources, R.drawable.car_top)
//                    val smallMarker = Bitmap.createScaledBitmap(b, width, height, false)
//                    val smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(smallMarker)
//                    currentLocationMarker = googleMap.addMarker(
//                        MarkerOptions().position(currentLocation).title("My Location")
//                            .icon(smallMarkerIcon)
//                    )
//                    animateCamera(currentLocation)
                }
            } else {
                Log.e("Animation", "Hello Marker Animation")
                animateCamera(currentLocation)
                MarkerAnimation.animateMarkerToGB(
                    currentLocationMarker!!,
                    currentLocation,
                    LatLngInterpolator.Companion.Spherical()
                )
            }
        }
    }

    private fun animateCamera(location: LatLng) {
        googleMap.animateCamera(
            CameraUpdateFactory.newCameraPosition(
                getCameraPositionWithBearing(
                    location
                )
            )
        )
    }

    private fun getCameraPositionWithBearing(latLng: LatLng): CameraPosition {
        return CameraPosition.Builder().target(latLng).zoom(15f).build()
    }

    private fun get_lat_lonApi() {
        val api: YayatoApiService = ApiClient.getClient(mContext)!!.create(YayatoApiService::class.java)
        val call: Call<ResponseBody> = api.get_lat_lon(
            driverID
        )

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                try {
                    val responseString = response.body()!!.string()
                    val jsonObject = JSONObject(responseString)
                    Log.e("CurrentTaxiBooking", "responseString = $responseString")
                    if (jsonObject.getString("status") == "1") {
//                        val modelVehicleList: ModelVehicalList =
//                            Gson().fromJson(responseString, ModelVehicalList::class.java)
//                        listVehicle.addAll(modelVehicleList.getResult()!!)
//                        MyApplication.showAlert(mContext, jsonObject.getString("result"))

//                        get_profileApi()
                        try {
                            driverLatLng = LatLng(
                                jsonObject.optString("lat").toDouble(),
                                jsonObject.optString("lon").toDouble()
                            )

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        if(driverMarker==null) {
                            val height = 95
                            val width = 65
                            val b = BitmapFactory.decodeResource(resources, R.drawable.car_top)
                            val smallMarker = Bitmap.createScaledBitmap(b, width, height, false)
                            val smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(smallMarker)
                            driverMarker = googleMap.addMarker(
                                MarkerOptions().position(driverLatLng!!).title("Driver Location")
                                    .icon(smallMarkerIcon)
                            )
                        }else{
                            driverMarker?.setPosition(driverLatLng!!)
                        }



//

//                        animateCamera(driverLatLng!!)
                    }
                } catch (e: Exception) {
//                    Toast.makeText(mContext, "Exception = " + e.message, Toast.LENGTH_SHORT).show()
                    Log.e("Exception", "Exception = " + e.message)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("Exception", "Throwable = " + t.message)
            }

        })
    }

    private fun getCurrentTaxiBookingApi() {
        val api: YayatoApiService = ApiClient.getClient(mContext)!!.create(YayatoApiService::class.java)
        val call: Call<ResponseBody> = api.getCurrentTaxiBooking(
            modelLogin.getResult()?.id.toString(),
            AppConstant.USER
        )

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                try {
                    val responseString = response.body()!!.string()
                    val jsonObject = JSONObject(responseString)
                    Log.e("CurrentTaxiBooking", "responseString = $responseString")
                    if (jsonObject.getString("status") == "1") {
                        val modelTaxiRequest: ModelTaxiRequest =
                            Gson().fromJson(responseString, ModelTaxiRequest::class.java)
                        requestId = modelTaxiRequest.getResult()?.get(0)?.id.toString()

                        if (modelTaxiRequest.getResult()?.get(0)?.driver_details?.size!! > 0) {
                            driverID =
                                modelTaxiRequest.getResult()
                                    ?.get(0)?.driver_details!![0].id.toString()


                            tvName.text =
                                modelTaxiRequest.getResult()
                                    ?.get(0)?.driver_details!![0].user_name + "\n" +
                                        modelTaxiRequest.getResult()
                                            ?.get(0)?.driver_details!![0].email

                            Glide.with(mContext)
                                .load(
                                    modelTaxiRequest.getResult()?.get(0)?.driver_details!![0].image
                                )
                                .error(R.drawable.user_ic)
                                .placeholder(R.drawable.user_ic)
                                .into(driver_image)
                            tvCarNumber.text = modelTaxiRequest.getResult()?.get(0)?.car_number

                            get_lat_lonApi()
                        } else {
                            tvCarNumber.text = modelTaxiRequest.getResult()?.get(0)?.status
                        }
                        tvCarNumber.text = modelTaxiRequest.getResult()?.get(0)?.status
                        tvCarName.text = modelTaxiRequest.getResult()?.get(0)?.car_name
                        tvTime.text = modelTaxiRequest.getResult()?.get(0)?.estimate_time + " min"
                        tvPrice.text = "$" + modelTaxiRequest.getResult()?.get(0)?.amount
                        Glide.with(mContext)
                            .load("")
                            .error(R.drawable.car)
                            .placeholder(R.drawable.car)
                            .into(ivCar)

                        if (googleMap != null) {

                            val latLngSource: LatLng = LatLng(
                                modelTaxiRequest.getResult()?.get(0)?.picuplat?.toDouble()!!,
                                modelTaxiRequest.getResult()?.get(0)?.pickuplon?.toDouble()!!
                            )


                            val height = 95
                            val width = 65
                            val b = BitmapFactory.decodeResource(resources, R.drawable.ic_setloc)
                            val smallMarker = Bitmap.createScaledBitmap(b, width, height, false)
                            val smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(smallMarker)


                            place1 = MarkerOptions().position(
                                latLngSource
                            ).title(
                                "Pickup Location: " + modelTaxiRequest.getResult()
                                    ?.get(0)?.picuplocation
                            )
                                .icon(smallMarkerIcon)
                            googleMap.addMarker(place1!!)
                            animateCamera(latLngSource)

                            val latLngDropOff: LatLng = LatLng(
                                modelTaxiRequest.getResult()?.get(0)?.droplat?.toDouble()!!,
                                modelTaxiRequest.getResult()?.get(0)?.droplon?.toDouble()!!
                            )

                            val height1 = 95
                            val width1 = 65
                            val b1 = BitmapFactory.decodeResource(resources, R.drawable.ic_setloc)
                            val smallMarker1 = Bitmap.createScaledBitmap(b1, width1, height1, false)
                            val smallMarkerIcon1 = BitmapDescriptorFactory.fromBitmap(smallMarker1)


                            place2 = MarkerOptions().position(
                                latLngDropOff
                            ).title(
                                "Drop Off Location: " + modelTaxiRequest.getResult()
                                    ?.get(0)?.dropofflocation
                            )
                                .icon(smallMarkerIcon1)
                            googleMap.addMarker(place2!!)

                            if (place1 != null && place2 != null) {
                                FetchURL(this@TrackAct).execute(
                                    getUrl(
                                        place1!!.position,
                                        place2!!.position,
                                        "driving"
                                    ), "driving"
                                )
                            }
                        }
                    } else {
                        finish()
                    }
                } catch (e: Exception) {
                    Toast.makeText(mContext, "Exception = " + e.message, Toast.LENGTH_SHORT).show()
                    Log.e("Exception", "Exception = " + e.message)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("Exception", "Throwable = " + t.message)
            }
        })
    }

/*
    private fun tripStatusDialog(text: String, status: String, data: ModelCurrentBooking?) {
        val dialog = Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT)
        dialog.setCancelable(false)
        val dialogNewBinding: TripStatusDialogNewBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mContext), R.layout.trip_status_dialog_new, null, false
        )
        dialogNewBinding.tvMessage.setText(text)
        dialog.window!!.setBackgroundDrawableResource(R.color.transparent)
        dialogNewBinding.tvOk.setOnClickListener { v ->
            if ("End" == status) {
                val j = Intent(mContext, EndUserAct::class.java)
                startActivity(j)
                finish()
            } else if ("Finish" == status) {
                finishAffinity()
                startActivity(Intent(mContext, HomeAct::class.java))
            }
            dialog.dismiss()
        }
        dialog.setContentView(dialogNewBinding.getRoot())
        dialog.show()
    }
*/

    override fun onTaskDone(vararg values: Any?) {
        if (currentPolyline != null) currentPolyline!!.remove()
        currentPolyline = googleMap.addPolyline((values[0] as PolylineOptions?)!!)
    }

    private fun getUrl(
        origin: LatLng,
        dest: LatLng,
        directionMode: String
    ): String? {
        // Origin of route
        val str_origin = "origin=" + origin.latitude + "," + origin.longitude
        // Destination of route
        val str_dest = "destination=" + dest.latitude + "," + dest.longitude
        // Mode
        val mode = "mode=$directionMode"
        // Building the parameters to the web service
        val parameters = "$str_origin&$str_dest&$mode"
        // Output format
        val output = "json"
        // Building the url to the web service

//        String url = "https://maps.googleapis.com/maps/api/directions/json?origin="+latitude+","+longitude+"&destination="+latitudeStr+","+longitudeStr+"&key=" + getString(R.string.google_maps_key);
        return "https://maps.googleapis.com/maps/api/directions/$output?$parameters&key=AIzaSyC-QuuXBR3Yunb-WlpEE8Ja2dxNGCiVboM"
    }

}