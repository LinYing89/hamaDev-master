package com.bairock.hamadev.settings

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.EditText
import com.bairock.hamadev.R
import com.bairock.hamadev.adapter.AdapterValueTrigger
import com.bairock.hamadev.app.MainActivity
import com.bairock.hamadev.database.ValueTriggerDao
import com.bairock.iot.intelDev.device.devcollect.CollectProperty
import com.bairock.iot.intelDev.device.devcollect.ValueTrigger
import com.yanzhenjie.recyclerview.swipe.SwipeItemClickListener
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItemClickListener
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView
import com.yanzhenjie.recyclerview.swipe.widget.DefaultItemDecoration

class ValueTriggerListActivity : AppCompatActivity() {

    companion object {
        var collectProperty: CollectProperty? = null
    }

    private lateinit var lvValueTrigger: SwipeMenuRecyclerView
    private lateinit var adapterValueTrigger: AdapterValueTrigger

    private var valueTrigger: ValueTrigger? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_value_trigger_list)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        lvValueTrigger = findViewById(R.id.lvValueTrigger)

        lvValueTrigger.layoutManager = LinearLayoutManager(this)
        lvValueTrigger.addItemDecoration(DefaultItemDecoration(Color.LTGRAY))
        lvValueTrigger.setSwipeMenuCreator(swipeMenuConditionCreator)

        lvValueTrigger.setSwipeItemClickListener(linkageSwipeItemClickListener)
        lvValueTrigger.setSwipeMenuItemClickListener(linkageSwipeMenuItemClickListener)

        setListTrigger()
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
                showRenameDialog(null)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        collectProperty = null
    }

    private fun setListTrigger() {
        adapterValueTrigger = AdapterValueTrigger(this, collectProperty!!.listValueTrigger)
        lvValueTrigger.adapter = adapterValueTrigger
    }

    private val swipeMenuConditionCreator = SwipeMenuCreator { _, swipeRightMenu, _ ->
        val width = resources.getDimensionPixelSize(R.dimen.dp_70)
        val height = ViewGroup.LayoutParams.MATCH_PARENT

        val renameItem = SwipeMenuItem(this)
                .setBackgroundColor(ContextCompat.getColor(this, R.color.orange))
                .setText("重命名")
                .setTextColor(Color.WHITE)
                .setWidth(width)
                .setHeight(height)
        swipeRightMenu.addMenuItem(renameItem)
        val deleteItem = SwipeMenuItem(this)
                .setBackgroundColor(ContextCompat.getColor(this, R.color.red_normal))
                .setText("删除")
                .setTextColor(Color.WHITE)
                .setWidth(width)
                .setHeight(height)
        swipeRightMenu.addMenuItem(deleteItem)
    }

    private fun showRenameDialog(oldName: String?) {
        val isRename = null != oldName
        val editNewName = EditText(this)
        val title: String
        title = if (isRename) {
            editNewName.setText(oldName)
            this.getString(R.string.rename)
        } else {
            "输入名称"
        }
        AlertDialog.Builder(this)
                .setTitle(title)
                .setView(editNewName)
                .setPositiveButton(MainActivity.strEnsure) { _, _ ->
                    val value = editNewName.text.toString()
                    if (isRename) {
                        valueTrigger!!.name = value
                        ValueTriggerDao.get(this).update(valueTrigger!!)
                        adapterValueTrigger.notifyDataSetChanged()
                    } else {
                        val valueTrigger = ValueTrigger()
                        valueTrigger.name = value
                        valueTrigger.isEnable = true
                        collectProperty!!.addValueTrigger(valueTrigger)
                        ValueTriggerDao.get(this).add(valueTrigger)
                        adapterValueTrigger.notifyDataSetChanged()

                        ValueTriggerSettingActivity.valueTrigger = valueTrigger
                        startActivity(Intent(this, ValueTriggerSettingActivity::class.java))
                    }
                }.setNegativeButton(MainActivity.strCancel, null).create().show()
    }

    private val linkageSwipeItemClickListener = SwipeItemClickListener { _, position ->
        ValueTriggerSettingActivity.valueTrigger = collectProperty!!.listValueTrigger[position]
        startActivity(Intent(this, ValueTriggerSettingActivity::class.java))
    }

    private val linkageSwipeMenuItemClickListener = SwipeMenuItemClickListener { menuBridge ->
        menuBridge.closeMenu()
        val adapterPosition = menuBridge.adapterPosition
        val menuPosition = menuBridge.position
        valueTrigger = collectProperty!!.listValueTrigger[adapterPosition]

        when (menuPosition) {
            0 -> {
                showRenameDialog(valueTrigger!!.name)
            }
            1 -> {
                //删除
                ValueTriggerDao.get(this).delete(valueTrigger!!)
                collectProperty!!.listValueTrigger.remove(valueTrigger!!)
                adapterValueTrigger.notifyDataSetChanged()
            }
        }

    }
}
