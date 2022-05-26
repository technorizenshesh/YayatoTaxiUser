package com.yayatotaxi.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.yayatotaxi.model.ModelLogin
import com.yayatotaxi.repository.LoginRepository

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    var loginRepository: LoginRepository? = null
    var loginViewModel: LiveData<ModelLogin?>? = null
    var context: Context? = null



    fun init(context: Context) {
        this.context = context
        loginRepository  = LoginRepository(context)
        loginViewModel = loginRepository!!.getLognUserData()
    }


    fun loginApiCallViewModel(email : String,password : String,lat : String,lon : String,registerId : String) {
        loginRepository!!.loginUser(email,password,lat,lon,registerId)
    }

    fun getLoginDataViewModel(): LiveData<ModelLogin?>? {
        return loginViewModel
    }

}