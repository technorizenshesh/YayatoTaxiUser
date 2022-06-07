package com.yayatotaxi.activity

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.DatePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.yayatotaxi.R
import com.yayatotaxi.adapter.AdapterCarOnRentCopy
import com.yayatotaxi.databinding.ActivitySetDateTimeBinding
import com.yayatotaxi.listerner.CarOnRentCopyListener
import com.yayatotaxi.model.CarListModel
import com.yayatotaxi.model.ModelLogin
import com.yayatotaxi.model.ModelVehicalList
import com.yayatotaxi.retrofit.ApiClient
import com.yayatotaxi.retrofit.YayatoApiService
import com.yayatotaxi.utils.AppConstant
import com.yayatotaxi.utils.MyApplication
import com.yayatotaxi.utils.ProjectUtil
import com.yayatotaxi.utils.SharedPref
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class SetDateTimeAct : AppCompatActivity(), CarOnRentCopyListener {
    lateinit var binding: ActivitySetDateTimeBinding
    var mContext: Context = this@SetDateTimeAct
    private lateinit var adapterCarOnRent: AdapterCarOnRentCopy
    private lateinit var transList: ArrayList<ModelVehicalList.Result>

    lateinit var sharedPref: SharedPref
    lateinit var modelLogin: ModelLogin

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_set_date_time)



        sharedPref = SharedPref(mContext)
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS)




        adapterCarOnRent = AdapterCarOnRentCopy(mContext)
        adapterCarOnRent.setList(ArrayList())
        adapterCarOnRent.setListener(this)
        binding.recyclerview.adapter = adapterCarOnRent



        binding.etStartDate.setOnClickListener {
            var cal = Calendar.getInstance()
            // create an OnDateSetListener
            val dateSetListener = object : DatePickerDialog.OnDateSetListener {
                override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int,
                                       dayOfMonth: Int) {
                    cal.set(Calendar.YEAR, year)
                    cal.set(Calendar.MONTH, monthOfYear)
                    cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    val myFormat = "yyyy-MM-dd" // mention the format you need
                    val sdf = SimpleDateFormat(myFormat, Locale.US)
//                    datecurr=sdf.format(cal.getTime())
                    binding.etStartDate!!.setText(sdf.format(cal.getTime()))

                }
            }
            DatePickerDialog(this,
                dateSetListener,
                // set DatePickerDialog to point to today's date when it loads up
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).show()

        }
        binding.etEndDate.setOnClickListener {
            var cal = Calendar.getInstance()
            // create an OnDateSetListener
            val dateSetListener = object : DatePickerDialog.OnDateSetListener {
                override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int,
                                       dayOfMonth: Int) {
                    cal.set(Calendar.YEAR, year)
                    cal.set(Calendar.MONTH, monthOfYear)
                    cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    val myFormat = "yyyy-MM-dd" // mention the format you need
                    val sdf = SimpleDateFormat(myFormat, Locale.US)
//                    datecurr=sdf.format(cal.getTime())
                    binding.etEndDate!!.setText(sdf.format(cal.getTime()) )

                }
            }
            DatePickerDialog(this,
                dateSetListener,
                // set DatePickerDialog to point to today's date when it loads up
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).show()

        }

        binding.etStartTime.setOnClickListener {
            val mTimePicker: TimePickerDialog
            val mcurrentTime = Calendar.getInstance()
            val hour = mcurrentTime.get(Calendar.HOUR_OF_DAY)
            val minute = mcurrentTime.get(Calendar.MINUTE)

            mTimePicker = TimePickerDialog(
                this,
                { view, hourOfDay, minute ->
                    mcurrentTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    mcurrentTime.set(Calendar.MINUTE, minute);
                    var simpleDateFormat = SimpleDateFormat("hh:mm a")
                    var finaldate = simpleDateFormat.format(mcurrentTime.time)
                    binding.etStartTime.setText(finaldate)
                }, hour, minute, false
            )
            mTimePicker.show()
        }

        binding.etEndTime.setOnClickListener {
            val mTimePicker: TimePickerDialog
            val mcurrentTime = Calendar.getInstance()
            val hour = mcurrentTime.get(Calendar.HOUR_OF_DAY)
            val minute = mcurrentTime.get(Calendar.MINUTE)

            mTimePicker = TimePickerDialog(this, { view, hourOfDay, minute ->
                mcurrentTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                mcurrentTime.set(Calendar.MINUTE, minute);
                var simpleDateFormat = SimpleDateFormat("hh:mm a")
                var finaldate = simpleDateFormat.format(mcurrentTime.time)
                binding.etEndTime.setText(finaldate)
            }, hour, minute, false
            )
            mTimePicker.show()
        }

        binding.ivBack.setOnClickListener { finish() }


        binding.btnNext.setOnClickListener {
            if (TextUtils.isEmpty(binding.etStartDate.text.toString().trim())) {
                MyApplication.showAlert(mContext, "please select start date")
            } else if (TextUtils.isEmpty(binding.etEndDate.text.toString().trim())) {
                MyApplication.showAlert(mContext, "please select start date")
            } else if (TextUtils.isEmpty(binding.etStartTime.text.toString().trim())) {
                MyApplication.showAlert(mContext, "please select start time")
            } else if (TextUtils.isEmpty(binding.etEndTime.text.toString().trim())) {
                MyApplication.showAlert(mContext, "please select end time")
            } else {
//                val intent = Intent(mContext, SetDateTimeActivity::class.java)
//                    .putExtra("sourceAddress", intent.getStringExtra("sourceAddress"))
//                    .putExtra("sourceAddressLat",  intent.getStringExtra("sourceAddressLat"))
//                    .putExtra("sourceAddressLon",  intent.getStringExtra("sourceAddressLon"))
//                    .putExtra("destinationAddress",  intent.getStringExtra("destinationAddress"))
//                    .putExtra("destinationAddressLat",  intent.getStringExtra("destinationAddressLat"))
//                    .putExtra("destinationAddressLon",  intent.getStringExtra("destinationAddressLon"))
//                startActivity(intent)

                getBookingHistoryPoolApi()

            }
        }
    }



    override fun onResume() {
        super.onResume()
    }

    private fun getBookingHistoryPoolApi() {
        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait))
        val api: YayatoApiService = ApiClient.getClient(mContext)!!.create(YayatoApiService::class.java)
        val call: Call<ResponseBody> = api.get_car_detial(
            binding.etStartDate.text.toString(),  binding.etEndDate.text.toString(),
            intent.getStringExtra("sourceAddressLat").toString(),
            intent.getStringExtra("sourceAddressLon").toString()
        )
        Log.e("date===",binding.etStartDate.text.toString())
        Log.e("iddd===",modelLogin.getResult()?.id!!)

        Log.e("lat===",intent.getStringExtra("sourceAddressLat").toString())
        Log.e("lon===",intent.getStringExtra("sourceAddressLon").toString())


        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                ProjectUtil.pauseProgressDialog()
                try {
                    val responseString = response.body()!!.string()
                    val jsonObject = JSONObject(responseString)
                    Log.e("CurrentTaxiBooking", "responseString = $responseString")
                    if (jsonObject.getString("status") == "1") {
                        val modelCarsRent: CarListModel =
                            Gson().fromJson(responseString, CarListModel::class.java)
                        adapterCarOnRent.setList(modelCarsRent.getResult()!!)
                        adapterCarOnRent.notifyDataSetChanged()

                    }
                    else{
                        Toast.makeText(mContext,"No Car available for rent...",Toast.LENGTH_LONG).show()

                    }
                } catch (e: Exception) {
                    Toast.makeText(mContext, "Exception = " + e.message, Toast.LENGTH_SHORT).show()
                    Log.e("Exception", "Exception = " + e.message)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                ProjectUtil.pauseProgressDialog()
                Log.e("Exception", "Throwable = " + t.message)
            }

        })
    }


/*

    private fun get_car_on_rentApi(id: String, status: String,driverId: String) {

        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait))
        val api: YayatoApiService = ApiClient.getClient(mContext)!!.create(YayatoApiService::class.java)
        val call: Call<ResponseBody> = api.add_car_request(
            /* status,*/id,
            modelLogin.getResult()?.id!!,/*intent.getStringExtra("sourceAddressLat").toString(),
            intent.getStringExtra("sourceAddressLon").toString()*/
        )
        Log.e("status===",status)
        Log.e("iddddddddd===",id)
        Log.e("driver_id===",driverId)


        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                ProjectUtil.pauseProgressDialog()
                try {
                    val responseString = response.body()!!.string()
                    val jsonObject = JSONObject(responseString)
                    Log.e("CurrentTaxiBooking", "responseString = $responseString")
                    if (jsonObject.getString("status") == "1") {
//                        val modelVehicalList: ModelVehicalList =
//                            Gson().fromJson(responseString, ModelVehicalList::class.java)
//                        adapterCarOnRent.setList(modelVehicalList.getResult()!!)
//                        adapterCarOnRent.notifyDataSetChanged()
                        Toast.makeText(mContext, "Request has benn sent", Toast.LENGTH_SHORT).show()
                        finish()
                        //getBookingHistoryPoolApi()

                    }
                } catch (e: Exception) {
                    Toast.makeText(mContext, "Exception = " + e.message, Toast.LENGTH_SHORT).show()
                    Log.e("Exception", "Exception = " + e.message)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                ProjectUtil.pauseProgressDialog()
                Log.e("Exception", "Throwable = " + t.message)
            }

        })
    }

    private fun update_car_request_statusApi(id:String,status:String) {
        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait))
        val api: YayatoApiService = ApiClient.getClient(mContext)!!.create(YayatoApiService::class.java)
        val call: Call<ResponseBody> = api.update_car_request_status(
            id,
            status
        )

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                ProjectUtil.pauseProgressDialog()
                try {
                    val responseString = response.body()!!.string()
                    val jsonObject = JSONObject(responseString)
                    Log.e("CurrentTaxiBooking", "responseString = $responseString")
                    if (jsonObject.getString("status") == "1") {
//                        val modelVehicalList: ModelVehicalList =
//                            Gson().fromJson(responseString, ModelVehicalList::class.java)
//                        val adapterAllVehicle =
//                            AdapterAllVehicle(mContext, modelVehicalList.getResult())
//                        binding.chlidDashboard.recyclerView.adapter = adapterAllVehicle
                        getBookingHistoryPoolApi();

                    }
                } catch (e: Exception) {
                    Toast.makeText(mContext, "Exception = " + e.message, Toast.LENGTH_SHORT).show()
                    Log.e("Exception", "Exception = " + e.message)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                ProjectUtil.pauseProgressDialog()
                Log.e("Exception", "Throwable = " + t.message)
            }

        })
    }
*/

    override fun onClickCopy(poolDetails: CarListModel.Result, status: String, position: Int) {
        if(status.equals("Cancel")){
           // update_car_request_statusApi(poolDetails?.id!!,status)

        }else{
          //  get_car_on_rentApi(poolDetails.id.toString(),status,"")
        }    }
}