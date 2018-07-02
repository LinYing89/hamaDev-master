package com.bairock.hamadev.database

import android.database.Cursor
import android.database.CursorWrapper
import com.bairock.iot.intelDev.device.Device
import com.bairock.iot.intelDev.linkage.device.DeviceLinkage
import com.bairock.iot.intelDev.user.DevGroup

class DeviceLinkageWrapper(cursor: Cursor, var listDevice : List<Device>) : CursorWrapper(cursor) {

    fun getDeviceLinkage(): DeviceLinkage {
        val id = getString(getColumnIndex(DbSb.TabDeviceLinkage.Cols.ID))
        val switchModel = getInt(getColumnIndex(DbSb.TabDeviceLinkage.Cols.SWITCH_MODEL))
        val value1 = getFloat(getColumnIndex(DbSb.TabDeviceLinkage.Cols.VALUE1))
        val value2 = getFloat(getColumnIndex(DbSb.TabDeviceLinkage.Cols.VALUE2))
        val targetDevId = getString(getColumnIndex(DbSb.TabDeviceLinkage.Cols.TARGET_DEV_ID))

        val targetDev = DevGroup.findDeviceByDevId(listDevice, targetDevId)

        val deviceLinkage = DeviceLinkage()
        deviceLinkage.id = id
        deviceLinkage.switchModel = switchModel
        deviceLinkage.value1 = value1
        deviceLinkage.value2 = value2
        deviceLinkage.targetDevice = targetDev
        return deviceLinkage
    }

}