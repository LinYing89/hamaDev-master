package com.bairock.hamadev.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.bairock.iot.intelDev.device.alarm.AlarmTrigger
import com.bairock.iot.intelDev.device.alarm.DevAlarm

class AlarmTriggerDao(context: Context) {

    private val mDatabase: SQLiteDatabase = SdDbHelper(context).writableDatabase

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var alarmTriggerDao: AlarmTriggerDao? = null

        fun get(context: Context): AlarmTriggerDao {
            if (null == alarmTriggerDao) {
                alarmTriggerDao = AlarmTriggerDao(context)
            }
            return alarmTriggerDao!!
        }
    }

    private fun getContentValues(alarmTrigger: AlarmTrigger): ContentValues {
        val values = ContentValues()
        values.put(DbSb.TabAlarmTrigger.Cols.ID, alarmTrigger.id)
        values.put(DbSb.TabAlarmTrigger.Cols.ENABLE, alarmTrigger.isEnable)
        values.put(DbSb.TabAlarmTrigger.Cols.MESSAGE, alarmTrigger.message)
        values.put(DbSb.TabAlarmTrigger.Cols.DEV_ALARM_ID, alarmTrigger.devAlarm.id)
        return values
    }

    fun add(alarmTrigger: AlarmTrigger) {
        val values = getContentValues(alarmTrigger)
        mDatabase.insert(DbSb.TabAlarmTrigger.NAME, null, values)
    }

    fun delete(alarmTrigger: AlarmTrigger) {
        mDatabase.delete(DbSb.TabAlarmTrigger.NAME, DbSb.TabAlarmTrigger.Cols.ID + "=?", arrayOf(alarmTrigger.id))
    }

    fun find(devAlarm: DevAlarm): AlarmTrigger? {
        return find(DbSb.TabAlarmTrigger.Cols.DEV_ALARM_ID + " = ?", arrayOf(devAlarm.id))
    }

    fun find(whereClause: String, whereArgs: Array<String>): AlarmTrigger? {
        var alarmTrigger: AlarmTrigger? = null
        val cursor = query(whereClause, whereArgs)
        try {
            if (cursor.count > 0) {
                cursor.moveToFirst()
                alarmTrigger = cursor.getAlarmTrigger()
            }
        } finally {
            cursor.close()
        }
        return alarmTrigger
    }

    fun update(alarmTrigger: AlarmTrigger) {
        val values = getContentValues(alarmTrigger)
        mDatabase.update(DbSb.TabAlarmTrigger.NAME, values,
                "id = ?",
                arrayOf(alarmTrigger.id))
    }

    private fun query(whereClause: String, whereArgs: Array<String>): AlarmTriggerWrapper {
        val cursor = mDatabase.query(
                DbSb.TabAlarmTrigger.NAME, // having
                null // orderBy
                , // Columns - null selects all columns
                whereClause,
                whereArgs, null, null, null
        )// groupBy
        return AlarmTriggerWrapper(cursor)
    }

    fun clean() {
        mDatabase.execSQL("delete from " + DbSb.TabAlarmTrigger.NAME)
    }
}