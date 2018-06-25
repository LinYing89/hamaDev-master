package com.bairock.hamadev.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.bairock.hamadev.video.VideoCollectorFragment
import com.bairock.hamadev.video.VideoElectricalFragment
import com.bairock.hamadev.video.VideoMessageFragment

class VideoDevicePagerAdapter(fm : FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment? {
        var fragment: Fragment? = null
        when (position) {
            0 -> fragment = VideoElectricalFragment.newInstance(position)
            1 -> fragment = VideoCollectorFragment.newInstance(position)
            2 -> fragment = VideoMessageFragment.newInstance(position)
        }
        return fragment
    }

    override fun getCount(): Int {
        return 3
    }

    override fun getPageTitle(position: Int): CharSequence? {
        when (position) {
            0 -> return "电器"
            1 -> return "仪表"
            2 -> return "消息"
        }
        return null
    }
}