package com.bairock.hamadev.receiver

import android.content.Context
import android.util.Log
import com.tencent.tac.messaging.TACMessagingReceiver
import com.tencent.tac.messaging.TACMessagingText
import com.tencent.tac.messaging.TACMessagingToken
import com.tencent.tac.messaging.TACNotification
import com.tencent.tac.messaging.type.PushChannel

class MessageReceiver : TACMessagingReceiver() {

    override fun onNotificationArrived(p0: Context?, p1: TACNotification?, p2: PushChannel?) {

    }

    override fun onBindTagResult(p0: Context?, p1: Int, p2: String?) {
    }

    override fun onMessageArrived(p0: Context?, p1: TACMessagingText?, p2: PushChannel?) {
    }

    override fun onUnregisterResult(p0: Context?, p1: Int) {
    }

    override fun onRegisterResult(p0: Context?, p1: Int, p2: TACMessagingToken?) {
        if (p2 != null) {
            val token = p2.tokenString
            Log.e("receiver", token)
        }
        Log.e("errorCode", p1.toString() + "?")
    }

    override fun onNotificationClicked(p0: Context?, p1: TACNotification?, p2: PushChannel?) {
    }

    override fun onNotificationDeleted(p0: Context?, p1: TACNotification?, p2: PushChannel?) {
    }

    override fun onUnbindTagResult(p0: Context?, p1: Int, p2: String?) {
    }
}