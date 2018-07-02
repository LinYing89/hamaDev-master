package com.bairock.hamadev.settings

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import com.bairock.hamadev.R
import com.bairock.hamadev.adapter.AdapterValueChangeLinkage
import com.bairock.hamadev.database.DeviceLinkageDao
import com.bairock.iot.intelDev.device.Device
import com.bairock.iot.intelDev.linkage.device.DeviceLinkage
import com.yanzhenjie.recyclerview.swipe.SwipeItemClickListener
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItemClickListener
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView

/**
 * 采集设备值变化连锁设置界面
 */
class ValueChangeLinkageActivity : AppCompatActivity() {

    companion object {
        var DEVICE : Device? = null
    }
    private lateinit var lvDeviceLinkage : SwipeMenuRecyclerView

    private lateinit var adapterCondition : AdapterValueChangeLinkage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_value_change_linkage)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        lvDeviceLinkage = findViewById(R.id.lvDeviceLinkage)
        lvDeviceLinkage.layoutManager = LinearLayoutManager(this)
        lvDeviceLinkage.setSwipeMenuCreator(swipeMenuConditionCreator)
        lvDeviceLinkage.setSwipeItemClickListener(linkageSwipeItemClickListener)
        lvDeviceLinkage.setSwipeMenuItemClickListener(linkageSwipeMenuItemClickListener)

        setListViewCondition()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_search_device, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home ->
                finish()
            R.id.action_add_device -> {
                val deviceLinkage = DeviceLinkage()
                deviceLinkage.sourceDevice = DEVICE
                DeviceLinkageDao.get(this).add(deviceLinkage)

            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        DEVICE = null
    }

    private fun setListViewCondition() {
        adapterCondition = AdapterValueChangeLinkage(this, DEVICE!!.listDeviceLinkage)
        lvDeviceLinkage.adapter = adapterCondition
    }

    private val swipeMenuConditionCreator = SwipeMenuCreator{ _, swipeRightMenu, _ ->
        val width = resources.getDimensionPixelSize(R.dimen.dp_70)
        val height = ViewGroup.LayoutParams.MATCH_PARENT

        val deleteItem = SwipeMenuItem(this)
                .setBackgroundColor(ContextCompat.getColor(this, R.color.red_normal))
                .setText("删除")
                .setTextColor(Color.WHITE)
                .setWidth(width)
                .setHeight(height)
        swipeRightMenu.addMenuItem(deleteItem)
    }

    private val linkageSwipeItemClickListener = SwipeItemClickListener { _, _ ->

    }

    private val linkageSwipeMenuItemClickListener = SwipeMenuItemClickListener { menuBridge ->
        menuBridge.closeMenu()
        val menuPosition = menuBridge.position
        when(menuPosition){
            0 ->{
                //删除
            }
        }
    }
}
