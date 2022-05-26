package com.yayatotaxi.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.gson.Gson
import com.yayatotaxi.R
import com.yayatotaxi.databinding.ActivityHomeBinding
import com.yayatotaxi.model.ModelLogin
import com.yayatotaxi.utils.*

class HomeAct : AppCompatActivity(), OnMapReadyCallback {
    var mContext: Context = this@HomeAct
    lateinit var binding: ActivityHomeBinding
    lateinit var mapFragment: SupportMapFragment
    lateinit var sharedPref: SharedPref
    lateinit var modelLogin: ModelLogin
    lateinit var tracker: GPSTracker
    var currentLocation: LatLng? = null
    lateinit var googleMap: GoogleMap
    var currentLocationMarker: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        initViews()
    }

    private fun initViews() {
        mapFragment = (supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?)!!
        mapFragment.getMapAsync(this)

        binding.chlidDashboard.navbar.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }

        binding.childNavDrawer.tvHome.setOnClickListener {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }

        binding.childNavDrawer.tvProfile.setOnClickListener {
            startActivity(Intent(mContext, UpdateProfielAct::class.java))
            binding.drawerLayout.closeDrawer(GravityCompat.START)

        }


        binding.childNavDrawer.signout.setOnClickListener {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            ProjectUtil.logoutAppDialog(mContext)
        }




     /*   binding.chlidDashboard.btnAddChild.setOnClickListener {
            startActivity(Intent(mContext, SchoolRideAct::class.java))
        }



        binding.chlidDashboard.cvCarPool.setOnClickListener {
            startActivity(
                Intent(mContext, CarPoolHomeAct::class.java)
                .putExtra("type", "classic")
            )
        }


        binding.chlidDashboard.cvBookNow.setOnClickListener {
            startActivity(
                Intent(mContext, CarPoolHomeAct::class.java)
                    .putExtra("type", "vtc")
            )
        }

        binding.chlidDashboard.cvREntalTaxi.setOnClickListener {
            startActivity(
                Intent(mContext, NormalBookHomeAct::class.java)
                    .putExtra("type", "rent")
            )
        }

        binding.childNavDrawer.tvPoolRequest.setOnClickListener {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            startActivity(Intent(mContext, PoolRequestAct::class.java))
        }

        binding.childNavDrawer.tvRentRequest.setOnClickListener {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            startActivity(Intent(mContext, RentRequestAct::class.java))
        }

        binding.childNavDrawer.tvWallet.setOnClickListener {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            startActivity(Intent(mContext, WalletAct::class.java))
        }

        binding.childNavDrawer.tvRideHistory.setOnClickListener {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            startActivity(Intent(mContext, RideHistoryAct::class.java))
        }

        binding.chlidDashboard.goDetail.setOnClickListener {
            startActivity(Intent(mContext, TrackAct::class.java).putExtra("id", requestId))
        }*/
    }

    override fun onResume() {
        super.onResume()
        setUserData()

       // getCurrentTaxiBookingApi()
       // if(modelLogin.getResult()!!.social_status.equals("False")) showAlert(mContext,"Please Complete your profile")
    }


    fun setUserData(){
        sharedPref = SharedPref(mContext)
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS)
        Glide.with(mContext).load(modelLogin.getResult()?.image)
            .error(R.drawable.user_ic)
            .placeholder(R.drawable.user_ic)
            .into(binding.childNavDrawer.userImg)
        binding.childNavDrawer.tvUsername.setText(modelLogin.getResult()?.user_name)
        binding.childNavDrawer.tvEmail.setText(modelLogin.getResult()?.email)
        Log.e("sfasdasdas", "modelLogin.getResult()?.image = " + modelLogin.getResult()?.image)
        Log.e("sfasdasdas", "modelLogin Gson = " + Gson().toJson(modelLogin))

        tracker = GPSTracker(mContext)
        currentLocation = LatLng(tracker.latitude, tracker.longitude)
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
                    val b = BitmapFactory.decodeResource(resources, R.drawable.ic_setloc)
                    val smallMarker = Bitmap.createScaledBitmap(b, width, height, false)
                    val smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(smallMarker)
                    currentLocationMarker = googleMap.addMarker(
                        MarkerOptions().position(currentLocation).title("My Location")
                            .icon(smallMarkerIcon)
                    )
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

}