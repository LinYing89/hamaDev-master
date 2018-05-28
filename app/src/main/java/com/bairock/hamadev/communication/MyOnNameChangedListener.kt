package com.bairock.hamadev.communication

import com.bairock.hamadev.adapter.*
import com.bairock.hamadev.app.HamaApp
import com.bairock.hamadev.database.Config
import com.bairock.hamadev.database.DeviceDao
import com.bairock.iot.intelDev.device.Device
import com.bairock.iot.intelDev.device.IStateDev
import com.bairock.iot.intelDev.device.devcollect.DevCollect

class MyOnNameChangedListener : Device.OnNameChangedListener {
    override fun onNameChanged(p0: Device, p1: String?) {
        refreshUi(p0)
        updateDeviceDao(p0)
    }

    private fun refreshUi(device: Device) {
        if (device is IStateDev) {
            if (Config.devShowStyle == "0") {
                if (null != RecyclerAdapterElectrical3.handler) {
                    RecyclerAdapterElectrical3.handler.obtainMessage(RecyclerAdapterElectrical3.NAME, device).sendToTarget()
                }
            } else {
                if (null != RecyclerAdapterElectricalList.handler) {
                    RecyclerAdapterElectricalList.handler!!.obtainMessage(RecyclerAdapterElectricalList.NAME, device).sendToTarget()
                }
            }
        }else if(device is DevCollect){
            if (null != RecyclerAdapterCollect.handler) {
                RecyclerAdapterCollect.handler.obtainMessage(RecyclerAdapterCollect.NAME, device).sendToTarget()
            }
        }
        if(null != RecyclerAdapterDevice.handler){
            RecyclerAdapterDevice.handler.obtainMessage(RecyclerAdapterDevice.NAME, device).sendToTarget()
        }

        if(null != RecyclerAdapterChildDevice.handler){
            RecyclerAdapterChildDevice.handler.obtainMessage(RecyclerAdapterChildDevice.NAME, device).sendToTarget()
        }
    }

    private fun updateDeviceDao(device: Device) {
        val deviceDao = DeviceDao.get(HamaApp.HAMA_CONTEXT)
        deviceDao.update(device)
    }
}