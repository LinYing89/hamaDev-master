package com.bairock.hamadev.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.bairock.hamadev.database.Config;
import com.bairock.hamadev.media.Media;
import com.bairock.hamadev.R;
import com.bairock.hamadev.app.HamaApp;
import com.bairock.hamadev.settings.DevSwitchAttributeSettingActivity;
import com.bairock.iot.intelDev.device.Device;
import com.bairock.iot.intelDev.device.Gear;
import com.bairock.iot.intelDev.device.IStateDev;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class RecyclerAdapterElectrical3 extends RecyclerView.Adapter<RecyclerAdapterElectrical3.ViewHolder> {

    public static final int AUTO = 0;
    public static final int GEAR_NEED_TO_AUTO = 1;
    public static final int STATE = 2;
    public static final int NAME = 3;
    private static int colorNoraml;
    private Context context;

    public static MyHandler handler;

    private LayoutInflater mInflater;
    private List<Device> listDevice;
    private List<ViewHolder> listViewHolder;

    public RecyclerAdapterElectrical3(Context context, List<Device> listDevice) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.listDevice = listDevice;
        listViewHolder = new ArrayList<>();
        handler = new MyHandler(this);
        colorNoraml = context.getResources().getColor(R.color.back_fort);
    }

    @Override
    public int getItemCount() {
        return listDevice == null ? 0 : listDevice.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewHolder vh = new ViewHolder(mInflater.inflate(R.layout.adapter_electrical_card, parent, false), context);
        listViewHolder.add(vh);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setData(listDevice.get(position));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private Device device;
        private TextView textName;
        private TextView txtGearToAuto;
        private Button btnState;

        public ViewHolder(View itemView, Context context) {
            super(itemView);
            textName = itemView.findViewById(R.id.textName);
            txtGearToAuto = itemView.findViewById(R.id.txtGearToAuto);
            txtGearToAuto.setOnClickListener(v -> device.setGear(Gear.ZIDONG));
            textName.setSelected(true);
            textName.setOnClickListener(view -> {
                if(DevSwitchAttributeSettingActivity.Companion.getDevice() == null) {
                    DevSwitchAttributeSettingActivity.Companion.setDevice(device);
                    context.startActivity(new Intent(context, DevSwitchAttributeSettingActivity.class));
                }
            });
            btnState = itemView.findViewById(R.id.btnState);
            btnState.setOnClickListener(view -> {
                Animation animation= AnimationUtils.loadAnimation(HamaApp.HAMA_CONTEXT,R.anim.ele_btn_state_zoomin);
                view.startAnimation(animation);
                if(Config.INSTANCE.getCtrlRing()) {
                    Media.INSTANCE.playCtrlRing();
                }
                IStateDev dev = (IStateDev) device;
                switch (device.getGear()) {
                    case UNKNOW:
                    case ZIDONG:
                        if (device.isKaiState()) {
                            toGuanGear();
                            HamaApp.sendOrder(device, dev.getTurnOffOrder(), true);
                        } else {
                            toKaiGear();
                            HamaApp.sendOrder(device, dev.getTurnOnOrder(), true);
                        }
                        break;
                    case KAI:
                        toGuanGear();
                        HamaApp.sendOrder(device, dev.getTurnOffOrder(), true);
                        break;
                    default:
                        toKaiGear();
                        HamaApp.sendOrder(device, dev.getTurnOnOrder(), true);
                        break;
                }
            });
        }

        public void setData(Device device) {
            this.device = device;
            init();
        }

        private void init() {
            refreshName();
            refreshState();
            refreshBntStateText(device.getGear());
            refreshGearToAuto();
        }

        private void toKaiGear(){
            device.setGear(Gear.KAI);
        }

        private void toGuanGear(){
            device.setGear(Gear.GUAN);
        }

        private void refreshState() {
            if (!device.isNormal()) {
                textName.setTextColor(HamaApp.abnormalColorId);
            } else {
                textName.setTextColor(colorNoraml);
                refreshBtnState();
            }
        }

        private void refreshBntStateText(Gear gear) {
            switch (gear) {
                case KAI:
                    btnState.setText("O");
                    break;
                case GUAN:
                    btnState.setText("S");
                    break;
                default:
                    btnState.setText("A");
                    break;
            }
        }

        private void refreshBtnState() {
            if (device.isKaiState()) {
                btnState.setBackgroundResource(R.drawable.sharp_btn_switch_on);
            } else {
                btnState.setBackgroundResource(R.drawable.sharp_btn_switch_off);
            }
        }

        private void refreshName() {
            if(Config.INSTANCE.getDevNameShowStyle().equals("0")) {
                textName.setText(device.getName());
            }else{
                textName.setText(device.getAlias());
            }
        }

        private void refreshGearToAuto() {
            if(device.isGearNeedToAuto()) {
                txtGearToAuto.setVisibility(View.VISIBLE);
            }else{
                txtGearToAuto.setVisibility(View.GONE);
            }
        }
    }

    public static class MyHandler extends Handler {
        WeakReference<RecyclerAdapterElectrical3> mActivity;

        MyHandler(RecyclerAdapterElectrical3 activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            RecyclerAdapterElectrical3 theActivity = mActivity.get();
            Device dev = (Device) msg.obj;
            for (ViewHolder vh : theActivity.listViewHolder) {
                if (vh.device == dev) {
                    switch (msg.what) {
                        case AUTO:
                            vh.refreshBntStateText(dev.getGear());
                            break;
                        case STATE:
                            vh.refreshState();
                            break;
                        case NAME:
                            vh.refreshName();
                            break;
                        case GEAR_NEED_TO_AUTO:
                            vh.refreshGearToAuto();
                            break;
                    }
                    break;
                }
            }
        }
    }
}
