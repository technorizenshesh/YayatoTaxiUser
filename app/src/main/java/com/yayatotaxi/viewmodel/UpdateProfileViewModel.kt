package com.yayatotaxi.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.yayatotaxi.model.ModelLogin
import com.yayatotaxi.repository.UpdateProfileRepository
import okhttp3.MultipartBody
import java.io.File

class UpdateProfileViewModel (application: Application) : AndroidViewModel(application) {

    var updateProfileRepository: UpdateProfileRepository? = null
    var updateProfileMutableLiveData: LiveData<ModelLogin?>? = null
    var context: Context? = null



    fun init(context: Context) {
        this.context = context
        updateProfileRepository  = UpdateProfileRepository(context)
        updateProfileMutableLiveData = updateProfileRepository!!.getUpdateProfileUserData()
    }


    fun updateProfileApiCallViewModel( firstName: String, lastName: String, email: String, mobile: String,
                                address: String, lat: String, lon: String,
                                 type: String, id: String, proImg: MultipartBody.Part
    ) {
        updateProfileRepository!!.updateProfileUser(firstName, lastName, email, mobile,
            address, lat, lon, type, id, proImg)
    }

    fun getUpdateProfileDataViewModel(): LiveData<ModelLogin?>? {
        return updateProfileMutableLiveData
    }

}
