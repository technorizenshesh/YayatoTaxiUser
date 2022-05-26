package com.yayatotaxi.utils

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.app.NotificationManager
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Geocoder
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.SystemClock
import android.provider.MediaStore
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.loader.content.CursorLoader
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.yayatotaxi.activity.LoginAct
import com.yayatotaxi.R
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class ProjectUtil {

    companion object {
        private var mProgressDialog: ProgressDialog? = null
        private var isMarkerRotating = false
        private val snackbar: Snackbar? = null
        private val mInstance: ProjectUtil? = null

        @Synchronized
        fun getInstance(): ProjectUtil? {
            return mInstance
        }

        fun isLocationEnabled(mContext: Context): Boolean {
            val locationManager =
                mContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
            )
        }

        fun getCurrentDateTime(): String? {
            val c = Calendar.getInstance().time
            val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
            return df.format(c.time)
        }

        fun openAppInfoScreen(mContext: Context) {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", mContext.packageName, null)
            intent.data = uri
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            mContext.startActivity(intent)
        }

        fun showProgressDialog(
            context: Context?,
            isCancelable: Boolean,
            message: String?
        ): Dialog? {
            mProgressDialog = ProgressDialog(context)
            mProgressDialog!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
            mProgressDialog!!.setMessage(message)
            mProgressDialog!!.show()
            mProgressDialog!!.setCancelable(isCancelable)
            return mProgressDialog
        }

        fun checkPermissions(mContext: Context?): Boolean {
            return if (ActivityCompat.checkSelfPermission(
                    mContext!!,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    mContext, Manifest.permission.READ_EXTERNAL_STORAGE
                ) ==
                PackageManager.PERMISSION_GRANTED && (
                        ActivityCompat.checkSelfPermission(
                            (mContext),
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ) ==
                                PackageManager.PERMISSION_GRANTED)
            ) {
                true
            } else false
        }

        fun logoutAppDialog(mContext: Context) {
            val sharedPref: SharedPref = SharedPref(mContext)
            val builder = AlertDialog.Builder(mContext)
            builder.setMessage(mContext.getString(R.string.logout_text))
                .setCancelable(false)
                .setPositiveButton(
                    mContext.getString(R.string.yes)
                ) { dialog, which ->
                    sharedPref.clearAllPreferences()
                    val loginscreen = Intent(mContext, LoginAct::class.java)
                    loginscreen.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    val nManager =
                        (mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                    nManager.cancelAll()
                    mContext.startActivity(loginscreen)
                    (mContext as Activity).finish()
                }.setNegativeButton(
                    mContext.getString(R.string.no)
                ) { dialog, which -> dialog.cancel() }
            val alertDialog = builder.create()
            alertDialog.show()
        }

        fun requestPermissions(mContext: Context?) {
            ActivityCompat.requestPermissions(
                (mContext as Activity?)!!, arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ), 101
            )
        }

        fun openGallery(mContext: Context, GALLERY: Int) {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            (mContext as Activity).startActivityForResult(
                Intent.createChooser(intent, "Select Image"),
                GALLERY
            )
        }

        fun openCamera(mContext: Context, CAMERA: Int): String? {
            val dirtostoreFile =
                File(Environment.getExternalStorageDirectory().toString() + "/taxiuser/Images/")
            if (!dirtostoreFile.exists()) {
                dirtostoreFile.mkdirs()
            }
            val timestr = convertDateToString(Calendar.getInstance().timeInMillis)
            val tostoreFile = File(
                Environment.getExternalStorageDirectory()
                    .toString() + "/taxiuser/Images/" + "IMG_" + timestr + ".jpg"
            )
            val str_image_path = tostoreFile.path
            val uriSavedImage = FileProvider.getUriForFile(
                Objects.requireNonNull(mContext as Activity),
                "com.taxiuser" + ".provider", tostoreFile
            )
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage)
            mContext.startActivityForResult(intent, CAMERA)
            return str_image_path
        }

        fun convertDateToString(l: Long): String {
            var str = ""
            val date = Date(l)
            val dateFormat = SimpleDateFormat("dd_MM_yyyy_HH_mm_ss")
            str = dateFormat.format(date)
            return str
        }

        fun getRealPathFromURI(mContext: Context?, contentUri: Uri): String? {
            // TODO: get realpath from uri
            var stringPath: String? = null
            try {
                if (contentUri.scheme.toString().compareTo("content") == 0) {
                    val proj = arrayOf(MediaStore.Images.Media.DATA)
                    val loader =
                        CursorLoader((mContext as Activity?)!!, contentUri, proj, null, null, null)
                    val cursor = loader.loadInBackground()
                    val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                    cursor.moveToFirst()
                    stringPath = cursor.getString(column_index)
                    cursor.close()
                } else if (contentUri.scheme!!.compareTo("file") == 0) {
                    stringPath = contentUri.path
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return stringPath
        }

        fun updateResources(context: Context, language: String?) {
            val locale = Locale(language)
            Locale.setDefault(locale)
            val resources = context.resources
            val configuration = resources.configuration
            configuration.locale = locale
            resources.updateConfiguration(configuration, resources.displayMetrics)
        }


        fun clearNortifications(mContext: Context) {
            val nManager =
                mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nManager.cancelAll()
        }

        fun sendEmail(mContext: Context, email: String) {
            val emailSelectorIntent = Intent(Intent.ACTION_SENDTO)
            emailSelectorIntent.data = Uri.parse("mailto:")
            val emailIntent = Intent(Intent.ACTION_SEND)
            emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "")
            emailIntent.selector = emailSelectorIntent
            if (emailIntent.resolveActivity(mContext.packageManager) != null) mContext.startActivity(
                emailIntent
            )
        }

        fun call(mContext: Context, no: String?) {
            val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", no, null))
            mContext.startActivity(intent)
        }

        fun navigateToGooogleMap(mContext: Context, sAddres: String, dAddress: String) {
            try {
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://maps.google.com/maps?saddr=$sAddres&daddr=$dAddress")
                )
                intent.setPackage("com.google.android.apps.maps")
                mContext.startActivity(intent)
            } catch (e: Exception) {
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://maps.google.com/maps?saddr=$sAddres&daddr=$dAddress")
                )
                mContext.startActivity(intent)
            }
        }

//    public static void imageShowFullscreenDialog(Context mContext,String url) {
//        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
//        dialog.setContentView(R.layout.image_fullscreen_dialog);
//        TouchImageView ivImage = dialog.findViewById(R.id.ivImage);
//        ivImage.setMaxZoom(4f);
//        Glide.with(mContext).load(url).into(ivImage);
//        dialog.show();
//    }

        //    public static void imageShowFullscreenDialog(Context mContext,String url) {
        //        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
        //        dialog.setContentView(R.layout.image_fullscreen_dialog);
        //        TouchImageView ivImage = dialog.findViewById(R.id.ivImage);
        //        ivImage.setMaxZoom(4f);
        //        Glide.with(mContext).load(url).into(ivImage);
        //        dialog.show();
        //    }
        fun callCustomer(mContext: Context, no: String?) {
            val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", no, null))
            mContext.startActivity(intent)
        }

        fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
            val bytes = ByteArrayOutputStream()
            inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
            val path = MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title" + System.currentTimeMillis(), null)
            return Uri.parse(path)
        }

        fun pauseProgressDialog() {
            try {
                if (mProgressDialog != null) {
                    mProgressDialog!!.cancel()
                    mProgressDialog!!.dismiss()
                    mProgressDialog = null
                }
            } catch (ex: IllegalArgumentException) {
                ex.printStackTrace()
            }
        }

        fun getPolyLineUrl(context: Context, origin: LatLng, dest: LatLng): String? {
            val str_origin = "origin=" + origin.latitude + "," + origin.longitude
            val str_dest = "destination=" + dest.latitude + "," + dest.longitude
            val sensor = "sensor=false"
            val parameters =
                str_origin + "&" + str_dest + "&" + sensor + "&key=" + context.resources.getString(R.string.api_key)
            val output = "json"
            val url = "https://maps.googleapis.com/maps/api/directions/$output?$parameters"
            Log.e("PathURL", "====>$url")
            return url
        }

        fun blinkAnimation(view: View) {
            val anim: Animation = AlphaAnimation(0.0f, 1.0f)
            anim.duration = 50
            anim.startOffset = 20
            anim.repeatMode = Animation.REVERSE
            view.startAnimation(anim)
        }

        fun isValidEmail(target: CharSequence?): Boolean {
            return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
        }

        private fun isValidPhoneNumber(phoneNumber: CharSequence): Boolean {
            return if (!TextUtils.isEmpty(phoneNumber)) {
                Patterns.PHONE.matcher(phoneNumber).matches()
            } else false
        }

        fun changeStatusBarColor(activity: Activity) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                activity.window.statusBarColor =
                    activity.resources.getColor(R.color.purple_200, activity.theme)
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                activity.window.statusBarColor = activity.resources.getColor(R.color.purple_200)
            }
        }

        fun getCurrentDate(): String? {
            val dateFormat = SimpleDateFormat("dd-MMM-yyyy")
            return dateFormat.format(Date())
        }

        fun getCurrentTime(): String? {
            val dateFormat = SimpleDateFormat("HH:mm:ss")
            return dateFormat.format(Date())
        }

        fun convertDateIntoMillisecond(date: String): Long {
            val date_ = date
            val sdf = SimpleDateFormat("dd-MMM-yyyy")
            try {
                val mDate = sdf.parse(date)
                val timeInMilliseconds = mDate.time
                println("Date in milli :: $timeInMilliseconds")
                return timeInMilliseconds
            } catch (e: Exception) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }
            return 0
        }

        fun getCompleteAddressString(
            context: Context?,
            LATITUDE: Double,
            LONGITUDE: Double
        ): String? {
            var strAdd = "getting address..."
            if (context != null) {
                val geocoder = Geocoder(context.applicationContext, Locale.getDefault())
                try {
                    val addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1)
                    if (addresses != null) {
                        val returnedAddress = addresses[0]
                        val strReturnedAddress = StringBuilder("")
                        for (i in 0..returnedAddress.maxAddressLineIndex) {
                            strReturnedAddress.append(returnedAddress.getAddressLine(i))
                                .append("\n")
                        }
                        strAdd = strReturnedAddress.toString()
                        Log.w("My Current address", strReturnedAddress.toString())
                    } else {
                        strAdd = "No Address Found"
                        Log.w("My Current address", "No Address returned!")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    strAdd = "Cant get Address"
                    Log.w("My Current address", "Canont get Address!")
                }
            }
            return strAdd
        }

        private fun bearingBetweenLocations (
            marker: MarkerOptions,
            latLng1: LatLng,
            latLng2: LatLng
        ) {
            val PI = 3.14159
            val lat1 = latLng1.latitude * PI / 180
            val long1 = latLng1.longitude * PI / 180
            val lat2 = latLng2.latitude * PI / 180
            val long2 = latLng2.longitude * PI / 180
            val dLon = long2 - long1
            val y = Math.sin(dLon) * Math.cos(lat2)
            val x = Math.cos(lat1) * Math.sin(lat2) - (Math.sin(lat1)
                    * Math.cos(lat2) * Math.cos(dLon))
            var brng = Math.atan2(y, x)
            brng = Math.toDegrees(brng)
            brng = (brng + 360) % 360
            rotateMarker(marker, brng.toFloat())
        }

        fun rotateMarker(marker: MarkerOptions, toRotation: Float) {
            if (!isMarkerRotating) {
                val handler = Handler()
                val start = SystemClock.uptimeMillis()
                val startRotation = marker.rotation
                val duration: Long = 1000
                val interpolator: Interpolator = LinearInterpolator()
                handler.post(object : Runnable {
                    override fun run() {
                        isMarkerRotating = true
                        val elapsed = SystemClock.uptimeMillis() - start
                        val t = interpolator.getInterpolation(elapsed.toFloat() / duration)
                        val rot = t * toRotation + (1 - t) * startRotation
                        marker.rotation(if (-rot > 180) rot / 2 else rot)
                        if (t < 1.0) {
                            // Post again 16ms later.
                            handler.postDelayed(this, 16)
                        } else {
                            isMarkerRotating = false
                        }
                    }
                })
            }
        }

    }


}