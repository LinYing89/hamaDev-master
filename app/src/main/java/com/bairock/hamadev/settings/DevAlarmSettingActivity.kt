package com.bairock.hamadev.settings

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
import com.bairock.hamadev.R
import com.bairock.hamadev.database.AlarmTriggerDao
import com.bairock.hamadev.database.DeviceDao
import com.bairock.iot.intelDev.device.alarm.DevAlarm

class DevAlarmSettingActivity : AppCompatActivity() {

    companion object {
        var DEVICE : DevAlarm? = null
    }

    private lateinit var txtCoding : TextView
    private lateinit var etxtName : EditText
    private lateinit var etxtAlias : EditText
    private lateinit var switchAlarm : Switch
    private lateinit var etxtMessage : EditText
    private lateinit var btnSave : Button
    private lateinit var btnCancel : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dev_alarm_setting)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        findViews()
        setListener()
        init()
    }

    override fun onDestroy() {
        super.onDestroy()
        DEVICE = null
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home ->
                finish()
        }
        return super.onOptionsItemSelected(item)
    }

    fun findViews(){
        txtCoding = findViewById(R.id.txtCoding)
        etxtName = findViewById(R.id.etxtName)
        etxtAlias = findViewById(R.id.etxtAlias)
        switchAlarm = findViewById(R.id.switchAlarm)
        etxtMessage = findViewById(R.id.etxtMessage)
        btnSave = findViewById(R.id.btnSave)
        btnCancel = findViewById(R.id.btnCancel)
    }

    fun setListener(){
        btnSave.setOnClickListener {
            DEVICE!!.name = etxtName.text.toString()
            DEVICE!!.alias = etxtAlias.text.toString()
            DeviceDao.get(this).update(DEVICE!!)

            DEVICE!!.trigger.isEnable = switchAlarm.isChecked
            DEVICE!!.trigger.message = etxtMessage.text.toString()
            AlarmTriggerDao.get(this).update(DEVICE!!.trigger)

            finish()
        }
        btnCancel.setOnClickListener {
            finish()
        }
    }

    fun init(){
        txtCoding.text = DEVICE!!.longCoding
        etxtName.setText(DEVICE!!.name)
        etxtAlias.setText(DEVICE!!.alias)
        etxtMessage.setText(DEVICE!!.trigger.message)
        switchAlarm.isChecked = DEVICE!!.trigger.isEnable
    }
}
