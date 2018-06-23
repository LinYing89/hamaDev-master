package com.bairock.hamadev.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.bairock.iot.intelDev.device.remoter.Remoter
import com.bairock.iot.intelDev.device.remoter.RemoterKey

class RemoterKeyDao(context: Context) {

    private val mDatabase: SQLiteDatabase = SdDbHelper(context).writableDatabase

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var remoterKeyDao: RemoterKeyDao? = null

        fun get(context: Context): RemoterKeyDao {
            if (null == remoterKeyDao) {
                remoterKeyDao = RemoterKeyDao(context)
            }
            return remoterKeyDao!!
        }
    }

    private fun getContentValues(remoterKey: RemoterKey): ContentValues {
        val values = ContentValues()
        values.put(DbSb.TabRemoterKey.Cols.ID, remoterKey.id)
        values.put(DbSb.TabRemoterKey.Cols.REMOTE_ID, remoterKey.remoter.id)
        values.put(DbSb.TabRemoterKey.Cols.NAME, remoterKey.name)
        values.put(DbSb.TabRemoterKey.Cols.NUMBER, remoterKey.number)
        values.put(DbSb.TabRemoterKey.Cols.LOCATION_X, remoterKey.locationX)
        values.put(DbSb.TabRemoterKey.Cols.LOCATION_Y, remoterKey.locationY)
        return values
    }

    fun add(remoterKey: RemoterKey) {
        val values = getContentValues(remoterKey)
        mDatabase.insert(DbSb.TabCollectProperty.NAME, null, values)
    }

    fun delete(remoterKey: RemoterKey) {
        mDatabase.delete(DbSb.TabCollectProperty.NAME, DbSb.TabRemoterKey.Cols.ID + "=?", arrayOf(remoterKey.id))
    }

    fun find(remoter: Remoter): List<RemoterKey> {
        return find(DbSb.TabRemoterKey.Cols.REMOTE_ID + " = ?", arrayOf(remoter.id))
    }

    fun find(whereClause: String, whereArgs: Array<String>): List<RemoterKey> {
        val listDevice = mutableListOf<RemoterKey>()
        val cursor = query(whereClause, whereArgs)
        try {
            cursor.moveToFirst()
            while (!cursor.isAfterLast) {
                val device = cursor.getRemoterKey()
                listDevice.add(device)
                cursor.moveToNext()
            }
        } finally {
            cursor.close()
        }
        return listDevice
    }

    fun update(remoterKey: RemoterKey) {
        val values = getContentValues(remoterKey)
        mDatabase.update(DbSb.TabRemoterKey.NAME, values,
                "id = ?",
                arrayOf(remoterKey.id))
    }

    private fun query(whereClause: String, whereArgs: Array<String>): RemoterKeyWrapper {
        val cursor = mDatabase.query(
                DbSb.TabRemoterKey.NAME, // having
                null // orderBy
                , // Columns - null selects all columns
                whereClause,
                whereArgs, null, null, null
        )// groupBy
        return RemoterKeyWrapper(cursor)
    }

    fun clean() {
        mDatabase.execSQL("delete from " + DbSb.TabRemoterKey.NAME)
    }
}