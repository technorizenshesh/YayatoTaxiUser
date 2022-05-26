package com.yayatotaxi.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.yayatotaxi.model.ModelLogin
import com.yayatotaxi.repository.LoginRepository
import com.yayatotaxi.repository.SignupRepository
import java.io.File

class SignupViewModel (application: Application) : AndroidViewModel(application) {

    var signupRepository: SignupRepository? = null
    var signupMutableLiveData: LiveData<ModelLogin?>? = null
    var context: Context? = null



    fun init(context: Context) {
        this.context = context
        signupRepository  = SignupRepository(context)
        signupMutableLiveData = signupRepository!!.getSignupUserData()
    }


    fun signupApiCallViewModel( firstName: String, lastName: String, email: String, mobile: String,
                                address: String, registerId: String, lat: String, lon: String,
                                password: String, type: String, step: String, proImg: File
    ) {
        signupRepository!!.signupUser(firstName, lastName, email, mobile,
            address, registerId, lat, lon, password, type, step, proImg)
    }

    fun getSignupDataViewModel(): LiveData<ModelLogin?>? {
        return signupMutableLiveData
    }

}