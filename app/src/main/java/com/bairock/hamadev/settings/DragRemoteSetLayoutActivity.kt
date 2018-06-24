package com.bairock.hamadev.settings

import android.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import com.bairock.hamadev.R
import com.bairock.hamadev.app.Constant
import com.bairock.hamadev.app.MainActivity
import com.bairock.hamadev.database.RemoterKeyDao
import com.bairock.hamadev.zview.DragRemoterKeyButton
import com.bairock.iot.intelDev.device.remoter.Remoter
import com.bairock.iot.intelDev.device.remoter.RemoterKey
import android.graphics.drawable.BitmapDrawable
import android.widget.*


/**
 * 自定义布局，添加按钮与改变按钮位置界面
 */
class DragRemoteSetLayoutActivity : AppCompatActivity(), View.OnTouchListener {

    companion object {
        var REMOTER: Remoter? = null
    }

    private lateinit var layoutRoot: RelativeLayout

    private var listDragRemoterBtn = mutableListOf<DragRemoterKeyButton>()
    private var lastX = 0
    private var lastY = 0
    private var longClick = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drag_remote_set_layout)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        layoutRoot = findViewById(R.id.layoutRoot)
        initListButtons()
        setGridView()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_search_device, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            R.id.action_add_device -> {
                showRenameDialog(null)
            }
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
        rb.setOnTouchListener(this)
        return rb
    }

    private fun createAndAddDragRemoterButton(remoterKey : RemoterKey){
        val rb = createDragRemoterButton(remoterKey)
        rb.setOnLongClickListener { p0 ->
            if(longClick){
                showPopUp(p0)
            }
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

    private fun showRenameDialog(dragRemoterKeyButton: DragRemoterKeyButton?) {
        val editNewName = EditText(this)
        if(null != dragRemoterKeyButton) {
            editNewName.setText(dragRemoterKeyButton.text)
        }
        AlertDialog.Builder(this)
                .setTitle("输入按键名称")
                .setView(editNewName)
                .setPositiveButton(MainActivity.strEnsure
                ) { _, _ ->
                    val value = editNewName.text.toString()
                    if(REMOTER!!.keyNameIsExists(value)){
                        Toast.makeText(this, "名称重复", Toast.LENGTH_SHORT).show()
                    }else{
                        if(null == dragRemoterKeyButton) {
                            val num = REMOTER!!.nextNumber()
                            if (null != num) {
                                val rk = RemoterKey()
                                rk.number = num
                                rk.name = value
                                rk.locationX = 10
                                rk.locationY = 10
                                REMOTER!!.addRemoterKey(rk)
                                RemoterKeyDao.get(this).add(rk)
                                createAndAddDragRemoterButton(rk)
                                addToLayout(listDragRemoterBtn.last())
                            }
                        }else{
                            dragRemoterKeyButton.remoterKey.name = value
                            dragRemoterKeyButton.text = value
                            RemoterKeyDao.get(this).update(dragRemoterKeyButton.remoterKey)
                        }
                    }
                }.setNegativeButton(MainActivity.strCancel, null).create().show()
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
            showRenameDialog(v as DragRemoterKeyButton)
            popupWindow.dismiss()
        }
        btnDel.setOnClickListener {
            popupWindow.dismiss()
            deleteBtn(v as DragRemoterKeyButton)
        }
    }

    private fun deleteBtn(dragRemoterKeyButton: DragRemoterKeyButton){
        RemoterKeyDao.get(this).delete(dragRemoterKeyButton.remoterKey)
        REMOTER!!.removeRemoterKey(dragRemoterKeyButton.remoterKey)
        listDragRemoterBtn.remove(dragRemoterKeyButton)
        layoutRoot.removeView(dragRemoterKeyButton)
    }

    override fun onTouch(p0: View?, p1: MotionEvent): Boolean {
        val dragRemoterKeyButton = p0 as DragRemoterKeyButton
        when (p1.action) {
            MotionEvent.ACTION_DOWN -> {
                lastX = p1.rawX.toInt()
                lastY = p1.rawY.toInt()
                longClick = true
            }

            MotionEvent.ACTION_MOVE -> {
                val dx = p1.rawX.toInt() - lastX
                val dy = p1.rawY.toInt() - lastY
                if (dx > 1 || dy > 1) {
                    longClick = false
                }

                var top = p0.getTop() + dy
                var left = p0.getLeft() + dx

                if (top <= 0) {
                    top = 0
                }
                if (top >= Constant.displayHeight - dragRemoterKeyButton.height) {
                    top =  Constant.displayHeight - dragRemoterKeyButton.height
                }
                if (left >=  Constant.displayWidth - dragRemoterKeyButton.width) {
                    left =  Constant.displayWidth - dragRemoterKeyButton.width
                }
                if (left <= 0) {
                    left = 0
                }

                dragRemoterKeyButton.remoterKey.locationX = left
                dragRemoterKeyButton.remoterKey.locationY = top
                dragRemoterKeyButton.layoutBtn()
                // v.layout(left, top, left + iv.getWidth(), top + iv.getHeight());
                lastX = p1.rawX.toInt()
                lastY = p1.rawY.toInt()
            }
            MotionEvent.ACTION_UP -> {
                if(!longClick){
                    RemoterKeyDao.get(this).update(dragRemoterKeyButton.remoterKey)
                }
                //setGridView()
            }
        }
        return false
    }
}
