package com.bairock.hamadev.database

import android.database.Cursor
import android.database.CursorWrapper
import com.bairock.iot.intelDev.device.remoter.RemoterKey

class RemoterKeyWrapper(cursor: Cursor) : CursorWrapper(cursor) {

    fun getRemoterKey(): RemoterKey {
        val id = getString(getColumnIndex(DbSb.TabRemoterKey.Cols.ID))
        val name = getString(getColumnIndex(DbSb.TabRemoterKey.Cols.NAME))
        val number = getString(getColumnIndex(DbSb.TabRemoterKey.Cols.NUMBER))
        val locationX = getInt(getColumnIndex(DbSb.TabRemoterKey.Cols.LOCATION_X))
        val locationY = getInt(getColumnIndex(DbSb.TabRemoterKey.Cols.LOCATION_Y))

        val remoterKey = RemoterKey()
        remoterKey.id = id
        remoterKey.name = name
        remoterKey.number = number
        remoterKey.locationX = locationX
        remoterKey.locationY = locationY
        return remoterKey
    }
}