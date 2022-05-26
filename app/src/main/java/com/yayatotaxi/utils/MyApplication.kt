package com.yayatotaxi.utils

import android.app.AlertDialog
import android.app.Application
import android.content.Context
import android.os.CountDownTimer
import android.widget.Toast
import com.google.android.libraries.places.api.Places
import com.yayatotaxi.R

class MyApplication : Application() {

    private var downTimer: CountDownTimer? = null

    override fun onCreate() {
        super.onCreate()
        Places.initialize(applicationContext, resources.getString(R.string.api_key))
    }

    companion object {

        fun showConnectionDialog(mContext: Context) {
            val builder = AlertDialog.Builder(mContext)
            builder.setMessage(mContext.getString(R.string.please_check_internet))
                .setCancelable(false)
                .setPositiveButton(
                    mContext.getString(R.string.ok)
                ) { dialog, which -> dialog.dismiss() }.create().show()
        }

        fun showAlert(mContext: Context, text: String) {
            val builder = AlertDialog.Builder(mContext)
            builder.setMessage(text).setCancelable(false).setPositiveButton(
                mContext.getString(R.string.ok)
            ) { dialog, which -> dialog.dismiss() }.create().show()
        }

        fun showToast(mContext: Context?, msg: String) {
            Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show()
        }

        fun get(): MyApplication? {
            return MyApplication()
        }

    }

}