package com.bairock.hamadev.communication

import com.bairock.hamadev.adapter.RecyclerAdapterElectrical3
import com.bairock.hamadev.adapter.RecyclerAdapterElectricalList
import com.bairock.iot.intelDev.device.Device

class MyOnGearNeedToAutoListener : Device.OnGearNeedToAutoListener {
    override fun onGearNeedToAuto(p0: Device?, p1: Boolean) {
        if(null != RecyclerAdapterElectrical3.handler){
            RecyclerAdapterElectrical3.handler.obtainMessage(RecyclerAdapterElectrical3.GEAR_NEED_TO_AUTO, p0).sendToTarget()
        }
        RecyclerAdapterElectricalList.handler!!.obtainMessage(RecyclerAdapterElectricalList.GEAR_NEED_TO_AUTO, p0).sendToTarget()
    }
}