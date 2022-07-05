package com.yayatotaxi.listerner

import com.yayatotaxi.model.ModelCarsType


interface CarListener {
    fun onClick(position:Int,model: ModelCarsType.Result)
}