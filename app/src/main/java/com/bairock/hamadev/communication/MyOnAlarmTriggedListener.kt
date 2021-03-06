package com.bairock.hamadev.communication

import android.app.Notification
import android.content.Intent
import android.util.Log
import com.bairock.hamadev.app.HamaApp
import com.bairock.iot.intelDev.device.alarm.AlarmTrigger
import com.bairock.iot.intelDev.device.alarm.DevAlarm
import com.tencent.tac.messaging.TACMessagingService
import com.tencent.tac.messaging.type.NotificationActionType
import com.tencent.tac.messaging.TACMessagingLocalMessage
import com.tencent.tac.messaging.type.MessageType
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.support.v4.app.NotificationCompat
import com.bairock.hamadev.R
import com.bairock.hamadev.app.AlarmMessageHelper
import com.bairock.hamadev.app.MainActivity
import com.bairock.iot.intelDev.device.CtrlModel

object MyOnAlarmTriggedListener : DevAlarm.OnAlarmTriggedListener {
    override fun onAlarmTrigged(p0: AlarmTrigger) {
        Log.e("OnAlarm", p0.devAlarm.name + " " + p0.message)
        //如果设备是本地模式，则本地推送消息，否则服务器推送提醒
        if(p0.devAlarm.ctrlModel == CtrlModel.LOCAL){
            val content = p0.devAlarm.name + " " + p0.message
            //TTSHelper.speech(content)
            pushLocal("报警", content)
            AlarmMessageHelper.add(p0.devAlarm.name, content)
        }
    }

    override fun onAlarmTriggedRelieve(p0: AlarmTrigger) {
        AlarmMessageHelper.remove(p0.devAlarm.name)
    }

    override fun onAlarmTrigging(p0: AlarmTrigger?) {

    }

    private fun pushLocal(title : String, content : String){
        //Media.playAlarm()
        val localMsg = TACMessagingLocalMessage()
        localMsg.setType(MessageType.NOTIFICATION)
        localMsg.title = title
        localMsg.content = content
//        val intent = Intent()
//        intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND)
//        intent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
        localMsg.setActionType(NotificationActionType.ACTION_OPEN_APPLICATION)
        // 是否覆盖原先build_id的保存设置。1覆盖，0不覆盖
        localMsg.styleId = 1

        //添加通知到本地
        val messagingService = TACMessagingService.getInstance()
        messagingService.addLocalNotification(HamaApp.HAMA_CONTEXT, localMsg)
    }

    private fun myPushLocal(title : String, content : String){
        val mainIntent = Intent(HamaApp.HAMA_CONTEXT, MainActivity::class.java)
        //mainIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        val mainPendingIntent = PendingIntent.getActivity(HamaApp.HAMA_CONTEXT, 0, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val manager = HamaApp.HAMA_CONTEXT.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(HamaApp.HAMA_CONTEXT)
                .setContentText(content)
                .setContentTitle(title)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(mainPendingIntent)
                .setAutoCancel(true)//点击通知头自动取消
                .setDefaults(Notification.DEFAULT_ALL)
                .build()
        manager.notify(1, notification)
    }
}