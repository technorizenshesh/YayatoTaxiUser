package com.yayatotaxi.repository

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.yayatotaxi.R
import com.yayatotaxi.model.ModelLogin
import com.yayatotaxi.retrofit.ApiClient
import com.yayatotaxi.retrofit.YayatoApiService
import com.yayatotaxi.utils.ProjectUtil
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class UpdateProfileRepository {
    val TAG = "UpdateProfileRepository"
    var context: Context? = null
    var apiInterface: YayatoApiService
    private var updateProfileMutableLiveData: MutableLiveData<ModelLogin>? = null


    constructor(context: Context) {
        this.context = context
        apiInterface = ApiClient.getClient(context)!!.create(YayatoApiService::class.java)
        updateProfileMutableLiveData = MutableLiveData()
    }

    fun updateProfileUser(
        firstName: String, lastName: String, email: String, mobile: String,
        address: String, lat: String, lon: String, type: String, userId: String, proImg: MultipartBody.Part
    ) {
        ProjectUtil.showProgressDialog(context, false, context?.getString(R.string.please_wait))




        val first_name = RequestBody.create(MediaType.parse("text/plain"), firstName)
        val last_name = RequestBody.create(MediaType.parse("text/plain"), lastName)
        val email = RequestBody.create(MediaType.parse("text/plain"), email)
        val mobile = RequestBody.create(MediaType.parse("text/plain"), mobile)
        val address = RequestBody.create(MediaType.parse("text/plain"), address)
        val lat = RequestBody.create(MediaType.parse("text/plain"), lat)
        val lon = RequestBody.create(MediaType.parse("text/plain"), lon)
        val type = RequestBody.create(MediaType.parse("text/plain"), type)
        val user_id: RequestBody = RequestBody.create(MediaType.parse("text/plain"),userId)




        apiInterface.updateDriverCallApi(first_name, last_name, email, mobile, address, lat, lon, type,user_id,proImg
        ).enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                ProjectUtil.pauseProgressDialog()
                try {
                    if (response.body() != null) {
                        val responseString = response.body()!!.string()
                        val jsonObject = JSONObject(responseString)
                        Log.e(TAG, "Update profile user response = $responseString")
                        var modelLogin = Gson().fromJson(responseString, ModelLogin::class.java)
                        if (jsonObject.getString("status") == "1") {
                            updateProfileMutableLiveData!!.postValue(modelLogin)
                        }
                        else if (jsonObject.getString("status") == "0") {
                            var modelLogin = Gson().fromJson(responseString, ModelLogin::class.java)
                            updateProfileMutableLiveData!!.postValue(modelLogin)
                        } else {
                            updateProfileMutableLiveData!!.postValue(null)
                        }

                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "Exception = " + e.message, Toast.LENGTH_SHORT).show()
                    Log.e("Exception", "Exception = " + e.message)
                }
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                ProjectUtil.pauseProgressDialog()
            }

        })

    }


    fun getUpdateProfileUserData(): LiveData<ModelLogin?>? {
        return updateProfileMutableLiveData
    }
}