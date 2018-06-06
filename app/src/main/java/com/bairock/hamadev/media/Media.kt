package com.bairock.hamadev.media

import android.content.Context
import android.media.AudioManager
import android.media.SoundPool
import com.bairock.hamadev.R

object Media{

    private val soundPool = SoundPool(1, AudioManager.STREAM_MUSIC, 0)
    private val soundMap = HashMap<Int, Int>()

    fun init(context: Context){
        soundMap[1] = soundPool.load(context, R.raw.da2, 1)
    }

    fun playCtrlRing(){
        soundPool.play(soundMap[1]!!, 1f, 1f, 1, 0, 1f)
    }
}