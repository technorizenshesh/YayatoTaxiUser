package com.yayatotaxi.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.gson.Gson
import com.yayatotaxi.R
import com.yayatotaxi.databinding.ActivityHomeBinding
import com.yayatotaxi.model.ModelLogin
import com.yayatotaxi.utils.*
import com.yayatotaxi.viewmodel.HomeViewModel
import com.yayatotaxi.viewmodel.LoginViewModel

class HomeAct : AppCompatActivity(), OnMapReadyCallback {
    var mContext: Context = this@HomeAct
    lateinit var binding: ActivityHomeBinding
    lateinit var mapFragment: SupportMapFragment
    var currentLocation: LatLng? = null
    lateinit var googleMap: GoogleMap
    var currentLocationMarker: Marker? = null
    var homeViewModel: HomeViewModel?=null
    lateinit var modelLogin: ModelLogin
    lateinit var sharedPref: SharedPref
    lateinit var tracker: GPSTracker


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        binding.homeViewModel = homeViewModel
        homeViewModel!!.init(mContext,binding.drawerLayout)
        sharedPref = SharedPref(mContext)
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS)
        tracker = GPSTracker(mContext)
        currentLocation = LatLng(tracker.latitude, tracker.longitude)
        initViews()


        homeViewModel!!.getUserHomeViewModel()!!.observe(this, {
            if (it != null) {
                modelLogin = it;
                Log.e("profile data===", Gson()!!.toJson(modelLogin))
                Toast.makeText(mContext,R.string.login_sucess, Toast.LENGTH_LONG)
                sharedPref.setBooleanValue(AppConstant.IS_REGISTER, true)
                sharedPref.setUserDetails(AppConstant.USER_DETAILS, modelLogin)
                Glide.with(mContext).load(modelLogin.getResult()?.image)
                    .error(R.drawable.user_ic)
                    .placeholder(R.drawable.user_ic)
                    .into(binding.childNavDrawer.userImg)
                binding.childNavDrawer.tvUsername.setText(modelLogin.getResult()?.user_name)
                binding.childNavDrawer.tvEmail.setText(modelLogin.getResult()?.email)

            } else {
                MyApplication.showAlert(mContext, getString(R.string.invalid_credentials))

            }
        })


    }

    private fun initViews() {
        mapFragment = (supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?)!!
        mapFragment.getMapAsync(this)


    /*   binding.chlidDashboard.btnAddChild.setOnClickListener {
            startActivity(Intent(mContext, SchoolRideAct::class.java))
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

        homeViewModel!!.getUserProfileApiCallViewModel(modelLogin.getResult()!!.id!!)


       // getCurrentTaxiBookingApi()
       // if(modelLogin.getResult()!!.social_status.equals("False")) showAlert(mContext,"Please Complete your profile")
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

            return
        }
        googleMap.isMyLocationEnabled=true
        tracker = GPSTracker(mContext)
        currentLocation = LatLng(tracker.latitude, tracker.longitude)
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