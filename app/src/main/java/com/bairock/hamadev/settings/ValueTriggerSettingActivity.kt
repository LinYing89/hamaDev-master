package com.bairock.hamadev.settings

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import com.bairock.hamadev.R
import com.bairock.hamadev.database.ValueTriggerDao
import com.bairock.iot.intelDev.device.CompareSymbol
import com.bairock.iot.intelDev.device.devcollect.ValueTrigger

class ValueTriggerSettingActivity : AppCompatActivity() {

    companion object {
        var valueTrigger : ValueTrigger? = null
    }

    lateinit var txtDevice : TextView
    lateinit var spinner: Spinner
    lateinit var etxtValue : EditText
    private lateinit var etxtMessage : EditText
    lateinit var btnSave : Button
    lateinit var btnCancel : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_value_trigger_setting)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        findViews()
        setListener()
        init()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home ->
                finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        valueTrigger = null
    }

    fun findViews(){
        txtDevice = findViewById(R.id.txtDevice)
        spinner = findViewById(R.id.spinnerSymbol)
        etxtValue = findViewById(R.id.etxtValue)
        etxtMessage = findViewById(R.id.etxtMessage)
        btnSave = findViewById(R.id.btnSave)
        btnCancel = findViewById(R.id.btnCancel)
    }

    fun setListener(){
        btnSave.setOnClickListener {
            val collectSrc = spinner.selectedItemPosition
            val compareSymbol = CompareSymbol.values()[collectSrc]
            valueTrigger!!.compareSymbol = compareSymbol
            valueTrigger!!.triggerValue = etxtValue.text.toString().toFloat()
            valueTrigger!!.message = etxtMessage.text.toString()
            ValueTriggerDao.get(this).update(valueTrigger!!)
            finish()
        }
        btnCancel.setOnClickListener {
            finish()
        }
    }

    fun init(){
        txtDevice.text = valueTrigger!!.collectProperty.devCollect.name
        spinner.setSelection(valueTrigger!!.compareSymbol.ordinal)
        etxtValue.setText(valueTrigger!!.triggerValue.toString())
        etxtMessage.setText(valueTrigger!!.message)
    }
}
