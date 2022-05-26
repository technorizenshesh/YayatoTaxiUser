package com.yayatotaxi.activity

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.yayatotaxi.R
import com.yayatotaxi.model.ModelLogin
import com.yayatotaxi.utils.InternetConnection
import com.yayatotaxi.utils.MyApplication
import com.yayatotaxi.utils.ProjectUtil
import com.yayatotaxi.viewmodel.ForgotPassViewModel
import com.yayatotaxi.viewmodel.LoginViewModel
import kotlinx.android.synthetic.main.activity_forgot_pass.*
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForgotPassAct : AppCompatActivity() {

    var mContext: Context = this@ForgotPassAct
    var forgotPassViewModel : ForgotPassViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_pass)
        forgotPassViewModel = ViewModelProviders.of(this).get(ForgotPassViewModel::class.java)
        forgotPassViewModel!!.init(mContext)
        initViews()

        forgotPassViewModel!!.getForgotPassDataViewModel()!!.observe(this,
            Observer<ResponseBody?> { response ->
                if (response != null) {
                    var stringResponse: String = response.string()
                    var jsonObject = JSONObject(stringResponse)
                    if (jsonObject.getString("status") == "1") {
                        finish()
                        Toast.makeText(mContext, getString(R.string.reset_pass_msg), Toast.LENGTH_LONG).show()
                    } else  {
                        Toast.makeText(mContext, getString(R.string.email_not_found), Toast.LENGTH_LONG).show()
                    }

                }
            })


    }

    private fun initViews() {
        btSubmit.setOnClickListener {
            if (TextUtils.isEmpty(etEmail.text.toString().trim())) {
                Toast.makeText(mContext, getString(R.string.enter_email_text), Toast.LENGTH_SHORT).show()
            } else {
                if (InternetConnection.checkConnection(mContext)) forgotPassViewModel!!.forgotPassApiCallViewModel(etEmail.text.toString().trim())
                else MyApplication.showConnectionDialog(mContext)
            }
        }

        ivBack.setOnClickListener {
            finish()
        }

    }


}