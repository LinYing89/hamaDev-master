package com.bairock.hamadev.app

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.PopupWindow
import android.widget.RelativeLayout
import com.bairock.hamadev.R
import com.bairock.hamadev.database.Config
import com.bairock.hamadev.media.Media
import com.bairock.hamadev.remote.StudyKeyActivity
import com.bairock.hamadev.zview.DragRemoterKeyButton
import com.bairock.iot.intelDev.device.remoter.Remoter
import com.bairock.iot.intelDev.device.remoter.RemoterKey

class DragRemoterActivity : AppCompatActivity() {

    companion object {
        var REMOTER: Remoter? = null
    }

    private lateinit var layoutRoot: RelativeLayout

    private var listDragRemoterBtn = mutableListOf<DragRemoterKeyButton>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drag_remoter)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        layoutRoot = findViewById(R.id.layoutRoot)
        initListButtons()
        setGridView()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        REMOTER = null
    }

    private fun initListButtons() {
        for (remoterKey in REMOTER!!.listRemoterKey) {
            createAndAddDragRemoterButton(remoterKey)
        }
    }

    private fun createDragRemoterButton(remoterKey : RemoterKey) : DragRemoterKeyButton{
        val rb = DragRemoterKeyButton(this)
        rb.remoterKey = remoterKey
        return rb
    }

    private fun createAndAddDragRemoterButton(remoterKey : RemoterKey){
        val rb = createDragRemoterButton(remoterKey)
        rb.setOnLongClickListener { p0 ->
            showPopUp(p0)
            false
        }
        rb.setOnClickListener{
            if (Config.ctrlRing) {
                Media.playCtrlRing()
            }
            val remoterKey1 = (it as DragRemoterKeyButton).remoterKey
            HamaApp.sendOrder(remoterKey1.remoter.parent, remoterKey1.createCtrlKeyOrder(), true)
        }
        listDragRemoterBtn.add(rb)
    }

    private fun setGridView() {
        layoutRoot.removeAllViews()
        for (cb in listDragRemoterBtn) {
            addToLayout(cb)
        }
    }

    private fun addToLayout(cb: DragRemoterKeyButton) {
        val width = Constant.getRemoterKeyWidth()
        val layoutParams = RelativeLayout.LayoutParams(
                width, width)
        if (cb.remoterKey.locationY >= Constant.displayHeight - width) {
            cb.remoterKey.locationY =  Constant.displayHeight - width
        }
        if (cb.remoterKey.locationX >=  Constant.displayWidth - width) {
            cb.remoterKey.locationX =  Constant.displayWidth - width
        }

        layoutParams.topMargin = cb.remoterKey.locationY
        layoutParams.leftMargin = cb.remoterKey.locationX
        cb.layoutParams = layoutParams
        layoutRoot.addView(cb)
    }

    private fun showPopUp(v: View) {
        val rb = v as DragRemoterKeyButton
        val btnStudy = Button(this)
        btnStudy.text = "学习"
        val popupWindow = PopupWindow(btnStudy, Constant.dip2px(100f),  Constant.dip2px(46f))

        popupWindow.isFocusable = true
        popupWindow.isOutsideTouchable = true
        popupWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val location = IntArray(2)
        v.getLocationOnScreen(location)

        popupWindow.showAsDropDown(v)
        btnStudy.setOnClickListener {
            popupWindow.dismiss()
            StudyKeyActivity.remoterKey = rb.remoterKey
            startActivity(Intent(this, StudyKeyActivity::class.java))
        }
    }
}
