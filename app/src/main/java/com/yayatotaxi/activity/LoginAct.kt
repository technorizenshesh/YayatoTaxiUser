package com.yayatotaxi.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.facebook.CallbackManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.yayatotaxi.R
import com.yayatotaxi.model.ModelLogin
import com.yayatotaxi.utils.*
import com.yayatotaxi.viewmodel.LoginViewModel
import kotlinx.android.synthetic.main.activity_login.*

class LoginAct : AppCompatActivity() {
    val TAG = "LoginAct";
    var mContext: Context = this@LoginAct
    private lateinit var registerId: String
    lateinit var sharedPref: SharedPref
    lateinit var modelLogin: ModelLogin
    private lateinit var mAuth: FirebaseAuth
    lateinit var mGoogleSignInClient: GoogleSignInClient
    var GOOGLE_SIGN_IN_REQUEST_CODE: Int = 1234
    private lateinit var callbackManager: CallbackManager
    var loginViewModel: LoginViewModel? = null


    lateinit var tracker: GPSTracker
    var currentLocation: LatLng? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)
        loginViewModel!!.init(mContext)

        initViews()

        loginViewModel!!.getLoginDataViewModel()!!.observe(this, {
            if (it != null) {
                modelLogin = it;
                Log.e("Login data===", Gson()!!.toJson(modelLogin))
                Toast.makeText(mContext,R.string.login_sucess,Toast.LENGTH_LONG)
                sharedPref.setBooleanValue(AppConstant.IS_REGISTER, true)
                sharedPref.setUserDetails(AppConstant.USER_DETAILS, modelLogin)
                startActivity(Intent(mContext, HomeAct::class.java))
                finish()

            } else {
                MyApplication.showAlert(mContext, getString(R.string.invalid_credentials))

            }
        })


    }

    private fun initViews() {
        sharedPref = SharedPref(mContext)
        FirebaseApp.initializeApp(mContext)
        callbackManager = CallbackManager.Factory.create()

        FirebaseMessaging.getInstance().token.addOnSuccessListener { token: String ->
            if (!TextUtils.isEmpty(token)) {
                registerId = token
                Log.e(TAG, "token = " + registerId)
            }
        }

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance()

        // Build a GoogleSignInClient with the options specified by gso.

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)


        btnSignin.setOnClickListener {
            if (TextUtils.isEmpty(etEmail.text.toString().trim())) {
                MyApplication.showAlert(mContext, getString(R.string.enter_email_text))
            } else if (TextUtils.isEmpty(etPassword.text.toString().trim())) {
                MyApplication.showAlert(mContext, getString(R.string.please_enter_pass))
            } else {
                if (InternetConnection.checkConnection(mContext)) {
                    loginViewModel!!.loginApiCallViewModel(
                        etEmail.text.toString().trim(),
                        etPassword.text.toString().trim(),
                        currentLocation?.latitude.toString(),
                        currentLocation?.latitude.toString(),
                        registerId
                    )
                } else {
                    MyApplication.showConnectionDialog(mContext)
                }
            }
        }


        ivForgotPass.setOnClickListener {
            startActivity(Intent(mContext, ForgotPassAct::class.java))
        }

        btSignup.setOnClickListener {
            startActivity(Intent(mContext, SignUpAct::class.java))
        }


    }


    override fun onResume() {
        super.onResume()
        tracker = GPSTracker(mContext)
        currentLocation = LatLng(tracker.latitude, tracker.longitude)
    }


}