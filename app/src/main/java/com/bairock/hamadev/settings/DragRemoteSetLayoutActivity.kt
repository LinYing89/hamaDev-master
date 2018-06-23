package com.bairock.hamadev.settings

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.RelativeLayout
import com.bairock.hamadev.R
import com.bairock.hamadev.zview.DragRemoterKeyButton
import com.bairock.iot.intelDev.device.remoter.Remoter



class DragRemoteSetLayoutActivity : AppCompatActivity() , View.OnTouchListener{

    companion object {
        var REMOTER : Remoter? = null
    }

    private lateinit var layoutRoot : RelativeLayout

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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initListButtons() {
        for (remoterKey in REMOTER!!.listRemoterKey) {
            val rb = DragRemoterKeyButton(this)
            rb.remoterKey = remoterKey
            rb.setOnTouchListener(this)
            //rb.setOnLongClickListener(BtnLongListener())
            listDragRemoterBtn.add(rb)
        }
    }

    private fun setGridView() {
        layoutRoot.removeAllViews()
        for (cb in listDragRemoterBtn) {
            addToLayout(cb)
        }
    }

    private fun addToLayout(cb: DragRemoterKeyButton) {
        val layoutParams = RelativeLayout.LayoutParams(
                100, 100)
        layoutParams.topMargin = cb.remoterKey.locationY
        layoutParams.leftMargin = cb.remoterKey.locationX
        cb.layoutParams = layoutParams
        layoutRoot.addView(cb)
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
                if (top >= 100 - dragRemoterKeyButton.height) {
                    top = 100 - dragRemoterKeyButton.height
                }
                if (left >= 100 - dragRemoterKeyButton.width) {
                    left = 100 - dragRemoterKeyButton.width
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
            MotionEvent.ACTION_UP -> setGridView()
        }
        return false
    }
}
