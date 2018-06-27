package com.bairock.hamadev.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.bairock.iot.intelDev.device.Device
import com.bairock.iot.intelDev.device.devcollect.CollectProperty
import com.bairock.iot.intelDev.device.devcollect.ValueTrigger

class ValueTriggerDao(context: Context) {

    private val mDatabase: SQLiteDatabase = SdDbHelper(context).writableDatabase

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var valueTriggerDao: ValueTriggerDao? = null

        fun get(context: Context): ValueTriggerDao {
            if (null == valueTriggerDao) {
                valueTriggerDao = ValueTriggerDao(context)
            }
            return valueTriggerDao!!
        }
    }

    private fun getContentValues(valueTrigger: ValueTrigger): ContentValues {
        val values = ContentValues()
        values.put(DbSb.TabValueTrigger.Cols.ID, valueTrigger.id)
        values.put(DbSb.TabValueTrigger.Cols.NAME, valueTrigger.name)
        values.put(DbSb.TabValueTrigger.Cols.ENABLE, valueTrigger.isEnable)
        values.put(DbSb.TabValueTrigger.Cols.TRIGGER_VALUE, valueTrigger.triggerValue)
        values.put(DbSb.TabValueTrigger.Cols.COMPARE_SYMBOL, valueTrigger.compareSymbol.toString())
        values.put(DbSb.TabValueTrigger.Cols.MESSAGE, valueTrigger.message)
        values.put(DbSb.TabValueTrigger.Cols.COLLECT_PROPERTY_ID, valueTrigger.collectProperty.id)
        return values
    }

    fun add(valueTrigger: ValueTrigger) {
        val values = getContentValues(valueTrigger)
        mDatabase.insert(DbSb.TabValueTrigger.NAME, null, values)
    }

    fun delete(valueTrigger: ValueTrigger) {
        mDatabase.delete(DbSb.TabValueTrigger.NAME, DbSb.TabValueTrigger.Cols.ID + "=?", arrayOf(valueTrigger.id))
    }

    fun find(collectProperty: CollectProperty): List<ValueTrigger> {
        return find(DbSb.TabValueTrigger.Cols.COLLECT_PROPERTY_ID + " = ?", arrayOf(collectProperty.id))
    }

    fun find(whereClause: String, whereArgs: Array<String>): List<ValueTrigger> {
        val listValueTrigger = mutableListOf<ValueTrigger>()
        val cursor = query(whereClause, whereArgs)
        try {
            cursor.moveToFirst()
            while (!cursor.isAfterLast) {
                val device = cursor.getValueTrigger()
                listValueTrigger.add(device)
                cursor.moveToNext()
            }
        } finally {
            cursor.close()
        }
        return listValueTrigger
    }

    fun update(valueTrigger: ValueTrigger) {
        val values = getContentValues(valueTrigger)
        mDatabase.update(DbSb.TabValueTrigger.NAME, values,
                "id = ?",
                arrayOf(valueTrigger.id))
    }

    private fun query(whereClause: String, whereArgs: Array<String>): ValueTriggerWrapper {
        val cursor = mDatabase.query(
                DbSb.TabValueTrigger.NAME, // having
                null // orderBy
                , // Columns - null selects all columns
                whereClause,
                whereArgs, null, null, null
        )// groupBy
        return ValueTriggerWrapper(cursor)
    }

    fun clean() {
        mDatabase.execSQL("delete from " + DbSb.TabValueTrigger.NAME)
    }
}