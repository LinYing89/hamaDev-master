package com.bairock.hamadev.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Switch
import android.widget.TextView
import com.bairock.hamadev.R
import com.bairock.hamadev.app.HamaApp
import com.bairock.hamadev.database.ValueTriggerDao
import com.bairock.iot.intelDev.device.devcollect.ValueTrigger

class AdapterValueTrigger(var context: Context, private var listValueTrigger: List<ValueTrigger>) : RecyclerView.Adapter<AdapterValueTrigger.ViewHolder>() {
    private val mInflater: LayoutInflater = LayoutInflater.from(context)

    override fun getItemCount(): Int {
        return listValueTrigger.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(mInflater.inflate(R.layout.adapter_linkage_holder, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(listValueTrigger[position])
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var valueTrigger: ValueTrigger? = null
        private val txtName: TextView = itemView.findViewById(R.id.txtLinkageName)
        private val switchEnable: Switch = itemView.findViewById(R.id.switchEnable)

        private var onCheckedChangeListener: CompoundButton.OnCheckedChangeListener = CompoundButton.OnCheckedChangeListener { _, isChecked ->
            valueTrigger!!.isEnable = isChecked
            ValueTriggerDao.get(HamaApp.HAMA_CONTEXT).update(valueTrigger!!)
        }

        fun setData(valueTrigger: ValueTrigger) {
            this.valueTrigger = valueTrigger
            init()
        }

        private fun init() {
            txtName.text = valueTrigger!!.name
            switchEnable.isChecked = valueTrigger!!.isEnable
            switchEnable.setOnCheckedChangeListener(onCheckedChangeListener)
        }
    }
}