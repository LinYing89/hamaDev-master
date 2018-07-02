package com.bairock.hamadev.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.bairock.iot.intelDev.device.Device
import com.bairock.iot.intelDev.linkage.device.DeviceLinkage

class DeviceLinkageDao(context: Context) {

    private val mDatabase: SQLiteDatabase = SdDbHelper(context).writableDatabase

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var deviceLinkageDao: DeviceLinkageDao? = null

        fun get(context: Context): DeviceLinkageDao {
            if (null == deviceLinkageDao) {
                deviceLinkageDao = DeviceLinkageDao(context)
            }
            return deviceLinkageDao!!
        }
    }

    private fun getContentValues(deviceLinkage: DeviceLinkage): ContentValues {
        val values = ContentValues()
        values.put(DbSb.TabDeviceLinkage.Cols.ID, deviceLinkage.id)
        values.put(DbSb.TabDeviceLinkage.Cols.SWITCH_MODEL, deviceLinkage.switchModel)
        values.put(DbSb.TabDeviceLinkage.Cols.VALUE1, deviceLinkage.value1)
        values.put(DbSb.TabDeviceLinkage.Cols.VALUE2, deviceLinkage.value2)
        values.put(DbSb.TabDeviceLinkage.Cols.SOURCE_DEVICE_ID, deviceLinkage.sourceDevice.id)
        if(deviceLinkage.targetDevice != null) {
            values.put(DbSb.TabDeviceLinkage.Cols.TARGET_DEV_ID, deviceLinkage.targetDevice.id)
        }
        return values
    }

    fun add(deviceLinkage: DeviceLinkage) {
        val values = getContentValues(deviceLinkage)
        mDatabase.insert(DbSb.TabDeviceLinkage.NAME, null, values)
    }

    fun delete(deviceLinkage: DeviceLinkage) {
        mDatabase.delete(DbSb.TabDeviceLinkage.NAME, DbSb.TabDeviceLinkage.Cols.ID + "=?", arrayOf(deviceLinkage.id))
    }

    fun find(device : Device, listDevice : List<Device>): List<DeviceLinkage> {
        return find(DbSb.TabDeviceLinkage.Cols.SOURCE_DEVICE_ID + " = ?", arrayOf(device.id), listDevice)
    }

    fun find(whereClause: String, whereArgs: Array<String>, listDevice : List<Device>): List<DeviceLinkage> {
        val listDeviceLinkage = mutableListOf<DeviceLinkage>()
        val cursor = query(whereClause, whereArgs, listDevice)
        try {
            cursor.moveToFirst()
            while (!cursor.isAfterLast) {
                val device = cursor.getDeviceLinkage()
                listDeviceLinkage.add(device)
                cursor.moveToNext()
            }
        } finally {
            cursor.close()
        }
        return listDeviceLinkage
    }

    fun update(deviceLinkage: DeviceLinkage) {
        val values = getContentValues(deviceLinkage)
        mDatabase.update(DbSb.TabDeviceLinkage.NAME, values,
                "id = ?",
                arrayOf(deviceLinkage.id))
    }

    private fun query(whereClause: String, whereArgs: Array<String>, listDevice : List<Device>): DeviceLinkageWrapper {
        val cursor = mDatabase.query(
                DbSb.TabDeviceLinkage.NAME, // having
                null // orderBy
                , // Columns - null selects all columns
                whereClause,
                whereArgs, null, null, null
        )// groupBy
        return DeviceLinkageWrapper(cursor, listDevice)
    }

    fun clean() {
        mDatabase.execSQL("delete from " + DbSb.TabDeviceLinkage.NAME)
    }
}