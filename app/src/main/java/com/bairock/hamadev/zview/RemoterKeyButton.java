package com.bairock.hamadev.zview;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

import com.bairock.hamadev.R;
import com.bairock.iot.intelDev.device.remoter.RemoterKey;

public class RemoterKeyButton extends android.support.v7.widget.AppCompatButton {

    private RemoterKey remoterKey;

    public RemoterKeyButton(Context context) {
        super(context);
        init();
    }
    public RemoterKeyButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }
    public RemoterKeyButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        setWidth(100);
        setHeight(100);
        setTextColor(Color.WHITE);
        setBackgroundResource(R.drawable.sharp_btn_switch_off);
    }

    public RemoterKey getRemoterKey() {
        return remoterKey;
    }

    public void setRemoterKey(RemoterKey remoterKey) {
        this.remoterKey = remoterKey;
    }
}
