package com.bairock.hamadev.communication

import com.bairock.hamadev.adapter.RecyclerAdapterCollect
import com.bairock.hamadev.app.ClimateFragment
import com.bairock.iot.intelDev.device.devcollect.CollectProperty
import com.bairock.iot.intelDev.device.devcollect.DevCollect

object MyOnCurrentValueChangedListener : CollectProperty.OnCurrentValueChangedListener {
    override fun onCurrentValueChanged(p0: DevCollect?, p1: Float?) {
        if (null != ClimateFragment.handler) {
            ClimateFragment.handler.obtainMessage(ClimateFragment.NOTIFY_ADAPTER, RecyclerAdapterCollect.VALUE, RecyclerAdapterCollect.VALUE, p0).sendToTarget()
        }
    }
}