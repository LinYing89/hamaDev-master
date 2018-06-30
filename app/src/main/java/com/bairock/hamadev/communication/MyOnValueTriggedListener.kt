package com.bairock.hamadev.communication

import android.util.Log
import com.bairock.iot.intelDev.device.devcollect.CollectProperty
import com.bairock.iot.intelDev.device.devcollect.ValueTrigger
import com.bairock.hamadev.app.HamaApp
import com.tencent.tac.messaging.TACMessagingLocalMessage
import com.tencent.tac.messaging.TACMessagingService
import com.tencent.tac.messaging.type.MessageType
import com.tencent.tac.messaging.type.NotificationActionType

class MyOnValueTriggedListener : CollectProperty.OnValueTriggedListener{

    override fun onValueTrigged(p0: ValueTrigger, p1: Float) {
        Log.e("ValueTriggedListener", p0.message + " triggered value = " + p1)
        //如果服务器已连接，本地不提醒，只允许服务器推送提醒，如果服务器未连接，本地推送提醒
        if(!PadClient.getIns().isLinked) {
            pushLocal("提醒", p0.collectProperty.devCollect.name + ":" + p0.message+ "(当前值:" + p1 + ")")
        }
    }

    private fun pushLocal(title : String, content : String){
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
}