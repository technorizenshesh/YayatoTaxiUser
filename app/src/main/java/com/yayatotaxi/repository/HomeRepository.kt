package com.yayatotaxi.repository

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.databinding.Bindable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.yayatotaxi.R
import com.yayatotaxi.model.ModelLogin
import com.yayatotaxi.retrofit.ApiClient
import com.yayatotaxi.retrofit.YayatoApiService
import com.yayatotaxi.utils.AppConstant
import com.yayatotaxi.utils.ProjectUtil
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeRepository {
    val TAG = "HomeRepository"
    var context: Context? = null
    var apiInterface: YayatoApiService
    private var homeMutableLiveData: MutableLiveData<ModelLogin>? = null


    constructor(context: Context) {
        this.context = context
        apiInterface = ApiClient.getClient(context)!!.create(YayatoApiService::class.java)
        homeMutableLiveData = MutableLiveData()
    }

    fun userProfile(userId: String) {
        ProjectUtil.showProgressDialog(context, false, context?.getString(R.string.please_wait))

        var map = HashMap<String, String>()
        map.put("user_id", userId)
        map.put("type", AppConstant.USER)
        Log.e(TAG, "Get user profile Request = $map")
        apiInterface.profileApiCall(map).enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                ProjectUtil.pauseProgressDialog()
                try {
                    if (response.body() != null) {
                        val responseString = response.body()!!.string()
                        val jsonObject = JSONObject(responseString)
                        Log.e(TAG, "Get user Profile response = $responseString")
                        var modelLogin = Gson().fromJson(responseString, ModelLogin::class.java)
                        if (jsonObject.getString("status") == "1") {
                            homeMutableLiveData!!.postValue(modelLogin)
                        } else {
                            homeMutableLiveData!!.postValue(modelLogin)
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

    fun getUserProfileData(): LiveData<ModelLogin?>? {
        return homeMutableLiveData
    }

}