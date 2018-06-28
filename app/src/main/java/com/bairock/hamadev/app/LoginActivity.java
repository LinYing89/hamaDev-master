package com.bairock.hamadev.app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.bairock.hamadev.R;
import com.bairock.hamadev.communication.MyHttpRequest;
import com.bairock.hamadev.database.Config;
import com.bairock.hamadev.database.DevGroupDao;
import com.bairock.hamadev.database.LinkageHolderDao;
import com.bairock.hamadev.database.SdDbHelper;
import com.bairock.hamadev.database.UserDao;
import com.bairock.iot.intelDev.communication.UdpServer;
import com.bairock.iot.intelDev.device.DevHaveChild;
import com.bairock.iot.intelDev.device.Device;
import com.bairock.iot.intelDev.device.devcollect.DevCollect;
import com.bairock.iot.intelDev.device.remoter.Remoter;
import com.bairock.iot.intelDev.device.remoter.RemoterKey;
import com.bairock.iot.intelDev.linkage.ChainHolder;
import com.bairock.iot.intelDev.linkage.Effect;
import com.bairock.iot.intelDev.linkage.Linkage;
import com.bairock.iot.intelDev.linkage.LinkageCondition;
import com.bairock.iot.intelDev.linkage.LinkageHolder;
import com.bairock.iot.intelDev.linkage.SubChain;
import com.bairock.iot.intelDev.linkage.guagua.GuaguaHolder;
import com.bairock.iot.intelDev.linkage.loop.LoopDuration;
import com.bairock.iot.intelDev.linkage.loop.LoopHolder;
import com.bairock.iot.intelDev.linkage.loop.ZLoop;
import com.bairock.iot.intelDev.linkage.timing.MyTime;
import com.bairock.iot.intelDev.linkage.timing.Timing;
import com.bairock.iot.intelDev.linkage.timing.TimingHolder;
import com.bairock.iot.intelDev.linkage.timing.ZTimer;
import com.bairock.iot.intelDev.user.DevGroup;
import com.bairock.iot.intelDev.user.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tencent.tac.TACApplication;

import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

public class LoginActivity extends AppCompatActivity {

    private EditText etUserName;
    private EditText etGroupName;
    private EditText etGroupPsd;

    private Button btnLogin;
    private Button btnLoginOffline;

    private ProgressDialog progressDialog;

    private UserLoginTask userLoginTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findViews();
        setListener();
    }

    private void findViews(){
        etUserName = findViewById(R.id.etUserName);
        etGroupName = findViewById(R.id.etGroupName);
        etGroupPsd = findViewById(R.id.etGroupPsd);
        btnLogin = findViewById(R.id.btnLogin);
        btnLoginOffline = findViewById(R.id.btnLoginOffline);

        String userName = "";
        if(null != HamaApp.USER){
            userName = HamaApp.USER.getName();
        }
        etUserName.setText(userName);

        String groupName = "";
        if(null != HamaApp.DEV_GROUP){
            groupName = HamaApp.DEV_GROUP.getName();
        }
        etGroupName.setText(groupName);
    }

    private void setListener(){
        btnLogin.setOnClickListener(onClickListener);
        btnLoginOffline.setOnClickListener(onClickListener);
    }

    private View.OnClickListener onClickListener = v -> {
        switch (v.getId()){
            case R.id.btnLogin:
                showProgress(true);
                userLoginTask = new UserLoginTask(LoginActivity.this, etUserName.getText().toString(),
                        etGroupName.getText().toString(), etGroupPsd.getText().toString());
                userLoginTask.execute();
                break;
            case R.id.btnLoginOffline:
                User user = new User();
                user.setId(1L);
                user.setName("admin");
                user.setPsd("admin123");
                user.setPetName("admin");
                user.setRegisterTime(new Date());
                user.setTel("888");
                DevGroup group = new DevGroup();
                group.setName("g1");
                group.setPsd("a123");
                group.setPetName("g1");
                user.addGroup(group);

                if(HamaApp.USER == null) {
                    HamaApp.USER = user;
                    UserDao userDao = UserDao.get(LoginActivity.this);
                    userDao.addUser(user);
                }else{
                    HamaApp.USER.setName(user.getName());
                    HamaApp.USER.setPsd(user.getPsd());
                    HamaApp.USER.setPetName(user.getPetName());
                    HamaApp.USER.setRegisterTime(user.getRegisterTime());
                    HamaApp.USER.setTel(user.getTel());
                    UserDao userDao = UserDao.get(LoginActivity.this);
                    userDao.updateUser(HamaApp.USER);
                }

                if(null == HamaApp.DEV_GROUP) {
                    HamaApp.DEV_GROUP = group;
                    DevGroupDao devGroupDao = DevGroupDao.get(LoginActivity.this);
                    devGroupDao.add(group);

                }else{
                    HamaApp.DEV_GROUP.setName(group.getName());
                    HamaApp.DEV_GROUP.setPsd(group.getPsd());
                    HamaApp.DEV_GROUP.setPetName(user.getPetName());
                    DevGroupDao devGroupDao = DevGroupDao.get(LoginActivity.this);
                    devGroupDao.update(HamaApp.DEV_GROUP);
                }
                if(HamaApp.DEV_GROUP.getListLinkageHolder().isEmpty()){
                    initLinkageHolder();
                }
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
                break;
        }
    };


    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    private static class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        WeakReference<LoginActivity> mActivity;

        private final String strName;
        private final String mGroup;
        private final String mPassword;

        UserLoginTask(LoginActivity activity, String userame,String group, String password) {
            mActivity = new WeakReference<>(activity);
            strName = userame;
            mGroup = group;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            LoginActivity activity = mActivity.get();
            boolean monitor = activity.isMonitor(strName, mGroup , mPassword);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                return false;
            }
            return monitor;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            LoginActivity activity = mActivity.get();
            activity.userLoginTask = null;
            activity.showProgress(false);

            if (success) {
                activity.startActivity(new Intent(activity, MainActivity.class));
                activity.finish();
            } else {
                Snackbar.make(activity.getWindow().getDecorView(), "登陆超时", Snackbar.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            LoginActivity activity = mActivity.get();
            activity.userLoginTask = null;
            activity.showProgress(false);
        }
    }

    private void showProgress(final boolean show) {
        if(show){
            if(null == progressDialog){
                progressDialog = new ProgressDialog(LoginActivity.this);
            }
            progressDialog.setMessage("正在登陆，请稍等...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }else{
            if(null != progressDialog){
                progressDialog.dismiss();
                progressDialog = null;
            }
        }
    }

    private boolean isMonitor(String username, String group, String psd){
        try {
            //发送 GET 请求
            String s = MyHttpRequest.sendGet(HamaApp.getLoginUrl(),
                    "name=" + username + "&group=" + group + "&psd=" + psd);
            Log.e("LoginActivity getUrl: ", HamaApp.getLoginUrl() + "?name=" + username + "&group=" + group + "&psd=" + psd);
            //System.out.println("get url: " + url);
            Log.e("LoginActivity get: ", s);
            ObjectMapper mapper = new ObjectMapper();
            Map map = mapper.readValue(s, Map.class);
            if((int)(map.get("stateCode")) == 200){
                Config.INSTANCE.setServerPadPort((int)map.get("padPort"));
                Config.INSTANCE.setServerDevPort((int)map.get("devPort"));
                Config.INSTANCE.setServerUpDownloadPort((int)map.get("upDownloadPort"));

                Config.INSTANCE.setServerInfo(this);

                String petName = (String)map.get("petName");

                boolean add = null == HamaApp.USER;
                if(add){
                    HamaApp.USER = new User();
                    HamaApp.USER.setId(1L);
                    HamaApp.DEV_GROUP = new DevGroup();
                    HamaApp.USER.addGroup(HamaApp.DEV_GROUP);
                    UdpServer.getIns().setUser(HamaApp.USER);
                }
                String oldUserName = HamaApp.USER.getName();
                String oldGroupName = HamaApp.DEV_GROUP.getName();
                String oldPsd = HamaApp.DEV_GROUP.getPsd();
                HamaApp.USER.setName(username);
                HamaApp.DEV_GROUP.setName(group);
                HamaApp.DEV_GROUP.setPsd(psd);
                HamaApp.DEV_GROUP.setPetName(petName);
                UserDao userDao = UserDao.get(LoginActivity.this);
                DevGroupDao devGroupDao = DevGroupDao.get(LoginActivity.this);
                if(add){
                    userDao.addUser(HamaApp.USER);
                    devGroupDao.add(HamaApp.DEV_GROUP);
                    initLinkageHolder();

                }else {
                    userDao.updateUser(HamaApp.USER);
                    devGroupDao.update(HamaApp.DEV_GROUP);
                    if(!username.equals(oldUserName) || !group.equals(oldGroupName)
                            || !psd.equals(oldPsd)){
                        //重新生成设备id
                        refreshDbId();
                    }
                }

                if(HamaApp.DEV_GROUP.getListLinkageHolder().isEmpty()){
                    initLinkageHolder();
                }

                //MainActivity.FIRST_LOGIN = true;
                MainActivity.IS_ADMIN = false;
                Config.INSTANCE.setNeedLogin(this, false);

                String userId = HamaApp.USER.getName() + "_" +HamaApp.DEV_GROUP.getName();
                TACApplication.bindUserId(userId);
                //HamaApp.setTokenTag();

                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    private void initLinkageHolder(){
        //初始化四个连锁根目录
        ChainHolder chainHolder = new ChainHolder();
        chainHolder.setId(UUID.randomUUID().toString());
        HamaApp.DEV_GROUP.setChainHolder(chainHolder);
        LinkageHolderDao.get(HamaApp.HAMA_CONTEXT).add(chainHolder);
        TimingHolder timingHolder = new TimingHolder();
        timingHolder.setId(UUID.randomUUID().toString());
        HamaApp.DEV_GROUP.setTimingHolder(timingHolder);
        LinkageHolderDao.get(HamaApp.HAMA_CONTEXT).add(timingHolder);
        LoopHolder loopHolder = new LoopHolder();
        loopHolder.setId(UUID.randomUUID().toString());
        HamaApp.DEV_GROUP.setLoopHolder(loopHolder);
        LinkageHolderDao.get(HamaApp.HAMA_CONTEXT).add(loopHolder);
        GuaguaHolder guaguaHolder = new GuaguaHolder();
        guaguaHolder.setId(UUID.randomUUID().toString());
        HamaApp.DEV_GROUP.setGuaguaHolder(guaguaHolder);
        LinkageHolderDao.get(HamaApp.HAMA_CONTEXT).add(guaguaHolder);
    }

    private void refreshDbId(){
        for(Device device : HamaApp.DEV_GROUP.getListDevice()){
            setDeviceId(device);
        }
        for(LinkageHolder linkageHolder : HamaApp.DEV_GROUP.getListLinkageHolder()) {
            linkageHolder.setId(UUID.randomUUID().toString());
            for (Linkage linkage : linkageHolder.getListLinkage()) {
                linkage.setId(UUID.randomUUID().toString());
                for(Effect effect : linkage.getListEffect()){
                    effect.setId(UUID.randomUUID().toString());
                }
                if(linkage instanceof SubChain){
                    for(LinkageCondition linkageCondition : ((SubChain) linkage).getListCondition()){
                        linkageCondition.setId(UUID.randomUUID().toString());
                    }
                    if(linkage instanceof ZLoop){
                        for(LoopDuration loopDuration : ((ZLoop) linkage).getListLoopDuration()){
                            loopDuration.setId(UUID.randomUUID().toString());
                            for(MyTime myTime : loopDuration.getListTimes()){
                                myTime.setId(UUID.randomUUID().toString());
                            }
                        }
                    }
                }else if(linkage instanceof Timing){
                    for(ZTimer zTimer : ((Timing) linkage).getListZTimer()){
                        zTimer.setId(UUID.randomUUID().toString());
                        for(MyTime myTime : zTimer.getListTimes()){
                            myTime.setId(UUID.randomUUID().toString());
                        }
                        zTimer.getWeekHelper().setId(UUID.randomUUID().toString());
                    }
                }
            }
        }
        SdDbHelper.replaceDbUser(HamaApp.USER);
    }

    private void setDeviceId(Device device){
        device.setId(UUID.randomUUID().toString());
        if(device instanceof DevHaveChild){
            for(Device device1 : ((DevHaveChild) device).getListDev()){
                setDeviceId(device1);
            }
        }
        if(device instanceof DevCollect){
            ((DevCollect) device).getCollectProperty().setId(UUID.randomUUID().toString());
        }else if(device instanceof Remoter){
            for(RemoterKey remoterKey : ((Remoter)device).getListRemoterKey()){
                remoterKey.setId(UUID.randomUUID().toString());
            }
        }
    }

}
