package com.yayatotaxi.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.yayatotaxi.R
import com.yayatotaxi.utils.AppConstant
import com.yayatotaxi.utils.SharedPref
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class SplashAct : AppCompatActivity() {
    var mContext: Context = this@SplashAct
    lateinit var sharedPref: SharedPref
    var PERMISSION_ID: Int = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        sharedPref = SharedPref(mContext)

    }


    private fun printHash(mContext: Context) {
        Log.i("dsadadsdad", "printHashKey() Hash Key: aaya ander")
        try {
            val info: PackageInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val hashKey = String(Base64.encode(md.digest(), 0))
                Log.i("dsadadsdad", "printHashKey() Hash Key: $hashKey")
            }
        } catch (e: NoSuchAlgorithmException) {
            Log.e("dsadadsdad", "printHashKey()", e)
        } catch (e: Exception) {
            Log.e("dsadadsdad", "printHashKey()", e)
        }
    }

    override fun onResume() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                processNextActivity()
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
        printHash(mContext)

        super.onResume()


    }

    private fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission (
            this, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            this, Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            this, Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            this, Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ), PERMISSION_ID
        )
    }

    private fun processNextActivity() {
        Handler(Looper.getMainLooper()).postDelayed({
            if (sharedPref.getBooleanValue(AppConstant.IS_REGISTER)) {
                startActivity(Intent(mContext, HomeAct::class.java))
                finish()
            } else {
                startActivity(Intent(mContext, LoginAct::class.java))
                finish()
            }
        }, 2000)
    }


}