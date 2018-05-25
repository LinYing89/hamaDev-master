package com.bairock.hamadev.database

import android.content.Context
import android.preference.PreferenceManager

object Config{

    const val keyServerName = "serverName"
    private const val keyServerPadPort = "serverPadPort"
    private const val keyServerDevPort = "serverDevPort"
    private const val keyServerUpDownloadPort = "serverUpDownloadPort"
    internal const val keyRouteName = "routeName"
    internal const val keyRoutePsd = "routePsd"
    internal const val keyDevShowStyle = "showStyle"
    internal const val keyCtrlRing = "ctrlRing"
    private const val keyNeedLogin = "needLogin"
    private const val keyDownloadId = "downloadId"

    var serverName = "123.206.104.15"
    var serverPadPort = 10002
    var serverDevPort = 10003
    var serverUpDownloadPort = 10004
    var routeName = ""
    var routePsd = ""
    var devShowStyle = ""
    var ctrlRing = true
    var needLogin = true
    //var downloadId = ""

    fun init(context: Context){
        val shared = PreferenceManager.getDefaultSharedPreferences(context)
        serverName = shared.getString(keyServerName, serverName)
        serverPadPort = shared.getInt(keyServerPadPort, serverPadPort)
        serverDevPort = shared.getInt(keyServerDevPort, serverDevPort)
        serverUpDownloadPort = shared.getInt(keyServerUpDownloadPort, serverUpDownloadPort)
        routeName = shared.getString(keyRouteName, routeName)
        routePsd = shared.getString(keyRoutePsd, routePsd)
        devShowStyle = shared.getString(keyDevShowStyle, "0")
        needLogin = shared.getBoolean(keyNeedLogin, true)
        //ctrlRing = shared.getBoolean(keyCtrlRing, ctrlRing)
    }

    fun setServerInfo(context: Context){
        val shared = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = shared.edit()
        editor.putInt(keyServerPadPort, serverPadPort)
        editor.putInt(keyServerDevPort, serverDevPort)
        editor.putInt(keyServerUpDownloadPort, serverUpDownloadPort)
        editor.apply()
    }

    fun setNeedLogin(context: Context, needLogin: Boolean){
        this.needLogin = needLogin
        val shared = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = shared.edit()
        editor.putBoolean(keyNeedLogin, needLogin)
        editor.apply()
    }

    fun setDownloadId(context: Context, downloadId: Long){
        val shared = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = shared.edit()
        editor.putLong(keyDownloadId, downloadId)
        editor.apply()
    }

    fun getDownloadId(context: Context): Long{
        val shared = PreferenceManager.getDefaultSharedPreferences(context)
        return shared.getLong(keyDownloadId, 1L)
    }
}