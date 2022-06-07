package com.yayatotaxi.viewmodel

import android.app.Application
import android.content.Context
import androidx.databinding.Observable
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.google.android.gms.maps.model.LatLng
import com.yayatotaxi.model.ModelLogin
import com.yayatotaxi.repository.HomeRepository
import com.yayatotaxi.utils.GPSTracker

class NormalBookViewModel (application: Application): AndroidViewModel(application) , Observable {
    var context: Context? = null
    lateinit var tracker: GPSTracker

    var currentLocation: LatLng? = null

    fun init(context: Context) {
        this.context = context
        tracker = GPSTracker(context!!)
        currentLocation =  LatLng(tracker.latitude, tracker.longitude)

    }




    fun getCurrentAddress(): LatLng? {
        return currentLocation
    }


    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        TODO("Not yet implemented")
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        TODO("Not yet implemented")
    }

}