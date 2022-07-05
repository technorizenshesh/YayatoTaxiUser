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
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.yayatotaxi.R
import com.yayatotaxi.directionhelpers.FetchURL
import com.yayatotaxi.directionhelpers.TaskLoadedCallback
import com.yayatotaxi.utils.GPSTracker
import com.yayatotaxi.utils.LatLngInterpolator
import com.yayatotaxi.utils.MarkerAnimation
import com.yayatotaxi.utils.ProjectUtil
import kotlinx.android.synthetic.main.activity_car_pool_home.*
import java.util.*

class CarPoolHomeAct : AppCompatActivity(), OnMapReadyCallback, TaskLoadedCallback {

    var mContext: Context = this@CarPoolHomeAct
    private var AUTOCOMPLETE_REQUEST_CODE: Int = 101
    private var latLng: LatLng? = null
    private var latLngSource: LatLng? = null
    private var latLngDestination: LatLng? = null

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
    private var currentPolyline: Polyline? = null

    private var place1: MarkerOptions? = null
    private var place2: MarkerOptions? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_car_pool_home)

        tracker = GPSTracker(mContext)
        currentLocation = LatLng(tracker.latitude, tracker.longitude)
        latLngSource = currentLocation;
        tvFrom.setText(getAddress(currentLocation!!))
        sourceAddressLat  = tracker.latitude.toString()
        sourceAddressLon = tracker.longitude.toString()
        sourceAddress   = tvFrom.getText().toString()
        mapFragment = (supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?)!!
        mapFragment.getMapAsync(this)

        itit()
    }

    fun getAddress_(latLng: LatLng) {
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses: List<Address>?
        val address: Address?
        var fulladdress = ""
        addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)

        if (addresses.isNotEmpty()) {
            address = addresses[0]
            fulladdress =
                address.getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex
            var city = address.getLocality();
            var state = address.getAdminArea();
            var country = address.getCountryName();
            var postalCode = address.getPostalCode();
            var knownName = address.getFeatureName(); // Only if available else return NULL
        } else {
            fulladdress = "Location not found"
        }
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
        } else {
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
        googleMap.isMyLocationEnabled = true
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
        return CameraPosition.Builder().target(latLng).zoom(16f).build()
    }

    private fun itit() {
        btnBack.setOnClickListener { finish() }

        btnFindDriver.setOnClickListener {
            if (sourceAddress.isNotEmpty() && destinationAddress.isNotEmpty()) {
//                startActivity(Intent(mContext, AvailableDriversAct::class.java))
                startActivity(
                    Intent(mContext, RideOptionAct::class.java)
                        .putExtra("sourceAddress", sourceAddress)
                        .putExtra("sourceAddressLat", sourceAddressLat)
                        .putExtra("sourceAddressLon", sourceAddressLon)
                        .putExtra("destinationAddress", destinationAddress)
                        .putExtra("destinationAddressLat", destinationAddressLat)
                        .putExtra("destinationAddressLon", destinationAddressLon)
                        .putExtra("type", intent.getStringExtra("type"))
                )

            }


        }

        tvFrom.setOnClickListener {
            addressType = "source"
            val fields = listOf(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.LAT_LNG,
                Place.Field.ADDRESS
            )
            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                .setCountry(getCountry(currentLocation!!))
                .setTypeFilter(TypeFilter.ADDRESS)
                .build(this)
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
        }

        tv_Destination.setOnClickListener {
            addressType = "destination"
            val fields = listOf(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.LAT_LNG,
                Place.Field.ADDRESS
            )
//            val locale = Locale.getDefault()

//            Toast.makeText(this,getCountry(currentLocation!!),Toast.LENGTH_LONG).show()

            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                .setCountry(getCountry(currentLocation!!))
                .setTypeFilter(TypeFilter.ADDRESS)
                .build(this)
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
        }
    }

    fun getCountry(latLng: LatLng): String {
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses: List<Address>?
        val address: Address?
        var fulladdress = ""
        var countryCode = ""

        addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)

        if (addresses.isNotEmpty()) {
            address = addresses[0]
            fulladdress =
                address.getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex
            var city = address.getLocality();
            var state = address.getAdminArea();
            var country = address.getCountryName();
            countryCode = address.countryCode
            var postalCode = address.getPostalCode();
            var knownName = address.getFeatureName(); // Only if available else return NULL
        } else {
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

                        tvFrom.text = addresses

                        latLngSource = place.latLng

                    } else {
                        destinationAddress = addresses.toString()
                        destinationAddressLat = place.latLng!!.latitude.toString()
                        destinationAddressLon = place.latLng!!.longitude.toString()
                        tv_Destination.text = addresses

                        latLngDestination = place.latLng

                        if (latLngSource != null && latLngDestination != null) {
                            FetchURL(this@CarPoolHomeAct).execute(
                                getUrl(
                                    latLngSource!!,
                                    latLngDestination!!,
                                    "driving"
                                ), "driving"
                            )
                        }


                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }


    override fun onTaskDone(vararg values: Any?) {
        val height = 95
        val width = 65
        val b = BitmapFactory.decodeResource(resources, R.drawable.ic_setloc)
        val smallMarker = Bitmap.createScaledBitmap(b, width, height, false)
        val smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(smallMarker)



        if (currentPolyline != null) {
            currentPolyline!!.remove()
            googleMap!!.clear()
        }

        place1 = MarkerOptions().position(
            latLngSource!!
        ).title(
            "Pickup Location: "
        )
            .icon(smallMarkerIcon)
        googleMap.addMarker(place1!!)
        animateCamera(
            latLngSource!!)

        place2 = MarkerOptions ().position(
            latLngDestination!!
        ).title(
            "Drop Off Location: "
        )
            .icon(smallMarkerIcon)

        googleMap . addMarker (place2!!)


        currentPolyline = googleMap . addPolyline ((values[0] as PolylineOptions?)!!)
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