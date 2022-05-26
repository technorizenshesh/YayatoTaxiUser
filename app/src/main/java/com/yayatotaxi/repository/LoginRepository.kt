package com.yayatotaxi.repository

import android.content.Context
import android.content.Intent
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
import com.yayatotaxi.utils.MyApplication
import com.yayatotaxi.utils.ProjectUtil
import kotlin.collections.HashMap
import org.json.JSONObject
import retrofit2.Call
import okhttp3.ResponseBody
import retrofit2.Callback
import retrofit2.Response

class LoginRepository {
    val TAG = "LoginRepository"
    var context : Context? = null
    lateinit var apiInterface : YayatoApiService
    private var loginMutableLiveData: MutableLiveData<ModelLogin>? = null


    constructor(context: Context){
        this.context = context
        apiInterface = ApiClient.getClient(context)!!.create(YayatoApiService::class.java)
        loginMutableLiveData = MutableLiveData()
    }

    fun loginUser(email : String,password : String,lat : String,lon : String,registerId : String){
        ProjectUtil.showProgressDialog(context, false, context?.getString(R.string.please_wait))

        var map = HashMap<String, String>()
        map.put("email", email)
        map.put("password",password)
        map.put("lat",lat)
        map.put("lon",lon)
        map.put("type", AppConstant.USER)
        map.put("register_id", registerId)
        Log.e(TAG, "Login user Request = $map")
        apiInterface.loginApiCall(map).enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                ProjectUtil.pauseProgressDialog()
                try {
                    if (response.body() != null) {
                        val responseString = response.body()!!.string()
                        val jsonObject = JSONObject(responseString)
                        Log.e(TAG, "Login user response = $responseString")
                        if (jsonObject.getString("status") == "1") {
                           var modelLogin = Gson().fromJson(responseString, ModelLogin::class.java)
                            loginMutableLiveData!!.postValue(modelLogin)
                        } else {
                            loginMutableLiveData!!.postValue(null)
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

    fun getLognUserData(): LiveData<ModelLogin?>? {
        return loginMutableLiveData
    }


}