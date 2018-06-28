package com.bairock.hamadev.app;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;

import com.bairock.hamadev.communication.MyOnGearNeedToAutoListener;
import com.bairock.hamadev.communication.MyOnValueTriggedListener;
import com.bairock.hamadev.media.Media;
import com.bairock.hamadev.R;
import com.bairock.hamadev.communication.ChannelBridgeHelperHeartSendListener;
import com.bairock.hamadev.communication.MyMessageAnalysiser;
import com.bairock.hamadev.communication.MyOnAliasChangedListener;
import com.bairock.hamadev.communication.MyOnBridgesChangedListener;
import com.bairock.hamadev.communication.MyOnCommunicationListener;
import com.bairock.hamadev.communication.MyOnCtrlModelChangedListener;
import com.bairock.hamadev.communication.MyOnCurrentValueChangedListener;
import com.bairock.hamadev.communication.MyOnDevHaveChildeOnCollectionChangedListener;
import com.bairock.hamadev.communication.MyOnGearChangedListener;
import com.bairock.hamadev.communication.MyOnNameChangedListener;
import com.bairock.hamadev.communication.MyOnSignalSourceChangedListener;
import com.bairock.hamadev.communication.MyOnSimulatorChangedListener;
import com.bairock.hamadev.communication.MyOnSortIndexChangedListener;
import com.bairock.hamadev.communication.MyOnStateChangedListener;
import com.bairock.hamadev.communication.MyOnUnitSymbolChangedListener;
import com.bairock.hamadev.database.Config;
import com.bairock.hamadev.database.DevGroupDao;
import com.bairock.hamadev.database.DeviceDao;
import com.bairock.hamadev.database.SdDbHelper;
import com.bairock.hamadev.database.UserDao;
import com.bairock.hamadev.settings.UdpLogActivity;
import com.bairock.iot.intelDev.communication.DevChannelBridge;
import com.bairock.iot.intelDev.communication.DevChannelBridgeHelper;
import com.bairock.iot.intelDev.communication.DevServer;
import com.bairock.iot.intelDev.communication.FindDevHelper;
import com.bairock.iot.intelDev.communication.UdpServer;
import com.bairock.iot.intelDev.device.Coordinator;
import com.bairock.iot.intelDev.device.CtrlModel;
import com.bairock.iot.intelDev.device.DevHaveChild;
import com.bairock.iot.intelDev.device.DevStateHelper;
import com.bairock.iot.intelDev.device.Device;
import com.bairock.iot.intelDev.device.DeviceAssistent;
import com.bairock.iot.intelDev.device.MainCodeHelper;
import com.bairock.iot.intelDev.device.devcollect.CollectProperty;
import com.bairock.iot.intelDev.device.devcollect.DevCollect;
import com.bairock.iot.intelDev.device.devcollect.DevCollectClimateContainer;
import com.bairock.iot.intelDev.device.devcollect.Pressure;
import com.bairock.iot.intelDev.device.remoter.RemoterContainer;
import com.bairock.iot.intelDev.linkage.LinkageHelper;
import com.bairock.iot.intelDev.linkage.LinkageTab;
import com.bairock.iot.intelDev.linkage.guagua.GuaguaHelper;
import com.bairock.iot.intelDev.linkage.timing.WeekHelper;
import com.bairock.iot.intelDev.user.DevGroup;
import com.bairock.iot.intelDev.user.User;
import com.tencent.tac.TACApplication;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.UUID;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        //判断是否需要登陆
        //new SharedHelper().getNeedLogin();

        WeekHelper.ARRAY_WEEKS = new String[]{"日","一","二","三","四","五","六",};

        ToMainTask toMainTask = new ToMainTask(this);
        toMainTask.execute();
    }

    public static void initUser(){
        HamaApp.USER = SdDbHelper.getDbUser();
        if(null == HamaApp.USER){
            return;
        }
        if(HamaApp.USER.getListDevGroup().isEmpty()){
            return;
        }
        HamaApp.DEV_GROUP = HamaApp.USER.getListDevGroup().get(0);

        //本地tcp网络数据监听器名称
        //调试模式下监听网络数据
        if(LogUtils.INSTANCE.getAPP_DBG()) {
            FindDevHelper.getIns().setOnSendListener(new FindDevHelper.OnSendListener() {
                @Override
                public void create() {

                }

                @Override
                public void send(String s) {
                    UdpLogActivity.addSend(s);
                }
            });

            DevChannelBridgeHelper.BRIDGE_COMMUNICATION_LISTENER_NAME = MyOnCommunicationListener.class.getName();
            DevChannelBridgeHelper.getIns().setOnBridgesChangedListener(new MyOnBridgesChangedListener());
        }

        MyOnStateChangedListener onStateChangedListener = new MyOnStateChangedListener();
        MyOnGearChangedListener onGearChangedListener = new MyOnGearChangedListener();
        MyOnCtrlModelChangedListener onCtrlModelChangedListener = new MyOnCtrlModelChangedListener();
        for (Device device : HamaApp.DEV_GROUP.getListDevice()){
            //刚打开软件开始寻找设备
            FindDevHelper.getIns().findDev(device.getCoding());
            device.setDevStateId(DevStateHelper.DS_YI_CHANG);
            setDeviceListener(device, onStateChangedListener, onGearChangedListener, onCtrlModelChangedListener);
        }

        DevChannelBridgeHelper.getIns().setUser(HamaApp.USER);

        //将状态设备添加到联动表，开始检查所有设备的联动和挡位状态
        List<Device> list = HamaApp.DEV_GROUP.findListIStateDev(true);
        LinkageTab.getIns().getListLinkageTabRow().clear();
        for(Device device : list){
            LinkageTab.getIns().addTabRow(device);
        }

        LinkageHelper.getIns().setChain(HamaApp.DEV_GROUP.getChainHolder());
        LinkageHelper.getIns().setLoop(HamaApp.DEV_GROUP.getLoopHolder());
        LinkageHelper.getIns().setTiming(HamaApp.DEV_GROUP.getTimingHolder());

        GuaguaHelper.getIns().setGuaguaHolder(HamaApp.DEV_GROUP.getGuaguaHolder());

        //GuaguaHelper.getIns().startCheckGuaguaThread();
    }

    public static void setDeviceListener(Device device, MyOnStateChangedListener onStateChangedListener,
                                         MyOnGearChangedListener onGearChangedListener,
                                         MyOnCtrlModelChangedListener onCtrlModelChangedListener){
        device.setCtrlModel(CtrlModel.UNKNOW);
        device.setOnStateChanged(onStateChangedListener);
        device.setOnGearChanged(onGearChangedListener);
        device.setOnCtrlModelChanged(onCtrlModelChangedListener);
        device.setOnSortIndexChangedListener(new MyOnSortIndexChangedListener());
        device.addOnAliasChangedListener(new MyOnAliasChangedListener());
        device.addOnNameChangedListener(new MyOnNameChangedListener());
        device.setOnGearNeedToAutoListener(new MyOnGearNeedToAutoListener());

        if(device instanceof DevHaveChild){
            DevHaveChild devHaveChild = (DevHaveChild)device;
            //协调器添加子设备集合改变监听器
            if(devHaveChild instanceof Coordinator){
                devHaveChild.addOnDeviceCollectionChangedListener(new MyOnDevHaveChildeOnCollectionChangedListener());
            }
            for(Device device1 : devHaveChild.getListDev()){
                setDeviceListener(device1, onStateChangedListener, onGearChangedListener, onCtrlModelChangedListener);
            }
        }
        if(device instanceof DevCollect){
            CollectProperty cp = ((DevCollect) device).getCollectProperty();
            cp.setOnCurrentValueChanged(new MyOnCurrentValueChangedListener());
            cp.setOnSignalSourceChangedListener(new MyOnSignalSourceChangedListener());
            cp.setOnSimulatorChangedListener(new MyOnSimulatorChangedListener());
            cp.setOnUnitSymbolChangedListener(new MyOnUnitSymbolChangedListener());
            cp.setOnValueTriggedListener(new MyOnValueTriggedListener());
        }
    }

    private static void testDeviceBx(){
        User user = new User();
        user.setName("test123");
        user.setPsd("a123456");
        UserDao userDao = UserDao.get(HamaApp.HAMA_CONTEXT);
        userDao.clean();
        userDao.addUser(user);

        DevGroup devGroup = new DevGroup("1", "a123", "g1");
        devGroup.setId(UUID.randomUUID().toString());
        user.addGroup(devGroup);
        DevGroupDao devGroupDao = DevGroupDao.get(HamaApp.HAMA_CONTEXT);
        devGroupDao.clean();
        devGroupDao.add(devGroup);

        Device device = DeviceAssistent.createDeviceByMcId(MainCodeHelper.KG_XLU_2TAI, "0001");
        devGroup.addDevice(device);

        DeviceDao deviceDao = DeviceDao.get(HamaApp.HAMA_CONTEXT);
        deviceDao.clean();
        deviceDao.add(device);

        SdDbHelper.replaceDbUser(user);
    }

    private static void testDevice(){
        User user = new User();
        user.setName("test123");
        user.setPsd("a123456");
        UserDao userDao = UserDao.get(HamaApp.HAMA_CONTEXT);
        userDao.clean();
        userDao.addUser(user);

        DevGroup devGroup = new DevGroup("1", "a123", "g1");
        devGroup.setId(UUID.randomUUID().toString());
        user.addGroup(devGroup);
        DevGroupDao devGroupDao = DevGroupDao.get(HamaApp.HAMA_CONTEXT);
        devGroupDao.clean();
        devGroupDao.add(devGroup);

//        Coordinator coordinator = (Coordinator)DeviceAssistent.createDeviceByMcId(MainCodeHelper.XIE_TIAO_QI, "9999");
//        DevCollectSignal devCollectSignal = (DevCollectSignal) DeviceAssistent.createDeviceByMcId(MainCodeHelper.COLLECTOR_SIGNAL, "9999");
//        devCollectSignal.getCollectProperty().setCollectSrc(CollectSignalSource.DIGIT);
//        DevCollectSignal devCollectSignal2 = (DevCollectSignal) DeviceAssistent.createDeviceByMcId(MainCodeHelper.COLLECTOR_SIGNAL, "9998");
//        devCollectSignal2.getCollectProperty().setCollectSrc(CollectSignalSource.ELECTRIC_CURRENT);
//        DevCollectSignal devCollectSignal3 = (DevCollectSignal) DeviceAssistent.createDeviceByMcId(MainCodeHelper.COLLECTOR_SIGNAL, "9997");
//        devCollectSignal3.getCollectProperty().setCollectSrc(CollectSignalSource.VOLTAGE);
//        coordinator.addChildDev(devCollectSignal);
//        coordinator.addChildDev(devCollectSignal2);
//        coordinator.addChildDev(devCollectSignal3);

        Device device = DeviceAssistent.createDeviceByMcId(MainCodeHelper.KG_3LU_2TAI, "9999");
        devGroup.addDevice(device);

        Device device1 = DeviceAssistent.createDeviceByMcId(MainCodeHelper.COLLECTOR_CLIMATE_CONTAINER, "9999");
        devGroup.addDevice(device1);

//        DevCollectSignalContainer devCollectSignalContainer = (DevCollectSignalContainer)DeviceAssistent.createDeviceByMcId(MainCodeHelper.COLLECTOR_SIGNAL_CONTAINER, "9996");
//        devGroup.addDevice(devCollectSignalContainer);

//        GuaguaMouth guaguaMouth = (GuaguaMouth)DeviceAssistent.createDeviceByMcId(MainCodeHelper.GUAGUA_MOUTH, "9999");
//        devGroup.addDevice(guaguaMouth);
//        DevCollectSignal devCollectSignal4 = (DevCollectSignal)DeviceAssistent.createDeviceByMcId(MainCodeHelper.COLLECTOR_SIGNAL, "9996");
//        devCollectSignal4.getCollectProperty().setCollectSrc(CollectSignalSource.SWITCH);
//        devGroup.addDevice(devCollectSignal4);

        SdDbHelper.replaceDbUser(user);
    }

    private static void testCoordinator(){
        User user = new User();
        user.setName("test123");
        user.setPsd("a123456");
        UserDao userDao = UserDao.get(HamaApp.HAMA_CONTEXT);
        userDao.clean();
        userDao.addUser(user);

        DevGroup devGroup = new DevGroup("1", "a123", "g1");
        devGroup.setId(UUID.randomUUID().toString());
        user.addGroup(devGroup);
        DevGroupDao devGroupDao = DevGroupDao.get(HamaApp.HAMA_CONTEXT);
        devGroupDao.clean();
        devGroupDao.add(devGroup);

        Coordinator coordinator = (Coordinator)DeviceAssistent.createDeviceByMcId(MainCodeHelper.XIE_TIAO_QI, "9999");
        Pressure pressure = (Pressure)DeviceAssistent.createDeviceByMcId(MainCodeHelper.YE_WEI, "9999");

        DevCollectClimateContainer climateContainer = (DevCollectClimateContainer)DeviceAssistent.createDeviceByMcId(MainCodeHelper.COLLECTOR_CLIMATE_CONTAINER, "9999");

        coordinator.addChildDev(pressure);
        coordinator.addChildDev(climateContainer);
        devGroup.addDevice(coordinator);

        SdDbHelper.replaceDbUser(user);
    }

    private static void testRemoterContainer(){
        User user = new User();
        user.setName("test123");
        user.setPsd("a123456");
        UserDao userDao = UserDao.get(HamaApp.HAMA_CONTEXT);
        userDao.clean();
        userDao.addUser(user);

        DevGroup devGroup = new DevGroup("1", "a123", "g1");
        devGroup.setId(UUID.randomUUID().toString());
        user.addGroup(devGroup);
        DevGroupDao devGroupDao = DevGroupDao.get(HamaApp.HAMA_CONTEXT);
        devGroupDao.clean();
        devGroupDao.add(devGroup);

        RemoterContainer remoterContainer = (RemoterContainer)DeviceAssistent.createDeviceByMcId(MainCodeHelper.YAO_KONG, "9999");

        devGroup.addDevice(remoterContainer);

        SdDbHelper.replaceDbUser(user);
    }

    private static class ToMainTask extends AsyncTask<Void, Void, Boolean> {

        WeakReference<WelcomeActivity> mActivity;

        ToMainTask(WelcomeActivity activity) {
            mActivity = new WeakReference<>(activity);
        }
        @Override
        protected Boolean doInBackground(Void... params) {
            //try {

                //获取屏幕宽高
                DisplayMetrics displayMetrics = new DisplayMetrics();
                mActivity.get().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                Constant.INSTANCE.setDisplayWidth(displayMetrics.widthPixels);
                Constant.INSTANCE.setDisplayHeight(displayMetrics.heightPixels);

                //没有可搜索设备时单机测试用
//                testDevice();
//                testDeviceBx();
//                testCoordinator();
//                testRemoterContainer();
                initUser();

                UdpServer.getIns().setUser(HamaApp.USER);
                UdpServer.getIns().run();

                Media.INSTANCE.init(mActivity.get());
                Config.INSTANCE.init(mActivity.get());

                //设置宫格/列表切换监听
                Config.INSTANCE.setOnDevShowStyleChangedListener(style -> {
                    if(null != ElectricalCtrlFragment.handler){
                        ElectricalCtrlFragment.handler.obtainMessage(ElectricalCtrlFragment.CHANGE_LAYOUT_MANAGER).sendToTarget();
                    }
                    if(null != ClimateFragment.handler){
                        ClimateFragment.handler.obtainMessage(ClimateFragment.CHANGE_LAYOUT_MANAGER).sendToTarget();
                    }
                });
                //设置名称/位号切换监听
                Config.INSTANCE.setOnDevNameShowStyleChangedListener(name -> {
                    if(null != ElectricalCtrlFragment.handler){
                        ElectricalCtrlFragment.handler.obtainMessage(ElectricalCtrlFragment.CHANGE_SHOW_NAME_STYLE).sendToTarget();
                    }
                    if(null != ClimateFragment.handler){
                        ClimateFragment.handler.obtainMessage(ClimateFragment.CHANGE_SHOW_NAME_STYLE).sendToTarget();
                    }
                });

                try {
                    HamaApp.DEV_SERVER = new DevServer();
                    HamaApp.DEV_SERVER.run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                DevChannelBridge.analysiserName = MyMessageAnalysiser.class.getName();
                DevChannelBridgeHelper.getIns().stopSeekDeviceOnLineThread();
                DevChannelBridgeHelper.getIns().startSeekDeviceOnLineThread();
                DevChannelBridgeHelper.getIns().setOnHeartSendListener(new ChannelBridgeHelperHeartSendListener());

                LinkageTab.getIns().SetOnOrderSendListener((device, order, ctrlModel) -> {
                    //Log.e("WelcomeAct", "OnOrderSendListener " + "order: " + order + " cm: " + ctrlModel);
                    if(null != order) {
                        HamaApp.sendOrder(device, order, false);
                    }
                });

                LinkageHelper.getIns().stopCheckLinkageThread();
                LinkageHelper.getIns().startCheckLinkageThread();
                GuaguaHelper.getIns().stopCheckGuaguaThread();
                GuaguaHelper.getIns().startCheckGuaguaThread();
                GuaguaHelper.getIns().setOnOrderSendListener((guagua, s, ctrlModel) -> HamaApp.sendOrder(guagua.findSuperParent(), s, true));
                return true;
//            }catch (Exception e){
//                e.printStackTrace();
//                return false;
//            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            WelcomeActivity theActivity = mActivity.get();
            if (success) {
                if(Config.INSTANCE.getNeedLogin() || HamaApp.USER == null || HamaApp.USER.getName() == null) {
                    theActivity.startActivity(new Intent(theActivity, LoginActivity.class));
                }else{
                    MainActivity.IS_ADMIN = HamaApp.USER.getName().equals("admin");
                    String userId = HamaApp.getGroupTag();
                    TACApplication.bindUserId(userId);
                    //HamaApp.setTokenTag();
                    theActivity.startActivity(new Intent(theActivity, MainActivity.class));
                }
                theActivity.finish();
            }else {
                theActivity.finish();
            }
        }

        @Override
        protected void onCancelled() {

        }
    }
}
