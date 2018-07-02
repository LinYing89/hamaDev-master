package com.bairock.hamadev.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.bairock.hamadev.R
import com.bairock.iot.intelDev.device.alarm.DevAlarm
import com.bairock.iot.intelDev.device.devcollect.DevCollect
import com.bairock.iot.intelDev.linkage.device.DeviceLinkage

class AdapterValueChangeLinkage(var context: Context, private var listDeviceLinkage: List<DeviceLinkage>) : RecyclerView.Adapter<AdapterValueChangeLinkage.ViewHolder>() {
    private val mInflater: LayoutInflater = LayoutInflater.from(context)

    override fun getItemCount(): Int {
        return listDeviceLinkage.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(mInflater.inflate(R.layout.adapter_value_change_device_linkage, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(listDeviceLinkage[position])
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var deviceLinkage: DeviceLinkage? = null
        private val txtInfo: TextView = itemView.findViewById(R.id.txtInfo)


        fun setData(deviceLinkage: DeviceLinkage) {
            this.deviceLinkage = deviceLinkage
            init()
        }

        private fun init() {
            txtInfo.text = getInfo()
        }

        private fun getInfo() : String{
            var info = ""
            if(deviceLinkage!!.sourceDevice is DevAlarm){
                info += "目标设备: " + deviceLinkage!!.targetDevice.name + "\n"
            }else if(deviceLinkage!!.sourceDevice is DevCollect){
                val action1: String
                val action2: String
                if(deviceLinkage!!.switchModel == 1){
                    action1 = "开"
                    action2 = "关"
                }else{
                    action1 = "关"
                    action2 = "开"
                }
                info += "目标设备: " + deviceLinkage!!.targetDevice.name + "\n" +
                        "小于" + deviceLinkage!!.value1 + action1 + "\n" +
                        "大于" + deviceLinkage!!.value2 + action2
            }
            return info
        }
    }
}