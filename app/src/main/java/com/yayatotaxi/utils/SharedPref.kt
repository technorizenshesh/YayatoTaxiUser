package com.yayatotaxi.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.yayatotaxi.model.ModelLogin

class SharedPref(context: Context) {

    private val PREFS_NAME = "yatatotaxiuser"
    val sharedPref: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun setUserDetails(Key: String, loginModel: ModelLogin) {
        val gson = Gson()
        val json: String = gson.toJson(loginModel)
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putString(Key, json)
        editor!!.commit()
    }

    fun getUserDetails(Key: String): ModelLogin {
        val gson = Gson()
        val json: String? = sharedPref.getString(Key, "{}")
        return gson.fromJson(json, ModelLogin::class.java)
    }

    fun setBooleanValue(key: String?, value: Boolean) {
        val myPrefEditor: SharedPreferences.Editor = sharedPref.edit()
        myPrefEditor.putBoolean(key, value)
        myPrefEditor.commit()
    }

    fun getBooleanValue(key: String?): Boolean {
        return sharedPref.getBoolean(key, false)
    }

    fun clearAllPreferences() {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.clear()
        editor.commit()
    }

}