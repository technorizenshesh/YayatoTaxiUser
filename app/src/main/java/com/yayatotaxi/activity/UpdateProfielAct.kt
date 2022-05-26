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
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.gson.Gson
import com.yayatotaxi.R
import com.yayatotaxi.model.ModelLogin
import com.yayatotaxi.utils.*
import com.yayatotaxi.viewmodel.LoginViewModel
import com.yayatotaxi.viewmodel.UpdateProfileViewModel
import kotlinx.android.synthetic.main.activity_update_profile.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class UpdateProfielAct : AppCompatActivity(){
    val TAG = "UpdateProfielAct";
    var mContext: Context = this@UpdateProfielAct
    private var PERMISSION_ID: Int = 1001
    private var AUTOCOMPLETE_REQUEST_CODE: Int = 101
    private val GALLERY = 0;
    private val CAMERA = 1;
    lateinit var registerId: String
    lateinit var sharedPref: SharedPref
    lateinit var modelLogin: ModelLogin
    var profileImage: File? = null
    private var latLng: LatLng? = null
    var updateProfileViewModel : UpdateProfileViewModel? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_profile)
        sharedPref = SharedPref(mContext)
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        updateProfileViewModel = ViewModelProviders.of(this).get(UpdateProfileViewModel::class.java)
        updateProfileViewModel!!.init(mContext)
        initViews()

        updateProfileViewModel!!.getUpdateProfileDataViewModel()!!.observe(this, {
            if (it != null) {
                modelLogin = it;
                Log.e("update profile data===", Gson()!!.toJson(modelLogin))
                Toast.makeText(mContext,R.string.profile_updated, Toast.LENGTH_LONG)
                sharedPref.setBooleanValue(AppConstant.IS_REGISTER, true)
                sharedPref.setUserDetails(AppConstant.USER_DETAILS, modelLogin)
                finish()

            } else {
                MyApplication.showAlert(mContext, getString(R.string.invalid_input))

            }
        })

    }

    private fun initViews() {
        setUserDataaa()
        ivBack.setOnClickListener { finish() }
        try {
            latLng = LatLng(
                modelLogin!!.getResult()!!.lat!!.toDouble(),
                modelLogin!!.getResult()!!.lon!!.toDouble()
            )
        } catch (e: Exception) {
        }


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

        btnSave.setOnClickListener {
           validation()
        }


    }

    private fun validation() {
        var lat : String
        var lon : String
        val profileFilePart : MultipartBody.Part
        val attachmentEmpty: RequestBody
        if (profileImage == null) {
            attachmentEmpty = RequestBody.create(MediaType.parse("text/plain"), "")
            profileFilePart = MultipartBody.Part.createFormData("attachment", "", attachmentEmpty)
        } else {
            profileFilePart = MultipartBody.Part.createFormData("image", profileImage!!.name, RequestBody.create(MediaType.parse("image/*"), profileImage))
        }

        if (TextUtils.isEmpty(etFirstName.text.toString().trim())) {
            MyApplication.showAlert(mContext,getString(R.string.enter_name_firsttext))
        } else if (TextUtils.isEmpty(etLastName.text.toString().trim())) {
            MyApplication.showAlert(mContext,getString(R.string.enter_name_lasttext))
        } else if (TextUtils.isEmpty(etEmail.text.toString().trim())) {
            MyApplication.showAlert(mContext,getString(R.string.enter_email_text))
        } else if (TextUtils.isEmpty(etPhone.text.toString().trim())) {
            MyApplication.showAlert(mContext,getString(R.string.enter_phone_text))
        } else if (TextUtils.isEmpty(etAdd1.text.toString().trim())) {
            MyApplication.showAlert(mContext,getString(R.string.enter_address1_text))
        } else if (!ProjectUtil.isValidEmail(etEmail.text.toString().trim())) {
            MyApplication.showAlert(mContext,getString(R.string.enter_valid_email))
        } else {
            try {
                lat = latLng!!.latitude.toString()
                lon = latLng!!.longitude.toString()
            } catch (e: java.lang.Exception) {
                lat = ""
                lon = ""
            }
            if (InternetConnection.checkConnection(mContext)) {
                updateProfileViewModel!!.updateProfileApiCallViewModel(etFirstName.text.toString().trim(),etLastName.text.toString().trim(),etEmail.text.toString().trim(),
                        etPhone.text.toString().trim(),etAdd1.text.toString().trim(),lat,lon,AppConstant.USER,modelLogin.getResult()!!.id.toString(),profileFilePart
                    )
                }

            else {
                MyApplication.showConnectionDialog(mContext)

            }

        }
    }

    private fun setUserDataaa() {
        etFirstName.setText(modelLogin?.getResult()?.first_name)
        etLastName.setText(modelLogin?.getResult()?.last_name)
        etPhone.setText(modelLogin?.getResult()?.mobile)
        etEmail.setText(modelLogin?.getResult()?.email)
        etAdd1.setText(modelLogin?.getResult()?.address)

        Log.e(TAG, " User Profile data = " + Gson().toJson(modelLogin))

        Glide.with(mContext).load(modelLogin!!.getResult()?.image)
            .placeholder(R.drawable.user_ic)
            .error(R.drawable.user_ic)
            .into(profileImageSetUpdate)
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
                profileImageSetUpdate.setImageURI(Uri.parse(path))
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
                        profileImageSetUpdate.setImageURI(Uri.parse(image))
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