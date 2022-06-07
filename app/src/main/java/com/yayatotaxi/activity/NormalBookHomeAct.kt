package com.yayatotaxi.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.firebase.firestore.GeoPoint
import com.yayatotaxi.R
import com.yayatotaxi.databinding.ActivityNormalBookHomeBinding
import com.yayatotaxi.utils.GPSTracker
import com.yayatotaxi.utils.LatLngInterpolator
import com.yayatotaxi.utils.MarkerAnimation
import com.yayatotaxi.utils.ProjectUtil
import com.yayatotaxi.viewmodel.HomeViewModel
import com.yayatotaxi.viewmodel.NormalBookViewModel
import kotlinx.android.synthetic.main.activity_normal_book_home.*
import java.io.IOException
import java.util.*

class NormalBookHomeAct : AppCompatActivity() , OnMapReadyCallback {

    var mContext: Context = this@NormalBookHomeAct
    var type: String = ""
    private var AUTOCOMPLETE_REQUEST_CODE: Int = 101
    private var latLng: LatLng? = null
    var sourceAddress: String = ""
    var sourceAddressLat: String = ""
    var sourceAddressLon: String = ""

    var destinationAddress: String = ""
    var destinationAddressLat: String = ""
    var destinationAddressLon: String = ""
    var addressType: String = ""


    lateinit var mapFragment: SupportMapFragment
    var currentLocationMarker: Marker? = null

    var currentLocation: LatLng? = null
    lateinit var googleMap: GoogleMap
    lateinit var tracker: GPSTracker
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_normal_book_home)
        type = intent.getStringExtra("type")!!

        tracker = GPSTracker(mContext)
        currentLocation = LatLng(tracker.latitude, tracker.longitude)
        tvFrom.setText(getAddress(currentLocation!!))
        sourceAddressLat   = tracker.latitude.toString()
        sourceAddressLon  = tracker.longitude.toString()
        sourceAddress  = tvFrom.getText().toString()

        mapFragment = (supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?)!!
        mapFragment.getMapAsync(this)

        itit()
    }

    fun getLocationFromAddress(strAddress: String?): GeoPoint? {
        val coder = Geocoder(this)
        val address: List<Address>?
        var p1: GeoPoint? = null
        try {
            address = coder.getFromLocationName(strAddress, 5)
            if (address == null) {
                return null
            }
            val location: Address = address[0]
            location.getLatitude()
            location.getLongitude()
            p1 = GeoPoint(
                (location.getLatitude() * 1E6) as Double,
                (location.getLongitude() * 1E6) as Double
            )
            return p1
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }
    private fun getAddress(latLng: LatLng): String {
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses: List<Address>?
        val address: Address?
        var addressText = ""

        addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)

        if (addresses.isNotEmpty()) {
            address = addresses[0]
            addressText = address.getAddressLine(0)
        } else{
            addressText = ""
        }
        return addressText
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
        googleMap.isMyLocationEnabled=true
        showMarkerCurrentLocation(currentLocation!!)
    }

    private fun showMarkerCurrentLocation(currentLocation: LatLng) {
        if (currentLocation != null) {
            if (currentLocationMarker == null) {
                if (googleMap != null) {
                    val height = 95
                    val width = 65
                    val b = BitmapFactory.decodeResource(resources, R.drawable.car_top)
                    val smallMarker = Bitmap.createScaledBitmap(b, width, height, false)
                    val smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(smallMarker)
//                    currentLocationMarker = googleMap.addMarker(
//                        MarkerOptions().position(currentLocation).title("My Location")
//                            .icon(smallMarkerIcon)
//                    )
                    animateCamera(currentLocation)
                }
            } else {
                Log.e("sdfdsfdsfds", "Hello Marker Anuimation")
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
        return CameraPosition.Builder().target(latLng).zoom(16f).build()
    }

    private fun itit() {
        btnBack.setOnClickListener { v -> finish() }
        btnNext.setOnClickListener { v ->
            if(sourceAddress.isNotEmpty()&&destinationAddress.isNotEmpty()) {
                val intent = Intent(mContext, SetDateTimeAct::class.java)
                    .putExtra("sourceAddress", sourceAddress)
                    .putExtra("sourceAddressLat", sourceAddressLat)
                    .putExtra("sourceAddressLon", sourceAddressLon)
                    .putExtra("destinationAddress", destinationAddress)
                    .putExtra("destinationAddressLat", destinationAddressLat)
                    .putExtra("destinationAddressLon", destinationAddressLon)
//                if (type != null && type == "rent") {
//                    intent.putExtra("type", "rent")
//                }
                startActivity(intent)
            }
        }


        tvFrom.setOnClickListener {
            addressType="source"
            val fields = listOf(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.LAT_LNG,
                Place.Field.ADDRESS
            )
            val locale = Locale.getDefault()

            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                .setCountry(getCountry(currentLocation!!))
                .setTypeFilter(TypeFilter.ADDRESS)
                .build(this)
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
        }

        tv_Destination.setOnClickListener {
            addressType="destination"
            val fields = listOf(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.LAT_LNG,
                Place.Field.ADDRESS
            )
            val locale = Locale.getDefault()
//            val token = AutocompleteSessionToken.newInstance()

            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                .setCountry(getCountry(currentLocation!!))
                .setTypeFilter(TypeFilter.ADDRESS)
                .build(this)
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
        }

    }

    fun getCountry(latLng: LatLng):String{
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses: List<Address>?
        val address: Address?
        var fulladdress = ""
        var countryCode = ""

        addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)

        if (addresses.isNotEmpty()) {
            address = addresses[0]
            fulladdress = address.getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex
            var city = address.getLocality();
            var state = address.getAdminArea();
            var country = address.getCountryName();
            countryCode= address.countryCode
            var postalCode = address.getPostalCode();
            var knownName = address.getFeatureName(); // Only if available else return NULL
        } else{
            fulladdress = "Location not found"
        }

        return countryCode
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                val place = Autocomplete.getPlaceFromIntent(data!!)
                latLng = place.latLng
                try {
                    val addresses = ProjectUtil.getCompleteAddressString(
                        mContext, place.latLng!!.latitude, place.latLng!!.longitude
                    )
                    if (addressType.equals("source")) {
                        sourceAddress = addresses.toString()
                        sourceAddressLat = place.latLng!!.latitude.toString()
                        sourceAddressLon = place.latLng!!.longitude.toString()

                        tvFrom.setText(addresses)

                    } else {
                        destinationAddress = addresses.toString()
                        destinationAddressLat = place.latLng!!.latitude.toString()
                        destinationAddressLon = place.latLng!!.longitude.toString()
                        tv_Destination.setText(addresses)
                    }
                } catch (e: Exception) {
                }
            }
        }
    }

}