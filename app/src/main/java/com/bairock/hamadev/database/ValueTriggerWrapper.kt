package com.bairock.hamadev.database

import android.database.Cursor
import android.database.CursorWrapper
import com.bairock.iot.intelDev.device.CompareSymbol
import com.bairock.iot.intelDev.device.devcollect.ValueTrigger
import com.bairock.iot.intelDev.device.remoter.RemoterKey

class ValueTriggerWrapper(cursor: Cursor) : CursorWrapper(cursor) {

    fun getValueTrigger(): ValueTrigger {
        val id = getString(getColumnIndex(DbSb.TabValueTrigger.Cols.ID))
        val name = getString(getColumnIndex(DbSb.TabValueTrigger.Cols.NAME))
        val enable = getString(getColumnIndex(DbSb.TabValueTrigger.Cols.ENABLE)) == "1";
        val triggerValue = getFloat(getColumnIndex(DbSb.TabValueTrigger.Cols.TRIGGER_VALUE))
        val compareSymbol = CompareSymbol.valueOf(getString(getColumnIndex(DbSb.TabValueTrigger.Cols.COMPARE_SYMBOL)))
        val message = getString(getColumnIndex(DbSb.TabValueTrigger.Cols.MESSAGE))

        val valueTrigger = ValueTrigger()
        valueTrigger.id = id
        valueTrigger.name = name
        valueTrigger.isEnable = enable
        valueTrigger.triggerValue = triggerValue
        valueTrigger.compareSymbol = compareSymbol
        valueTrigger.message = message
        return valueTrigger
    }
}