package com.bairock.hamadev.database

import android.database.Cursor
import android.database.CursorWrapper
import com.bairock.iot.intelDev.device.alarm.AlarmTrigger

class AlarmTriggerWrapper(cursor: Cursor) : CursorWrapper(cursor) {

    fun getAlarmTrigger(): AlarmTrigger {
        val id = getString(getColumnIndex(DbSb.TabAlarmTrigger.Cols.ID))
        val enable = getString(getColumnIndex(DbSb.TabAlarmTrigger.Cols.ENABLE)) == "1";
        val message = getString(getColumnIndex(DbSb.TabAlarmTrigger.Cols.MESSAGE))

        val valueTrigger = AlarmTrigger()
        valueTrigger.id = id
        valueTrigger.isEnable = enable
        valueTrigger.message = message
        return valueTrigger
    }

}