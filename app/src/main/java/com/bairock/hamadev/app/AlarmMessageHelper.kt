package com.bairock.hamadev.app

import com.bairock.hamadev.database.AlarmMessage
import java.sql.Time
import android.content.Intent
import com.bairock.hamadev.video.VideoPlayActivity


object AlarmMessageHelper {

    private var listAlarmMessage = mutableListOf<AlarmMessage>()
    var listMessage = mutableListOf<String>()

    fun add(name : String, content : String){
        var haved = false
        for (alarmMsg in listAlarmMessage) {
            if (alarmMsg.name == name) {
                haved = true
                break
            }
        }
        if (!haved) {
            val alarmMsg = AlarmMessage()
            alarmMsg.name = name
            alarmMsg.message = content
            alarmMsg.time = ""
            listAlarmMessage.add(alarmMsg)
            listMessage.add(content)
            if(listMessage.size <= 2){
                val broadcast = Intent(MainActivity.UPDATE_ALARM_TEXT_ACTION)
                HamaApp.HAMA_CONTEXT.sendBroadcast(broadcast)
            }
        }
    }

    fun remove(name : String){
        for (alarmMsg in listAlarmMessage) {
            if (alarmMsg.name == name) {
                listAlarmMessage.remove(alarmMsg)
                listMessage.remove(alarmMsg.message)
                if(listMessage.isEmpty()){
                    val broadcast = Intent(MainActivity.UPDATE_ALARM_TEXT_ACTION)
                    HamaApp.HAMA_CONTEXT.sendBroadcast(broadcast)
                }
                break
            }
        }
    }
}