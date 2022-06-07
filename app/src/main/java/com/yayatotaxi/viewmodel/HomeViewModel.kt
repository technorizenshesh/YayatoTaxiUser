package com.yayatotaxi.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.GravityCompat
import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.yayatotaxi.activity.CarPoolHomeAct
import com.yayatotaxi.activity.NormalBookHomeAct
import com.yayatotaxi.activity.UpdateProfielAct
import com.yayatotaxi.model.ModelLogin
import com.yayatotaxi.repository.HomeRepository
import com.yayatotaxi.utils.ProjectUtil

class HomeViewModel(application: Application): AndroidViewModel(application) ,Observable{

    var homeRepository: HomeRepository? = null
    var homeViewModel: LiveData<ModelLogin?>? = null
    var context: Context? = null
    lateinit var drawerLayout : DrawerLayout
    @Bindable
    val userName = MutableLiveData<String>()
    @Bindable
    val email = MutableLiveData<String>()


    fun init(context: Context,drawerLayout : DrawerLayout) {
        this.context = context
        this.drawerLayout = drawerLayout
        homeRepository  = HomeRepository(context)
        homeViewModel = homeRepository!!.getUserProfileData()
    }


    fun getUserProfileApiCallViewModel(userId : String) {
        homeRepository!!.userProfile(userId)
    }

    fun getUserHomeViewModel(): LiveData<ModelLogin?>? {
        return homeViewModel
    }


    fun profileClick(view: View){
        context!!.startActivity(Intent(context, UpdateProfielAct::class.java))
        openCloseNavigationDrawer(view)
    }


    fun logoutClick(view: View){
        ProjectUtil.logoutAppDialog(context!!)
    }


    fun cvPoolClick(view: View){
      //  context!!.startActivity(Intent(context, CarPoolHomeAct::class.java)
      //      .putExtra("type", "classic"))
       // openCloseNavigationDrawer(view)
        Toast.makeText(context,"Coming soon...",Toast.LENGTH_LONG).show()
    }


    fun bookNowClick(view: View){
    //    context!!.startActivity(Intent(context, CarPoolHomeAct::class.java)
    //        .putExtra("type", "vtc"))
      //  openCloseNavigationDrawer(view)
        Toast.makeText(context,"Coming soon...",Toast.LENGTH_LONG).show()

    }


    fun rentalTaxiClick(view: View){
        context!!.startActivity(Intent(context, NormalBookHomeAct::class.java)
            .putExtra("type", "rent"))
       // openCloseNavigationDrawer(view)
    }









    fun openCloseNavigationDrawer(view: View) {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            drawerLayout.openDrawer(GravityCompat.START)
        }
    }



    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
    }






}