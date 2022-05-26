package com.yayatotaxi.activity

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.firebase.messaging.FirebaseMessaging
import com.yayatotaxi.R
import com.yayatotaxi.model.ModelLogin
import com.yayatotaxi.utils.MyApplication
import com.yayatotaxi.utils.ProjectUtil
import com.yayatotaxi.utils.RealPathUtil
import com.yayatotaxi.utils.SharedPref
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.io.File
import java.util.HashMap

class SignUpAct: AppCompatActivity() {
    var TAG = "SignUpAct"
    var mContext: Context = this@SignUpAct
    private var AUTOCOMPLETE_REQUEST_CODE: Int = 101
    private val GALLERY = 0;
    private val CAMERA = 1;
    lateinit var registerId: String
    var sharedPref: SharedPref? = null
    var modelLogin: ModelLogin? = null
    var profileImage: File? = null
    private var latLng: LatLng? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        sharedPref = SharedPref(mContext)
        initViews()
    }

    private fun initViews() {
        sharedPref = SharedPref(mContext)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        FirebaseMessaging.getInstance().token.addOnSuccessListener { token: String ->
            if (!TextUtils.isEmpty(token)) {
                registerId = token
                Log.e(TAG, "retrieve token successful : $token")
            } else {
                Log.e(TAG, "token should not be null...")
            }
        }


        ivBack.setOnClickListener { finish() }

        etAdd1.setOnClickListener {
            val fields = listOf(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.LAT_LNG,
                Place.Field.ADDRESS
            )
            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                .build(this)
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
        }

        addIcon.setOnClickListener {
            if (ProjectUtil.checkPermissions(mContext)) {
                showPictureDialog()
            } else {
                ProjectUtil.requestPermissions(mContext)
            }
        }

        btnSignUp.setOnClickListener {
         validation()
        }



    }

    private fun validation() {
        if (TextUtils.isEmpty(etFirstName.text.toString().trim())) {
            MyApplication.showAlert(mContext, getString(R.string.enter_name_firsttext))
        } else if (TextUtils.isEmpty(etLastName.text.toString().trim())) {
            MyApplication.showAlert(mContext, getString(R.string.enter_name_lasttext))
        } else if (TextUtils.isEmpty(etEmail.text.toString().trim())) {
            MyApplication.showAlert(mContext, getString(R.string.enter_email_text))
        } else if (TextUtils.isEmpty(etPhone.text.toString().trim())) {
            MyApplication.showAlert(mContext, getString(R.string.enter_phone_text))
        } else if (TextUtils.isEmpty(etAdd1.text.toString().trim())) {
            MyApplication.showAlert(mContext, getString(R.string.enter_address1_text))
        } else if (!ProjectUtil.isValidEmail(etEmail.text.toString().trim())) {
            MyApplication.showAlert(mContext, getString(R.string.enter_valid_email))
        } else if (TextUtils.isEmpty(etPassword.text.toString().trim())) {
            MyApplication.showAlert(mContext, getString(R.string.please_enter_pass))
        } else if (etPassword.text.toString().trim().length < 4) {
            MyApplication.showAlert(mContext, getString(R.string.password_validation_text))
        } else if (profileImage == null) {
            MyApplication.showAlert(mContext, getString(R.string.please_upload_profile))
        } else if (!cbAcceptTerms.isChecked) {
            MyApplication.showAlert(mContext, getString(R.string.please_accept_term_con))
        } else {
            val params = HashMap<String, String>()
            val fileHashMap = HashMap<String, File>()
            params["first_name"] = etFirstName.text.toString().trim()
            params["last_name"] = etLastName.text.toString().trim()
            params["email"] = etEmail.text.toString().trim()
            params["mobile"] = etPhone.text.toString().trim()
            params["city"] = etCityName.text.toString().trim()
            params["address"] = etAdd1.text.toString().trim()
            params["register_id"] = registerId
            params["lat"] = latLng!!.latitude.toString()
            params["lon"] = latLng!!.longitude.toString()
            params["password"] = etPassword.text.toString().trim()
            params["type"] = "USER"
            fileHashMap["image"] = profileImage!!

            Log.e(TAG, "signupUser = $params")
            Log.e(TAG, "fileHashMap = $fileHashMap")

            val mobileNumber = "+237" + etPhone.text.toString().trim()
          //    val mobileNumber = "+91"  + etPhone.text.toString().trim()

            startActivity(
                Intent(mContext, VerifyAct::class.java)
                    .putExtra("resgisterHashmap", params)
                    .putExtra("mobile", mobileNumber)
                    .putExtra("fileHashMap", fileHashMap)
            )

        }
    }


    private fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(mContext)
        pictureDialog.setTitle("Select Action")
        val pictureDialogItems = arrayOf("Select photo from gallery", "Capture photo from camera")
        pictureDialog.setItems(
            pictureDialogItems
        ) { dialog, which ->
            when (which) {
                0 -> {
                    val galleryIntent = Intent(
                        Intent.ACTION_GET_CONTENT,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    )
                    galleryIntent.type = "image/*"
                    startActivityForResult(galleryIntent, GALLERY)
                }
                1 -> {
                    val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    if (cameraIntent.resolveActivity(mContext.packageManager) != null)
                        startActivityForResult(cameraIntent, CAMERA)
                }
            }
        }
        pictureDialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                val place = Autocomplete.getPlaceFromIntent(data!!)
                latLng = place.latLng
                try {
                    val addresses = ProjectUtil.getCompleteAddressString(
                        mContext, place.latLng!!.latitude, place.latLng!!.longitude
                    )
                    etAdd1.setText(addresses)
                } catch (e: Exception) {
                }
            }
        } else if (requestCode == GALLERY) {
            if (resultCode == RESULT_OK) {
                val path: String = RealPathUtil.getRealPath(mContext, data!!.data)!!
                profileImage = File(path)
                profileImageSet.setImageURI(Uri.parse(path))
            }
        } else if (requestCode == CAMERA) {
            if (resultCode == RESULT_OK) {
                try {
                    if (data != null) {
                        val extras = data.extras
                        val bitmapNew = extras!!["data"] as Bitmap
                        val imageBitmap: Bitmap =
                            BITMAP_RE_SIZER(bitmapNew, bitmapNew!!.width, bitmapNew!!.height)!!
                        val tempUri: Uri = ProjectUtil.getImageUri(mContext, imageBitmap)!!
                        val image = RealPathUtil.getRealPath(mContext, tempUri)
                        profileImage = File(image)
                        Log.e("sgfsfdsfs", "profileImage = $profileImage")
                        profileImageSet.setImageURI(Uri.parse(image))
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun BITMAP_RE_SIZER(bitmap: Bitmap, newWidth: Int, newHeight: Int): Bitmap? {
        val scaledBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888)
        val ratioX = newWidth / bitmap.width.toFloat()
        val ratioY = newHeight / bitmap.height.toFloat()
        val middleX = newWidth / 2.0f
        val middleY = newHeight / 2.0f
        val scaleMatrix = Matrix()
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY)
        val canvas = Canvas(scaledBitmap)
        canvas.setMatrix(scaleMatrix)
        canvas.drawBitmap(
            bitmap,
            middleX - bitmap.width / 2,
            middleY - bitmap.height / 2,
            Paint(Paint.FILTER_BITMAP_FLAG)
        )
        return scaledBitmap
    }


}