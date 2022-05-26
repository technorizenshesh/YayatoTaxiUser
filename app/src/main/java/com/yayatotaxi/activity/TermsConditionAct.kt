package com.yayatotaxi.activity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.yayatotaxi.R
import kotlinx.android.synthetic.main.activity_terms_condition.*

class TermsConditionAct : AppCompatActivity() {

    var mContext: Context = this@TermsConditionAct

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terms_condition)
        itit()
    }

    private fun itit() {
        ivBack.setOnClickListener { finish() }
    }

}