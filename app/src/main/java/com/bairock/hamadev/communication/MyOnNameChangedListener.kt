package com.bairock.hamadev.communication

import com.bairock.hamadev.adapter.*
import com.bairock.hamadev.app.ClimateFragment
import com.bairock.hamadev.app.ElectricalCtrlFragment
import com.bairock.hamadev.app.HamaApp
import com.bairock.hamadev.database.Config
import com.bairock.hamadev.database.DeviceDao
import com.bairock.iot.intelDev.device.Device
import com.bairock.iot.intelDev.device.IStateDev
import com.bairock.iot.intelDev.device.devcollect.DevCollect
import com.bairock.iot.intelDev.user.MyHome

class MyOnNameChangedListener : MyHome.OnNameChangedListener {
    override fun onNameChanged(p0: MyHome, p1: String?) {
        if(p0 is Device){
            refreshUi(device = p0)
            updateDeviceDao(device = p0)
        }
    }

    private fun refreshUi(device: Device) {
        if (device is IStateDev) {
            if (Config.devShowStyle == "0") {
                if(null != ElectricalCtrlFragment.handler){
                    ElectricalCtrlFragment.handler.obtainMessage(ElectricalCtrlFragment.NOTIFY_ADAPTER, RecyclerAdapterElectrical3.NAME, RecyclerAdapterElectrical3.NAME, device)
                }
            } else {
                if (null != RecyclerAdapterElectricalList.handler) {
                    RecyclerAdapterElectricalList.handler!!.obtainMessage(RecyclerAdapterElectricalList.NAME, device).sendToTarget()
                }
            }
        }else if(device is DevCollect){
            if (null != ClimateFragment.handler) {
                ClimateFragment.handler.obtainMessage(ClimateFragment.NOTIFY_ADAPTER, RecyclerAdapterCollect.NAME, RecyclerAdapterCollect.NAME, device).sendToTarget()
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