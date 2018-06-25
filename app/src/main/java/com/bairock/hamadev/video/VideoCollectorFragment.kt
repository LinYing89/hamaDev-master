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
import com.bairock.hamadev.adapter.RecyclerAdapterCollect
import com.bairock.hamadev.app.ClimateFragment
import com.bairock.hamadev.app.HamaApp
import com.bairock.iot.intelDev.device.devcollect.DevCollect
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView
import java.lang.ref.WeakReference

private const val ARG_PARAM1 = "param1"

class VideoCollectorFragment : Fragment() {
    private var param1: String? = null

    private lateinit var swipeMenuRecyclerViewCollector: SwipeMenuRecyclerView
    private var adapterCollect: RecyclerAdapterCollect? = null
    private var listDevCollect = mutableListOf<DevCollect>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_climate, container, false)
        handler = MyHandler(this)
        swipeMenuRecyclerViewCollector = view.findViewById(R.id.swipeMenuRecyclerViewCollector)
        swipeMenuRecyclerViewCollector.layoutManager = GridLayoutManager(this.context, 2)
        swipeMenuRecyclerViewCollector.isLongPressDragEnabled = true // 长按拖拽，默认关闭。

        setPressueList()
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler = null
    }

    private fun setPressueList() {
        listDevCollect = HamaApp.DEV_GROUP.findListCollectDev(true)
        listDevCollect.sort()
        for (i in listDevCollect.indices) {
            listDevCollect[i].sortIndex = i
        }
        setAdapter()
    }

    private fun setAdapter() {
        adapterCollect = RecyclerAdapterCollect(this.context, listDevCollect)
        swipeMenuRecyclerViewCollector.adapter = adapterCollect
    }

    class MyHandler internal constructor(activity: VideoCollectorFragment) : Handler() {
        internal var mActivity: WeakReference<VideoCollectorFragment> = WeakReference(activity)

        override fun handleMessage(msg: Message) {
            val theActivity = mActivity.get()
            when (msg.what) {
                ClimateFragment.REFRESH_VALUE -> if (null != theActivity!!.adapterCollect) {
                    theActivity.adapterCollect!!.notifyDataSetChanged()
                }
                ClimateFragment.REFRESH_DEVICE -> theActivity!!.setPressueList()
                ClimateFragment.CHANGE_SHOW_NAME_STYLE -> theActivity!!.setAdapter()
                ClimateFragment.NOTIFY_ADAPTER -> theActivity!!.adapterCollect!!.handler.obtainMessage(msg.arg1, msg.obj).sendToTarget()
            }

        }
    }

    companion object {
        var handler: MyHandler? = null
        @JvmStatic
        fun newInstance(param1: Int) =
                VideoCollectorFragment().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_PARAM1, param1)
                    }
                }
    }
}
