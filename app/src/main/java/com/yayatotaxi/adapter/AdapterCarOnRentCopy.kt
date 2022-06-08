package com.yayatotaxi.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.yayatotaxi.R
import com.yayatotaxi.databinding.AdapterCarOnRentBinding
import com.yayatotaxi.listerner.CarOnRentCopyListener
import com.yayatotaxi.model.CarListModel
import com.yayatotaxi.model.ModelLogin
import com.yayatotaxi.utils.AppConstant
import com.yayatotaxi.utils.SharedPref

class AdapterCarOnRentCopy (    val mContext: Context/*,
    var transList: ArrayList<ModelTaxiRequest.Result>?*/
) : RecyclerView.Adapter<AdapterCarOnRentCopy.TransViewHolder>() {
    private lateinit var transList: ArrayList<CarListModel.Result>

    lateinit var sharedPref: SharedPref
    lateinit var modelLogin: ModelLogin
    lateinit var crOnRentListener: CarOnRentCopyListener


    fun setListener(crOnRentListener: CarOnRentCopyListener) {
        this.crOnRentListener = crOnRentListener
    }
    fun setList(transList: ArrayList<CarListModel.Result>) {
        this.transList = transList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransViewHolder {
        var binding: AdapterCarOnRentBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mContext), R.layout.adapter_car_on_rent, parent, false
        )
        sharedPref = SharedPref(mContext)
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS)
        return TransViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransViewHolder, position: Int) {

        var data: CarListModel.Result = transList!!.get(position)
        holder.binding.tvDateTime.text = data.type
        holder.binding.tvPickup.text = data.partner_name+"\n"+data.car_number

//        holder.binding.tvFrom.text = data.picuplocation
//        holder.binding.etDestination.text = data.dropofflocation

        holder.binding.noofseats.text = data.start_date + "  " + data.start_time+"\n"+data.end_date + "  " + data.end_time

        holder.binding.tvStatus.text=data.status


        holder.binding.btTrackDriver.setOnClickListener {
           // crOnRentListener.onClickCopy(data,"Pending",position)
        }
        holder.binding.btSend.setOnClickListener {
         //   crOnRentListener.onClickCopy(data,"Cancel",position)

        }


//        holder.binding.goDetail.setOnClickListener {
//            if ("Finish" == data.status || "Cancel_by_driver" == data.status || "Cancel_by_user" == data.status) {
//                mContext.startActivity(
//                    Intent(
//                        mContext,
//                        RideDetailsAct::class.java
//                    ).putExtra("id", data.id)
//                )
//            } else {
//                mContext.startActivity(
//                    Intent(mContext, TrackPoolAct::class.java).putExtra(
//                        "id",
//                        data.id
//                    ).putExtra("status", data.status)
//                )
//
//            }
//        }

    }

    override fun getItemCount(): Int {
        return if (transList == null) 0 else transList!!.size
    }

    class TransViewHolder(var binding: AdapterCarOnRentBinding) :
        RecyclerView.ViewHolder(binding.root) {}

}
