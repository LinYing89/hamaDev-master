package com.bairock.hamadev.communication;

import com.bairock.hamadev.adapter.RecyclerAdapterCollect;
import com.bairock.hamadev.adapter.RecyclerAdapterDevice;
import com.bairock.hamadev.adapter.RecyclerAdapterElectrical3;
import com.bairock.hamadev.adapter.RecyclerAdapterElectricalList;
import com.bairock.hamadev.app.ClimateFragment;
import com.bairock.hamadev.app.ElectricalCtrlFragment;
import com.bairock.hamadev.app.HamaApp;
import com.bairock.hamadev.database.Config;
import com.bairock.iot.intelDev.communication.RefreshCollectorValueHelper;
import com.bairock.iot.intelDev.device.CtrlModel;
import com.bairock.iot.intelDev.device.DevStateHelper;
import com.bairock.iot.intelDev.device.Device;
import com.bairock.iot.intelDev.device.IStateDev;
import com.bairock.iot.intelDev.device.LinkType;
import com.bairock.iot.intelDev.device.devcollect.DevCollect;
import com.bairock.iot.intelDev.device.devcollect.DevCollectClimateContainer;
import com.bairock.iot.intelDev.device.devswitch.SubDev;

/**
 * 设备状态改变监听器
 * Created by 44489 on 2017/8/30.
 */

public class MyOnStateChangedListener implements Device.OnStateChangedListener {

    private final static Object syno = new Object();
//    private static final String TAG = MyOnStateChangedListener.class.getSimpleName();
    @Override
    public void onStateChanged(Device device, String s) {
        refreshUi(device);
    }

    @Override
    public void onNormalToAbnormal(Device device) {
        //Log.e(TAG, "onNormalToAbnormal " + device.getCoding());
        refreshSearchUi(device);
        HamaApp.addOfflineDevCoding(device);
        //本地设备才往服务器发送状态，远程设备只接收服务器状态
        if(!(device instanceof SubDev) && device.findSuperParent().getCtrlModel() == CtrlModel.LOCAL) {
            PadClient.getIns().send(device.createAbnormalOrder());
        }
        if(device instanceof DevCollectClimateContainer){
            RefreshCollectorValueHelper.getIns().endRefresh(device);
        }
    }

    @Override
    public void onAbnormalToNormal(Device device) {
        //Log.e(TAG, "onAbnormalToNormal " + device.getCoding());
        refreshSearchUi(device);
        HamaApp.removeOfflineDevCoding(device);

        if(device instanceof DevCollectClimateContainer){
            RefreshCollectorValueHelper.getIns().RefreshDev(device);
        }

        //addToRefreshCollectorValueHelper(device);
    }

//    private void addToRefreshCollectorValueHelper(Device device){
//        boolean canAdd = false;
//        if(device instanceof DevCollectSignalContainer){
//            canAdd = true;
//        }else if(device instanceof DevCollect){
//            if(device.getParent() == null){
//                canAdd = true;
//            }else if(!(device.getParent() instanceof DevCollectSignalContainer)){
//                canAdd = true;
//            }
//        }
//        if(canAdd){
//            if(!(device instanceof Pressure)) {
//                RefreshCollectorValueHelper.getIns().RefreshDev(device);
//            }
//        }
//    }

    private void refreshUi(Device device){
        synchronized (syno) {
            if (device instanceof IStateDev) {
                if(Config.INSTANCE.getDevShowStyle().equals("0")) {
                    if(null != ElectricalCtrlFragment.handler){
                        ElectricalCtrlFragment.handler.obtainMessage(ElectricalCtrlFragment.NOTIFY_ADAPTER, RecyclerAdapterElectrical3.STATE, RecyclerAdapterElectrical3.STATE, device).sendToTarget();
                    }
                }else{
                    if (null != RecyclerAdapterElectricalList.Companion.getHandler()) {
                        RecyclerAdapterElectricalList.Companion.getHandler().obtainMessage(RecyclerAdapterElectricalList.STATE, device).sendToTarget();
                    }
                }
            }else if(device instanceof DevCollect){
                if(null != ClimateFragment.handler){
                    ClimateFragment.handler.obtainMessage(ClimateFragment.NOTIFY_ADAPTER, RecyclerAdapterCollect.STATE, RecyclerAdapterCollect.STATE, device).sendToTarget();
                }
            }
        }
    }

    private void refreshSearchUi(Device device){
        if(!(device instanceof SubDev)){
            if(null != RecyclerAdapterDevice.handler){
                RecyclerAdapterDevice.handler.obtainMessage(RecyclerAdapterDevice.STATE, device).sendToTarget();
            }
        }
    }

    @Override
    public void onNoResponse(Device device) {
        if(device.getLinkType() == LinkType.SERIAL){
            device.setDevStateId(DevStateHelper.DS_YI_CHANG);
        }
    }
}
