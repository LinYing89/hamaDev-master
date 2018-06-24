package com.bairock.hamadev.app

import android.graphics.drawable.BitmapDrawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.PopupWindow
import android.widget.RelativeLayout
import com.bairock.hamadev.R
import com.bairock.hamadev.settings.DragRemoteSetLayoutActivity
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
        DragRemoteSetLayoutActivity.REMOTER = null
    }

    private fun initListButtons() {
        for (remoterKey in DragRemoteSetLayoutActivity.REMOTER!!.listRemoterKey) {
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
        val layout = this.layoutInflater
                .inflate(R.layout.edit_del, null)
        val btnEdit = layout.findViewById(R.id.btnEdit) as Button
        val btnDel = layout.findViewById(R.id.btnDel) as Button

        val popupWindow = PopupWindow(layout, RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT)

        popupWindow.isFocusable = true
        popupWindow.isOutsideTouchable = true
        popupWindow.setBackgroundDrawable(BitmapDrawable())

        val location = IntArray(2)
        v.getLocationOnScreen(location)

        popupWindow.showAsDropDown(v)
        btnEdit.setOnClickListener {
            popupWindow.dismiss()
        }
    }
}
