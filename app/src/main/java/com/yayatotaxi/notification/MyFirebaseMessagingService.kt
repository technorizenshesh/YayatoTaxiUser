package com.yayatotaxi.notification

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.yayatotaxi.model.PushNotificationModel
import org.json.JSONObject

class MyFirebaseMessagingService : FirebaseMessagingService() {
    private var notificationUtils: NotificationUtils? = null
    override fun onCreate() {
        LocalBroadcastManager.getInstance(this).registerReceiver(
            NotifyUserReceiver(),
            IntentFilter(Config.GET_DATA_NOTIFICATION)
        )
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Check if message contains a notification payload.
        if (remoteMessage.notification != null) {
            Log.e(TAG, "Notification Body: " + remoteMessage.notification!!.body)
            val pushNotificationModel = PushNotificationModel()
            pushNotificationModel.driver_firstname=(remoteMessage.notification!!.title)
            pushNotificationModel.key=(remoteMessage.notification!!.body)
            pushNotificationModel.driver_image= remoteMessage.notification!!.imageUrl?.toString()
            pushNotificationModel.req_datetime=(""/*Utils.currentDate*/)
            handleNotification(pushNotificationModel)
        }
        // Check if message contains a data payload.
        if (remoteMessage.data!=null) {
            if (remoteMessage.data.isNotEmpty()) {
                Log.e(TAG, "Data Payload: " + remoteMessage.data.toString())
                try {
                    val jsonObject = JSONObject(remoteMessage.data as Map<*, *>)
//                    val data = remoteMessage.data
//                    val key1= data["message"]
//                    val jsonObject = JSONObject(key1 as Map<*, *>)

                    val pushNotificationModel: PushNotificationModel =
                        Gson().fromJson(
                            jsonObject.toString(),
                            PushNotificationModel::class.java
                        )
                    val intent = Intent(Config.GET_DATA_NOTIFICATION)
                    intent.putExtra("pushNotificationModel", pushNotificationModel)
                    intent.putExtra("status",pushNotificationModel.status)
                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
                    handleDataMessage(pushNotificationModel)
                } catch (e: Exception) {
                    Log.e(TAG, "Exception: " + e.message)
                }
            }
        }
    }

    private fun handleNotification(pushNotificationModel: PushNotificationModel) {
        if (!NotificationUtils.isAppIsInBackground(applicationContext)) {
            // app is in foreground, broadcast the push message
            val pushNotification = Intent(Config.PUSH_NOTIFICATION)
            pushNotification.putExtra("pushNotificationModel", pushNotificationModel)
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification)
            // play notification sound
            showNotificationMessage(applicationContext, pushNotificationModel)
        } else {
            showNotificationMessage(applicationContext, pushNotificationModel)
            // If the app is in background, firebase itself handles the notification
        }
    }

    private fun handleDataMessage(pushNotificationModel: PushNotificationModel) {
        try {
            if (!NotificationUtils.isAppIsInBackground(applicationContext)) {
                val pushNotification = Intent(Config.PUSH_NOTIFICATION)
                pushNotification.putExtra("pushNotificationModel", pushNotificationModel)
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification)
                showNotificationMessage(applicationContext, pushNotificationModel)
            } else {
                showNotificationMessage(applicationContext, pushNotificationModel)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception: " + e.message)
        }
    }

    private fun showNotificationMessage(
        context: Context,
        pushNotificationModel: PushNotificationModel
    ) {
        notificationUtils = NotificationUtils(context)
        notificationUtils!!.showNotificationMessage(pushNotificationModel)
    }

    companion object {
        private val TAG = MyFirebaseMessagingService::class.java.simpleName
    }
}