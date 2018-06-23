package com.bairock.hamadev.settings

import android.app.Activity
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ListView
import com.bairock.hamadev.R
import android.content.Intent
import android.view.MenuItem


class SelectRemoterActivity : AppCompatActivity() {

    lateinit var lvRemoter : ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_remoter)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }
        val remoters = resources.getStringArray(R.array.array_remoter)

        lvRemoter = findViewById(R.id.lvRemotor)
        lvRemoter.onItemClickListener = AdapterView.OnItemClickListener { p0, p1, p2, p3 ->
            val intent = Intent()
            intent.putExtra("remoterCode", p2+1)
            intent.putExtra("remoterName", remoters[p2])
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home){
            setResult(Activity.RESULT_CANCELED, null)
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}
