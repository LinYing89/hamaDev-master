package com.bairock.hamadev.video

import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.bairock.hamadev.R
import com.bairock.hamadev.adapter.RecyclerAdapterElectrical3
import com.bairock.hamadev.app.ElectricalCtrlFragment
import com.bairock.hamadev.app.HamaApp
import com.bairock.iot.intelDev.device.Device
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView
import java.lang.ref.WeakReference

private const val ARG_PARAM1 = "param1"

class VideoElectricalFragment : Fragment() {
    private var param1: String? = null

    private lateinit var swipeMenuRecyclerViewElectrical: SwipeMenuRecyclerView
    private lateinit var adapterElectrical: RecyclerAdapterElectrical3

    private var listIStateDev = mutableListOf<Device>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val viewRoot = inflater.inflate(R.layout.fragment_electrical_ctrl, container, false)
        handler = MyHandler(this)
        swipeMenuRecyclerViewElectrical = viewRoot.findViewById(R.id.swipeMenuRecyclerViewElectrical)
        swipeMenuRecyclerViewElectrical.layoutManager = GridLayoutManager(this.context, 2)
        setGridViewElectrical()
        return viewRoot
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler = null
    }

    fun setGridViewElectrical() {
        if (null != HamaApp.DEV_GROUP) {
            listIStateDev = HamaApp.DEV_GROUP.findListIStateDev(true)
            listIStateDev.sort()
            for (i in listIStateDev.indices) {
                listIStateDev[i].sortIndex = i
            }
            setAdapter()
        }
    }

    private fun setAdapter() {
            adapterElectrical = RecyclerAdapterElectrical3(this.context, listIStateDev)
            swipeMenuRecyclerViewElectrical.adapter = adapterElectrical
    }

    class MyHandler internal constructor(activity: VideoElectricalFragment) : Handler() {
        internal var mActivity: WeakReference<VideoElectricalFragment> = WeakReference(activity)

        override fun handleMessage(msg: Message) {
            val theActivity = mActivity.get()
            when (msg.what) {
                ElectricalCtrlFragment.REFRESH_ELE_STATE -> theActivity!!.adapterElectrical.notifyDataSetChanged()
                ElectricalCtrlFragment.REFRESH_ELE -> theActivity!!.setGridViewElectrical()
                ElectricalCtrlFragment.CHANGE_SHOW_NAME_STYLE -> theActivity!!.setAdapter()
                ElectricalCtrlFragment.NOTIFY_ADAPTER -> theActivity!!.adapterElectrical.handler.obtainMessage(msg.arg1, msg.obj).sendToTarget()
            }
        }
    }

    companion object {
        var handler: MyHandler? = null

        @JvmStatic
        fun newInstance(param1: Int) =
                VideoElectricalFragment().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_PARAM1, param1)
                    }
                }
    }
}
