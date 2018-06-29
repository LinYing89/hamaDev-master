package com.bairock.hamadev.communication

import android.util.Log
import com.bairock.iot.intelDev.device.alarm.AlarmTrigger
import com.bairock.iot.intelDev.device.alarm.DevAlarm

class MyOnAlarmTriggedListener : DevAlarm.OnAlarmTriggedListener {
    override fun onAlarmTrigged(p0: AlarmTrigger) {
        Log.e("OnAlarm", p0.devAlarm.name + " " + p0.message)
    }
}