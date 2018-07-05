package com.bairock.hamadev.receiver

import android.content.Context
import android.util.Log
import com.bairock.hamadev.app.AlarmMessageHelper
import com.bairock.hamadev.app.HamaApp
import com.bairock.hamadev.media.Media
import com.tencent.tac.messaging.TACMessagingReceiver
import com.tencent.tac.messaging.TACMessagingText
import com.tencent.tac.messaging.TACMessagingToken
import com.tencent.tac.messaging.TACNotification
import com.tencent.tac.messaging.type.PushChannel

class MessageReceiver : TACMessagingReceiver() {

    override fun onNotificationArrived(p0: Context?, p1: TACNotification?, p2: PushChannel?) {
        //Toast.makeText(p0, "收到通知消息：$p2", Toast.LENGTH_LONG).show()
        Log.e("messaging", "MyReceiver::onNotificationArrived : notification is $p1 pushChannel $p2")
        Media.playAlarm()
    }

    override fun onBindTagResult(p0: Context?, p1: Int, p2: String?) {
        //Toast.makeText(p0, "绑定标签成功：tag = $p2", Toast.LENGTH_LONG).show()
        Log.e("messaging", "MyReceiver::onBindTagResult : code is $p1 tag $p2")
    }

    override fun onMessageArrived(p0: Context?, p1: TACMessagingText?, p2: PushChannel?) {
        val map = p1!!.customContent
        val name = map["name"]
        val triggerStyle = map["trigger"]!!.toInt()

        if(triggerStyle == 0 || triggerStyle == 1) {
            //报警中
            AlarmMessageHelper.add(name!!, p1.content)
        }else{
            //解除报警
            AlarmMessageHelper.remove(name!!)
        }
    }

    override fun onUnregisterResult(p0: Context?, p1: Int) {
    }

    override fun onRegisterResult(p0: Context?, p1: Int, p2: TACMessagingToken?) {
        Log.e("errorCode", p1.toString() + "?")
        if (p2 != null) {
            val token = p2.tokenString
            Log.e("receiver", token)
            HamaApp.tacMessagingToken = p2
            HamaApp.bindTokenTag()
        }
    }

    override fun onNotificationClicked(p0: Context?, p1: TACNotification?, p2: PushChannel?) {
    }

    override fun onNotificationDeleted(p0: Context?, p1: TACNotification?, p2: PushChannel?) {
    }

    override fun onUnbindTagResult(p0: Context?, p1: Int, p2: String?) {
    }
}