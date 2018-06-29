package com.bairock.hamadev.app;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.StrictMode;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.bairock.hamadev.R;
import com.bairock.hamadev.communication.PadClient;
import com.bairock.hamadev.database.Config;
import com.bairock.iot.intelDev.communication.DevChannelBridgeHelper;
import com.bairock.iot.intelDev.communication.DevServer;
import com.bairock.iot.intelDev.communication.FindDevHelper;
import com.bairock.iot.intelDev.device.Coordinator;
import com.bairock.iot.intelDev.device.DevHaveChild;
import com.bairock.iot.intelDev.device.Device;
import com.bairock.iot.intelDev.device.devcollect.DevCollect;
import com.bairock.iot.intelDev.device.devswitch.SubDev;
import com.bairock.iot.intelDev.user.DevGroup;
import com.bairock.iot.intelDev.user.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tencent.tac.messaging.TACMessagingToken;
import com.videogo.openapi.EZOpenSDK;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/8/27.
 */

public class HamaApp extends Application {

    public static User USER;
    public static DevGroup DEV_GROUP;
    public static DevServer DEV_SERVER;

    @SuppressLint("StaticFieldLeak")
    public static Context HAMA_CONTEXT;

    public static boolean NET_CONNECTED;
    public static boolean SERVER_CONNECTED;
    public static boolean BIND_TAG_SUCCESS;

    public static TACMessagingToken tacMessagingToken = null;
    public static int abnormalColorId;
    public static int stateKaiColorId;

    @Override   protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //SERVER_IP = "192.168.2.100";
        //URL_ROOT = "http://" + SERVER_IP + ":8080/hamaSer";
        //DevServer.PORT = 8000;

        //Stetho.initializeWithDefaults(this);
//        DebugDB.getAddressLog();
        HAMA_CONTEXT = this.getApplicationContext();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }

        abnormalColorId = getResources().getColor(R.color.abnormal);
        stateKaiColorId = getResources().getColor(R.color.state_kai);

        LogUtils.INSTANCE.init(this);

        initSDK();
//        AudioPlayUtil.init(this);
    }

    public static String getLoginUrl() {
        return "http://" + Config.INSTANCE.getServerName() + "/hamaSer/ClientLoginServlet";
    }

    public static String getPortUrl() {
        return "http://" + Config.INSTANCE.getServerName() + "/hamaSer/GetPortServlet";
    }

    public static String getCompareVersionUrl(int appVc) {
        return "http://" + Config.INSTANCE.getServerName() + "/hamaSer/CompareAppVersion?appVc=" + appVc + "&debug=" + LogUtils.INSTANCE.getAPP_DBG();
    }

    public static String getDownloadAppUrl(String appName) {
        return "http://" + Config.INSTANCE.getServerName() + "/hamaSer/Download?appName=" + appName + "&debug=" + LogUtils.INSTANCE.getAPP_DBG();
    }

    public static void addOfflineDevCoding(Device device) {
        if (null != device) {
            if (device instanceof Coordinator) {
                FindDevHelper.getIns().findDev(device.getCoding());
            } else if (!(device.findSuperParent() instanceof Coordinator)) {
                FindDevHelper.getIns().findDev(device.findSuperParent().getCoding());
            }
        }
    }

    public static void removeOfflineDevCoding(Device device) {
        if (null != device) {
            if (null == device.getParent() || !(device.findSuperParent() instanceof Coordinator)) {
                FindDevHelper.getIns().alreadyFind(device.findSuperParent().getCoding());
            }
        }
    }

    public static void sendOrder(Device device, String order, boolean immediately) {
        switch (device.getLinkType()) {
            case SERIAL:
                Device rootDev = device;
                if (device instanceof SubDev) {
                    rootDev = device.getParent();
                }
                //如果最后一次通信大于5s，发送
                //如果最后一次通信小于5s，但是设备有返回，发送
                //如果最后一次通信小于5s，并且设备无返回，不发送
                Log.e("HamaApp", rootDev + "," + rootDev.getCommunicationInterval() + "," + rootDev.getNoResponse());
                if (rootDev.canSend()) {
                    rootDev.noResponsePlus();
                    rootDev.resetLastCommunicationTime();
                }
                break;
            case NET:
                switch (device.getCtrlModel()) {
                    case UNKNOW:
                        DevChannelBridgeHelper.getIns().sendDevOrder(device, order, immediately);
                        PadClient.getIns().send(order);
                        break;
                    case LOCAL:
                        DevChannelBridgeHelper.getIns().sendDevOrder(device, order, immediately);
                        break;
                    case REMOTE:
                        PadClient.getIns().send(order);
                        break;
                }
                break;
        }
    }

    private static void copyChildDevices(DevHaveChild dev1, DevHaveChild dev2, boolean copyId) {
        List<Device> listNewDevice = new ArrayList<>();
        for (Device device2 : dev2.getListDev()) {
            boolean haved = false;
            for (Device device1 : dev1.getListDev()) {
                if (device1.getCoding().equals(device2.getCoding())) {
                    if (copyId) {
                        copyDevice(device1, device2);
                    } else {
                        copyDeviceExceptId(device1, device2);
                    }
                    haved = true;
                    break;
                }
            }
            if (!haved) {
                listNewDevice.add(device2);
            }
        }
        for (Device device : listNewDevice) {
            dev1.addChildDev(device);
        }
    }

    //用dev2的属性复写dev1的属性
    public static void copyDevice(Device dev1, Device dev2) {
        dev1.setId(dev2.getId());
        copyDeviceExceptId(dev1, dev2);
        if (dev1 instanceof DevHaveChild) {
            copyChildDevices((DevHaveChild) dev1, (DevHaveChild) dev2, true);
        }
    }

    public static void copyDeviceExceptId(Device dev1, Device dev2) {
        dev1.setName(dev2.getName());
        dev1.setMainCodeId(dev2.getMainCodeId());
        dev1.setSubCode(dev2.getSubCode());
        dev1.setSn(dev2.getSn());
        dev1.setDevCategory(dev2.getDevCategory());
        dev1.setPlace(dev2.getPlace());
        dev1.setAlias(dev2.getAlias());
        dev1.setGear(dev2.getGear());
        dev1.setDevStateId(dev2.getDevStateId());
        dev1.setCtrlModel(dev2.getCtrlModel());
        dev1.setSortIndex(dev2.getSortIndex());
        dev1.setVisibility(dev2.isVisibility());
        dev1.setDeleted(dev2.isDeleted());
        if (dev1 instanceof DevHaveChild) {
            copyChildDevices((DevHaveChild) dev1, (DevHaveChild) dev2, false);
        }
        if (dev1 instanceof DevCollect && dev2 instanceof DevCollect) {
            DevCollect dc1 = (DevCollect) dev1;
            DevCollect dc2 = (DevCollect) dev2;
            dc1.getCollectProperty().setCollectSrc(dc2.getCollectProperty().getCollectSrc());
            dc1.getCollectProperty().setCrestValue(dc2.getCollectProperty().getCrestValue());
            dc1.getCollectProperty().setCurrentValue(dc2.getCollectProperty().getCurrentValue());
            dc1.getCollectProperty().setLeastValue(dc2.getCollectProperty().getLeastValue());
            dc1.getCollectProperty().setPercent(dc2.getCollectProperty().getPercent());
            dc1.getCollectProperty().setUnitSymbol(dc2.getCollectProperty().getUnitSymbol());
        }
    }

    public static String getUserJson(User user) {
        String json = null;
        if (null != user) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                json = mapper.writeValueAsString(user);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return json;
    }

    public static EZOpenSDK getOpenSDK() {
        return EZOpenSDK.getInstance();
    }

    private void initSDK() {
        EZOpenSDK.showSDKLog(true);

        EZOpenSDK.enableP2P(true);

        String appKey = "3a2cb8b66afb494cb03b273257d3ddd1";
        EZOpenSDK.initLib(this, appKey);
    }

    public static String getGroupTag(){
        String tag = null;
        if(HamaApp.USER != null && HamaApp.USER.getName() != null){
            tag = HamaApp.USER.getName() + "_" + HamaApp.DEV_GROUP.getName();
        }
        return tag;
    }

    /**
     * 信鸽SDK，为设备绑定tag，方便服务器给同一账号的多个设备推送信息
     */
    public static void setTokenTag(){
        String tag = getGroupTag();
        //如果已绑定成功过，不再重复绑定
        if(!BIND_TAG_SUCCESS && null != tag && null != tacMessagingToken){
            tacMessagingToken.bindTag(HamaApp.HAMA_CONTEXT, tag);
            BIND_TAG_SUCCESS = true;
        }
    }
}
