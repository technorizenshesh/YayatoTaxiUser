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
import com.yayatotaxi.utils.AppConstant
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

class SignupRepository {
    val TAG = "SignupRepository"
    var context: Context? = null
    var apiInterface: YayatoApiService
    private var signupMutableLiveData: MutableLiveData<ModelLogin>? = null


    constructor(context: Context) {
        this.context = context
        apiInterface = ApiClient.getClient(context)!!.create(YayatoApiService::class.java)
        signupMutableLiveData = MutableLiveData()
    }

    fun signupUser(
        firstName: String, lastName: String, email: String, mobile: String,
        address: String, registerId: String, lat: String, lon: String,
        password: String, type: String, step: String, proImg: File
    ) {
        ProjectUtil.showProgressDialog(context, false, context?.getString(R.string.please_wait))

        val profileFilePart : MultipartBody.Part

        val first_name = RequestBody.create(MediaType.parse("text/plain"), firstName)
        val last_name = RequestBody.create(MediaType.parse("text/plain"), lastName)
        val email = RequestBody.create(MediaType.parse("text/plain"), email)
        val mobile = RequestBody.create(MediaType.parse("text/plain"), mobile)
        val address = RequestBody.create(MediaType.parse("text/plain"), address)
        val register_id = RequestBody.create(MediaType.parse("text/plain"), registerId)
        val lat = RequestBody.create(MediaType.parse("text/plain"), lat)
        val lon = RequestBody.create(MediaType.parse("text/plain"), lon)
        val password = RequestBody.create(MediaType.parse("text/plain"), password)
        val type = RequestBody.create(MediaType.parse("text/plain"), type)
        val step = RequestBody.create(MediaType.parse("text/plain"), step)

        profileFilePart = MultipartBody.Part.createFormData(
            "image", proImg!!.name,
            RequestBody.create(MediaType.parse("image/*"), proImg)
        )

        apiInterface.signUpUserCallApi(
            first_name, last_name, email, mobile,
            address, register_id, lat, lon, password, type, step, profileFilePart
        ).enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                ProjectUtil.pauseProgressDialog()
                try {
                    if (response.body() != null) {
                        val responseString = response.body()!!.string()
                        val jsonObject = JSONObject(responseString)
                        Log.e(TAG, "Login user response = $responseString")
                        var modelLogin = Gson().fromJson(responseString, ModelLogin::class.java)
                        if (jsonObject.getString("status") == "1") {
                            signupMutableLiveData!!.postValue(modelLogin)
                        }
                       else if (jsonObject.getString("status") == "0") {
                            var modelLogin = Gson().fromJson(responseString, ModelLogin::class.java)
                            signupMutableLiveData!!.postValue(modelLogin)
                        } else {
                            signupMutableLiveData!!.postValue(null)
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


    fun getSignupUserData(): LiveData<ModelLogin?>? {
        return signupMutableLiveData
    }


}