package com.yayatotaxi.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.yayatotaxi.R
import com.yayatotaxi.databinding.ItemRideBookBinding
import com.yayatotaxi.listerner.CarListener
import com.yayatotaxi.model.ModelCarsType
import com.yayatotaxi.utils.SharedPref

class AdapterRideOption(
    val mContext: Context,
    var transList: ArrayList<ModelCarsType.Result>?, val listener: CarListener
) : RecyclerView.Adapter<AdapterRideOption.TransViewHolder>() {

    lateinit var sharedPref: SharedPref
//    lateinit var modelLogin: ModelLogin


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransViewHolder {
        var binding: ItemRideBookBinding = DataBindingUtil.inflate (
            LayoutInflater.from(mContext), R.layout.item_ride_book, parent, false
        )
        sharedPref = SharedPref(mContext)
//        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS)
        return TransViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransViewHolder, position: Int) {

        var data: ModelCarsType.Result = transList!!.get(position)
        holder.binding.tvCarName.text = data.car_name
        holder.binding.tvTotal.text = data.total+"$"

        holder.binding.time.text=data.perMin+"min"

        if(data.isSelected!!){
            holder.binding.layoutbg.setBackgroundColor(mContext.getColor(R.color.slectedcolor))
        }else{
            holder.binding.layoutbg.setBackgroundColor(mContext.getColor(R.color.white))
        }


        Glide.with(mContext).load(data.car_image)
            .error(R.drawable.car)
            .placeholder(R.drawable.car)
            .into(holder.binding.ivCar)

        holder.binding.cardcar.setOnClickListener {
            listener.onClick(position,data)

            for (i in 0 until transList!!.size){
                transList?.get(i)?.isSelected=false
            }
            transList?.get(position)?.isSelected=true
            notifyDataSetChanged()

        }



//        if ("Credit" == data.transaction_type) {
//            holder.binding.tvDebitCredit.text = "Credit"
//            holder.binding.ivSendRecive.setImageResource(R.drawable.credit_icon)
//            holder.binding.tvDebitCredit.setTextColor(ContextCompat.getColor(mContext, R.color.green))
//        } else {
//            holder.binding.ivSendRecive.setImageResource(R.drawable.debit_icon)
//            holder.binding.tvDebitCredit.text = "Debit"
//            holder.binding.tvDebitCredit.setTextColor(ContextCompat.getColor(mContext, R.color.red))
//        }

    }

    override fun getItemCount(): Int {
        return if (transList == null) 0 else transList!!.size
    }

    class TransViewHolder(var binding: ItemRideBookBinding) :
        RecyclerView.ViewHolder(binding.root) {}

}