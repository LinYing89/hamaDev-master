package com.bairock.hamadev.settings

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.bairock.hamadev.R
import com.bairock.iot.intelDev.linkage.device.DeviceLinkage

class DeviceLinkageSettingActivity : AppCompatActivity() {

    companion object {
        var DEVICE_LINKAGE : DeviceLinkage? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_linkage_setting)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }
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
}
