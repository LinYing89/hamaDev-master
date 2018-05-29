package com.bairock.hamadev.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Handler
import android.os.Message
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import com.bairock.hamadev.R
import com.bairock.hamadev.app.HamaApp
import com.bairock.hamadev.database.Config
import com.bairock.hamadev.media.Media
import com.bairock.hamadev.settings.DevSwitchAttributeSettingActivity
import com.bairock.iot.intelDev.device.Device
import com.bairock.iot.intelDev.device.Gear
import com.bairock.iot.intelDev.device.IStateDev
import java.lang.ref.WeakReference
import java.util.ArrayList

class RecyclerAdapterElectricalList(private var context: Context, listDevice: MutableList<Device>) : RecyclerView.Adapter<RecyclerAdapterElectricalList.ViewHolder>() {

    companion object {
        const val AUTO = 0
        const val STATE = 2
        const val NAME = 3
        var colorNormal: Int = 0
        var colorOn: Int = 0
        var colorGear: Int = Color.parseColor("#1E90FF")
        var colorGearNot: Int = Color.BLACK
        var handler: MyHandler? = null
    }

    private var mInflater: LayoutInflater = LayoutInflater.from(context)
    private var listDevice: List<Device> = listDevice
    private var listViewHolder: MutableList<ViewHolder> = ArrayList()

    init {
        handler = MyHandler(this)
        colorNormal = ContextCompat.getColor(context, R.color.back_fort)
        colorOn = ContextCompat.getColor(context, R.color.state_kai)
    }

    override fun getItemCount(): Int {
        return listDevice.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vh = ViewHolder(mInflater.inflate(R.layout.adapter_electrical_list, parent, false), context)
        listViewHolder.add(vh)
        return vh
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(listDevice[position])
    }

    class ViewHolder(itemView: View, context: Context) : RecyclerView.ViewHolder(itemView) {

        lateinit var device: Device
        private val viewRoot = itemView;
        private val textName: TextView = itemView.findViewById(R.id.txtName)
        private val btnOn : Button = itemView.findViewById(R.id.btnOn)
        private val btnAuto : Button = itemView.findViewById(R.id.btnAuto)
        private val btnOff : Button = itemView.findViewById(R.id.btnOff)

        init {
            textName.setOnClickListener {
                if (DevSwitchAttributeSettingActivity.device == null) {
                    DevSwitchAttributeSettingActivity.device = device
                    context.startActivity(Intent(context, DevSwitchAttributeSettingActivity::class.java))
                }
            }

            btnOn.setOnClickListener{
                startAnim(it)
                device.gear = Gear.KAI
                refreshGear()
                HamaApp.sendOrder(device, (device as IStateDev).turnOnOrder, true)
            }

            btnAuto.setOnClickListener{
                startAnim(it)
                device.gear = Gear.ZIDONG
                refreshGear()
            }

            btnOff.setOnClickListener{
                startAnim(it)
                device.gear = Gear.GUAN
                refreshGear()
                HamaApp.sendOrder(device, (device as IStateDev).turnOffOrder, true)
            }

        }

        private fun startAnim(v:View){
            val animation = AnimationUtils.loadAnimation(HamaApp.HAMA_CONTEXT, R.anim.ele_btn_state_zoomin)
            v.startAnimation(animation)
            if (Config.ctrlRing) {
                Media.playCtrlRing()
            }
        }

        fun setData(device: Device) {
            this.device = device
            init()
        }

        private fun init() {
            refreshName()
            refreshState()
            refreshGear()
        }

        internal fun refreshState() {
            if (!device.isNormal) {
                //textName.setTextColor(HamaApp.abnormalColorId)
                viewRoot.setBackgroundColor(HamaApp.abnormalColorId)
            } else {
                //textName.setTextColor(colorNormal)
                viewRoot.setBackgroundColor(Color.TRANSPARENT)
                if(device.isKaiState){
                    //btnOn.setBackgroundResource(R.drawable.sharp_btn_switch_on)
                    viewRoot.setBackgroundColor(colorOn)
                }else{
                    viewRoot.setBackgroundColor(Color.TRANSPARENT)
                    //btnOn.setBackgroundResource(R.drawable.sharp_btn_switch_off)
                }
            }
        }

        internal fun refreshName() {
            if(Config.devNameShowStyle == "0") {
                textName.text = device.name
            }else{
                textName.text = device.alias
            }
        }

        internal fun refreshGear(){
            when(device.gear){
                Gear.KAI ->{
                    btnOn.setTextColor(colorGear)
                    btnAuto.setTextColor(colorGearNot)
                    btnOff.setTextColor(colorGearNot)
                }
                Gear.GUAN ->{
                    btnOn.setTextColor(colorGearNot)
                    btnAuto.setTextColor(colorGearNot)
                    btnOff.setTextColor(colorGear)
                }
                else ->{
                    btnOn.setTextColor(colorGearNot)
                    btnAuto.setTextColor(colorGear)
                    btnOff.setTextColor(colorGearNot)
                }
            }
        }
    }

    class MyHandler internal constructor(activity: RecyclerAdapterElectricalList) : Handler() {
        internal var mActivity: WeakReference<RecyclerAdapterElectricalList> = WeakReference(activity)

        override fun handleMessage(msg: Message) {
            val theActivity = mActivity.get()
            val dev = msg.obj as Device
            for (vh in theActivity!!.listViewHolder) {
                if (vh.device === dev) {
                    when (msg.what) {
                        AUTO -> vh.refreshGear()
                        STATE -> vh.refreshState()
                        NAME -> vh.refreshName()
                    }
                    break
                }
            }
        }
    }
}