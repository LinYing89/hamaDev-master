package com.bairock.hamadev.video

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.bairock.hamadev.app.HamaApp
import com.videogo.constant.Constant

class EzvizBroadcastReceiver : BroadcastReceiver() {
    private val TAG = "EzvizBroadcastReceiver"

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action == Constant.OAUTH_SUCCESS_ACTION) {
            Log.e(TAG, "onReceive: OAUTH_SUCCESS_ACTION")
            val toIntent = Intent(context, VideoPlayActivity::class.java)
            toIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//            /*******   获取登录成功之后的EZAccessToken对象    */
            val token = HamaApp.getOpenSDK().ezAccessToken
            context.startActivity(toIntent)
        }
    }
}