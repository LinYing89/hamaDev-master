package com.bairock.hamadev.communication

import com.bairock.hamadev.remote.StudyKeyActivity
import com.bairock.iot.intelDev.device.remoter.RemoterContainer
import com.bairock.iot.intelDev.device.remoter.RemoterKey

class MyOnRemoterOrderSuccessListener : RemoterContainer.OnRemoterOrderSuccessListener {
    override fun onRemoterOrderSuccess(p0: RemoterKey?) {
        if(null != StudyKeyActivity.remoterKey){
            if(null != StudyKeyActivity.handler) {
                StudyKeyActivity.handler!!.obtainMessage().sendToTarget()
            }
        }
    }
}