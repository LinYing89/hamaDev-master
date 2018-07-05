package com.bairock.hamadev.settings

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.*
import com.bairock.hamadev.R
import com.bairock.hamadev.app.HamaApp
import com.bairock.iot.intelDev.device.Coordinator
import com.bairock.iot.intelDev.device.Device
import com.bairock.iot.intelDev.linkage.device.DeviceLinkage
import com.bairock.iot.intelDev.user.DevGroup
import java.util.ArrayList

class DeviceLinkageSettingActivity : AppCompatActivity() {

    companion object {
        var DEVICE_LINKAGE : DeviceLinkage? = null
    }

    private lateinit var spinnerTargetDev : Spinner
    //小于
    private lateinit var etxtValue1 : EditText
    private lateinit var spinnerAction1: Spinner
    private lateinit var txtTargetDevName1 : TextView
    //大于
    private lateinit var etxtValue2 : EditText
    private lateinit var spinnerAction2: Spinner
    private lateinit var txtTargetDevName2 : TextView
    private lateinit var btnSave : Button
    private lateinit var btnCancel : Button

    private var listTargetDev = mutableListOf<Device>()
    private lateinit var targetDevice : Device
    private var switchModel = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_linkage_setting)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        //只有协调器系统下的设备才可设置
        if(DEVICE_LINKAGE!!.sourceDevice.findSuperParent() !is Coordinator){
            Toast.makeText(this, "非协调器系统", Toast.LENGTH_SHORT).show()
            finish()
        }

        val coordinator = DEVICE_LINKAGE!!.sourceDevice.findSuperParent() as Coordinator
        listTargetDev = DevGroup.findListIStateDev(coordinator.listDev, true)
        if(listTargetDev.isEmpty()){
            Toast.makeText(this, "无可控设备", Toast.LENGTH_SHORT).show()
            finish()
        }
        targetDevice = if(DEVICE_LINKAGE!!.targetDevice != null){
            DEVICE_LINKAGE!!.targetDevice
        }else {
            listTargetDev[0]
        }

        findViews()
        setListener()
        setTargetDevSpinner()
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
        DEVICE_LINKAGE = null
    }

    fun findViews(){
        spinnerTargetDev = findViewById(R.id.spinnerTargetDev)
        //小于
        etxtValue1 = findViewById(R.id.etxtValue1)
        spinnerAction1 = findViewById(R.id.spinnerAction1)
        txtTargetDevName1 = findViewById(R.id.txtTargetDevName1)
        //大于
        etxtValue2 = findViewById(R.id.etxtValue2)
        spinnerAction2 = findViewById(R.id.spinnerAction2)
        txtTargetDevName2 = findViewById(R.id.txtTargetDevName2)
        btnSave = findViewById(R.id.btnSave)
        btnCancel = findViewById(R.id.btnCancel)
    }

    fun setListener(){
        spinnerTargetDev.setOnItemClickListener { _, _, i, _ ->
            run {
                targetDevice = listTargetDev[i]
                txtTargetDevName1.text = targetDevice.name
                txtTargetDevName2.text = targetDevice.name
            }
        }
        spinnerAction1.setOnItemClickListener { _, _, i, _ ->
            run{
                if(i == 0){
                    switchModel = 2
                    spinnerAction2.setSelection(1)
                }else{
                    switchModel = 1
                    spinnerAction2.setSelection(0)
                }

            }
        }
        spinnerAction2.setOnItemClickListener { _, _, i, _ ->
            run{
                if(i == 0){
                    switchModel = 1
                    spinnerAction1.setSelection(1)
                }else{
                    switchModel = 2
                    spinnerAction1.setSelection(0)
                }
            }
        }
        btnSave.setOnClickListener {
            DEVICE_LINKAGE!!.targetDevice = targetDevice
            DEVICE_LINKAGE!!.switchModel =switchModel
            DEVICE_LINKAGE!!.value1 = etxtValue1.text.toString().toFloat()
            DEVICE_LINKAGE!!.value2 = etxtValue2.text.toString().toFloat()
            if(DEVICE_LINKAGE!!.sourceDevice.findSuperParent().isNormal){
                HamaApp.sendOrder(DEVICE_LINKAGE!!.sourceDevice.findSuperParent(), DEVICE_LINKAGE!!.createSetOrder(), true)
            }else{
                Toast.makeText(this, "协调器状态异常", Toast.LENGTH_SHORT).show()
            }
            finish()
        }
        btnCancel.setOnClickListener { finish() }
    }

    private fun init(){
        spinnerTargetDev.setSelection(listTargetDev.indexOf(targetDevice))
        if(DEVICE_LINKAGE!!.switchModel == 1){
            spinnerAction1.setSelection(0)
            spinnerAction2.setSelection(1)
        }else{
            spinnerAction1.setSelection(1)
            spinnerAction2.setSelection(0)
        }
        etxtValue1.setText(DEVICE_LINKAGE!!.value1.toString())
        etxtValue2.setText(DEVICE_LINKAGE!!.value2.toString())
    }

    private fun setTargetDevSpinner(){
        val listDeviceName = ArrayList<String>()
        for (device in listTargetDev) {
            listDeviceName.add(device.name)
        }
        spinnerTargetDev.adapter = ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1, listDeviceName)

    }
}
