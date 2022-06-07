package com.yayatotaxi.listerner

import com.yayatotaxi.model.CarListModel

interface CarOnRentCopyListener {
    fun onClickCopy(poolDetails: CarListModel.Result, status: String, position: Int)

}