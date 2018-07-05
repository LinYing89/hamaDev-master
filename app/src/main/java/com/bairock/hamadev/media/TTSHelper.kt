package com.bairock.hamadev.media

import android.speech.tts.TextToSpeech
import com.bairock.hamadev.app.HamaApp
import android.widget.Toast
import java.util.*


object TTSHelper : TextToSpeech.OnInitListener{

    private val tts = TextToSpeech(HamaApp.HAMA_CONTEXT, this)

    override fun onInit(p0: Int) {
        if(p0 == TextToSpeech.SUCCESS){
            //默认设定语言为中文，原生的android貌似不支持中文。
            val result = tts.setLanguage(Locale.CHINA)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(HamaApp.HAMA_CONTEXT, "语音不支持中文", Toast.LENGTH_SHORT).show()
            } else {
                //不支持中文就将语言设置为英文
                tts.language = Locale.US
            }
        }
    }

    fun speech(content : String){
        tts.speak(content, TextToSpeech.QUEUE_FLUSH, null);
    }
}