package com.yayatotaxi.activity

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.gson.Gson
import com.yayatotaxi.R
import com.yayatotaxi.model.ModelLogin
import com.yayatotaxi.utils.*
import com.yayatotaxi.viewmodel.LoginViewModel
import com.yayatotaxi.viewmodel.SignupViewModel
import kotlinx.android.synthetic.main.activity_verify.*
import java.io.File
import java.util.HashMap
import java.util.concurrent.TimeUnit

class VerifyAct : AppCompatActivity(),MySMSBroadcastReceiver.OTPReceiveListener {

    private var smsReceiver: MySMSBroadcastReceiver? = null

    var mContext: Context = this@VerifyAct
    var type = ""
    var mobile = ""
    var id: String? = null
    lateinit var sharedPref: SharedPref
    lateinit var modelLogin: ModelLogin
    private var mAuth: FirebaseAuth? = null
    var fileHashMap = HashMap<String, File>()
    var paramHash = HashMap<String, String>()
    var signupViewModel : SignupViewModel? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify)
        signupViewModel = ViewModelProviders.of(this).get(SignupViewModel::class.java)
        signupViewModel!!.init(mContext)
        initViews()



        signupViewModel!!.getSignupDataViewModel()!!.observe(this,{
            if (it != null) {
                modelLogin =  it;
                Toast.makeText(mContext,R.string.signup_sucess,Toast.LENGTH_LONG)
                sharedPref.setBooleanValue(AppConstant.IS_REGISTER, true)
                sharedPref.setUserDetails(AppConstant.USER_DETAILS, modelLogin)
                startActivity(Intent(mContext, HomeAct::class.java))
                finish()

            } else {
                MyApplication.showAlert(mContext, modelLogin.getMessage()!!)

            }
            })

    }

    private fun initViews() {
        sharedPref = SharedPref(mContext)
        mAuth = FirebaseAuth.getInstance()

        mobile = intent.getStringExtra("mobile")!!
        paramHash = intent.getSerializableExtra("resgisterHashmap") as HashMap<String, String>
        fileHashMap = intent.getSerializableExtra("fileHashMap") as HashMap<String, File>

       /* if (InternetConnection.checkConnection(mContext)) {
            sendVerificationCode()
        } else {
            MyApplication.showConnectionDialog(mContext)
        }*/


       // startSMSListener()


        ivBack.setOnClickListener { finish() }

        et1.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                et2.requestFocus()
            }

            override fun afterTextChanged(s: Editable) {}
        })

        et2.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                et3.requestFocus()
            }

            override fun afterTextChanged(s: Editable) {}
        })

        et3.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                et4.requestFocus()
            }

            override fun afterTextChanged(s: Editable) {}
        })

        et4.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                et5.requestFocus()
            }

            override fun afterTextChanged(s: Editable) {}
        })

        et5.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                et6.requestFocus()
            }

            override fun afterTextChanged(s: Editable) {}
        })

        btn_verify.setOnClickListener {
          validation()
        }

    }

    private fun validation() {
        if (TextUtils.isEmpty(et1.text.toString().trim())) {
            Toast.makeText(mContext, getString(R.string.invalid_otp), Toast.LENGTH_SHORT).show()
        } else if (TextUtils.isEmpty(et2.text.toString().trim())) {
            Toast.makeText(mContext, getString(R.string.invalid_otp), Toast.LENGTH_SHORT).show()
        } else if (TextUtils.isEmpty(et3.text.toString().trim())) {
            Toast.makeText(mContext, getString(R.string.invalid_otp), Toast.LENGTH_SHORT).show()
        } else if (TextUtils.isEmpty(et4.text.toString().trim())) {
            Toast.makeText(mContext, getString(R.string.invalid_otp), Toast.LENGTH_SHORT).show()
        } else if (TextUtils.isEmpty(et5.text.toString().trim())) {
            Toast.makeText(mContext, getString(R.string.invalid_otp), Toast.LENGTH_SHORT).show()
        } else if (TextUtils.isEmpty(et6.text.toString().trim())) {
            Toast.makeText(mContext, getString(R.string.invalid_otp), Toast.LENGTH_SHORT).show()
        } else {
            val otpFull: String = et1.text.toString().trim() +
                    et2.text.toString().trim() +
                    et3.text.toString().trim() +
                    et4.text.toString().trim() +
                    et5.text.toString().trim() +
                    et6.text.toString().trim()
          //  ProjectUtil.showProgressDialog(mContext, true, getString(R.string.please_wait))
         //   val credential = PhoneAuthProvider.getCredential(id!!, otpFull)
          //  signInWithPhoneAuthCredential(credential)

            if(otpFull.equals("000000")){
                if (InternetConnection.checkConnection(mContext)) signupViewModel!!.signupApiCallViewModel(paramHash["first_name"]!!,paramHash["last_name"]!!
                    ,paramHash["email"]!!,paramHash["mobile"]!!,paramHash["address"]!!,paramHash["register_id"]!!,paramHash["lat"]!!
                    , paramHash["lon"]!!,paramHash["password"]!!,paramHash["type"]!!,"1",fileHashMap["image"]!!)
                else MyApplication.showConnectionDialog(mContext)
            }
            else  Toast.makeText(mContext, getString(R.string.invalid_otp), Toast.LENGTH_SHORT).show()


        }
    }





    override fun onOTPReceived(otp: String) {
        showToast("OTP Received: " + otp)
//        editText.setText(otp)
        if (smsReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(smsReceiver!!)
        }
    }

    override fun onOTPTimeOut() {
        showToast("OTP Time out")
    }


    override fun onDestroy() {
        super.onDestroy()
        if (smsReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(smsReceiver!!)
        }
    }
    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
    /**
     * Starts SmsRetriever, which waits for ONE matching SMS message until timeout
     * (5 minutes). The matching SMS message will be sent via a Broadcast Intent with
     * action SmsRetriever#SMS_RETRIEVED_ACTION.
     */
    private fun startSMSListener() {
        try {
            smsReceiver = MySMSBroadcastReceiver()
            smsReceiver!!.initOTPListener(this)

            val intentFilter = IntentFilter()
            intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION)
            this.registerReceiver(smsReceiver, intentFilter)

            val client = SmsRetriever.getClient(this)

            val task = client.startSmsRetriever()
            task.addOnSuccessListener {
                // API successfully started
                Log.d("task", "addOnSuccessListener")
            }

            task.addOnFailureListener {
                // Fail to start API
                Log.d("task", "addOnFailureListener")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    private fun sendVerificationCode() {
        tvVerifyText.text = "We have send you an SMS on $mobile with 6 digit verification code."
        object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                tvResend.text = "" + millisUntilFinished / 1000
                tvResend.isEnabled = false
            }

            override fun onFinish() {
                tvResend.text = mContext.getString(R.string.resend)
                tvResend.isEnabled = true
            }
        }.start()
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            mobile.replace(" ", ""),  // Phone number to verify
            60,  // Timeout duration
            TimeUnit.SECONDS,  // Unit of timeout
            this,  // Activity (for callback binding)
            object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                override fun onCodeSent(id: String, forceResendingToken: PhoneAuthProvider.ForceResendingToken) {
                    this@VerifyAct.id = id
                    Toast.makeText(mContext, getString(R.string.enter_6_digit_code), Toast.LENGTH_SHORT).show()
                }

                override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                    ProjectUtil.pauseProgressDialog()
                    signInWithPhoneAuthCredential(phoneAuthCredential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    ProjectUtil.pauseProgressDialog()
                    Toast.makeText(mContext, "Failed", Toast.LENGTH_SHORT).show()
                    e.printStackTrace();
                }

            })

    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        mAuth!!.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    ProjectUtil.pauseProgressDialog()
                    // Toast.makeText(mContext, "success", Toast.LENGTH_SHORT).show();
                    // Sign in success, update UI with the signed-in user's information
                    val user = task.result.user
                    if (InternetConnection.checkConnection(mContext)) signupViewModel!!.signupApiCallViewModel(paramHash["first_name"]!!,paramHash["last_name"]!!
                        ,paramHash["email"]!!,paramHash["mobile"]!!,paramHash["address"]!!,paramHash["register_id"]!!,paramHash["lat"]!!
                        , paramHash["lon"]!!,paramHash["password"]!!,paramHash["type"]!!,"1",fileHashMap["image"]!!)
                    else MyApplication.showConnectionDialog(mContext)
                } else {
                    ProjectUtil.pauseProgressDialog()
                    Toast.makeText(mContext, "Failed", Toast.LENGTH_SHORT).show()
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        task.exception
                    }
                }
            }
    }



}