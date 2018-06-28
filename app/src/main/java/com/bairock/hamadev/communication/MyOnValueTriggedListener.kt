package com.bairock.hamadev.communication

import android.app.Notification
import android.util.Log
import com.bairock.hamadev.R
import com.bairock.iot.intelDev.device.devcollect.CollectProperty
import com.bairock.iot.intelDev.device.devcollect.ValueTrigger
import android.app.NotificationManager
import android.content.Context
import android.support.v4.app.NotificationCompat
import com.bairock.hamadev.app.HamaApp


class MyOnValueTriggedListener : CollectProperty.OnValueTriggedListener{

    override fun onValueTrigged(p0: ValueTrigger, p1: Float) {
        Log.e("ValueTriggedListener", p0.message + " triggered value = " + p1)
        //如果服务器已连接，本地不提醒，只允许服务器推送提醒，如果服务器未连接，本地推送提醒
        if(!PadClient.getIns().isLinked) {
            val manager = HamaApp.HAMA_CONTEXT.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val notification = NotificationCompat.Builder(HamaApp.HAMA_CONTEXT)
                    .setContentText(p0.message)
                    .setContentTitle(p0.collectProperty.devCollect.name + "提醒")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setAutoCancel(true)//点击通知头自动取消
                    .setDefaults(Notification.DEFAULT_ALL)
                    .build()
            manager.notify(1, notification)
        }
    }
}