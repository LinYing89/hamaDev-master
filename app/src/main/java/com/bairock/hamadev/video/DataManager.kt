package com.bairock.hamadev.video

import java.util.HashMap

object DataManager {
    private var mDeviceSerialVerifyCodeMap = HashMap<String, String?>()

    /**
     * 缓存设备验证码信息
     * @param deviceSerial
     * @param verifyCode
     */
    @Synchronized
    fun setDeviceSerialVerifyCode(deviceSerial: String, verifyCode: String?) {
        mDeviceSerialVerifyCodeMap[deviceSerial] = verifyCode
    }

    /**
     * @param deviceSerial
     * @return    获取缓存的设备验证码信息
     */
    @Synchronized
    fun getDeviceSerialVerifyCode(deviceSerial: String): String? {
        return if (mDeviceSerialVerifyCodeMap.containsKey(deviceSerial)) {
            mDeviceSerialVerifyCodeMap[deviceSerial]
        } else null
    }
}