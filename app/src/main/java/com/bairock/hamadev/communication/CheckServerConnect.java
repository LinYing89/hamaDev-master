package com.bairock.hamadev.communication;

import com.bairock.hamadev.app.HamaApp;
import com.bairock.hamadev.app.MainActivity;
import com.bairock.hamadev.database.Config;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

/**
 * 检查连接服务器状态
 * Created by 44489 on 2017/12/29.
 */

public class CheckServerConnect extends Thread {

    public static boolean running;

    @Override
    public void run() {
        while(running && !isInterrupted()){
            try {
                if(!MainActivity.IS_ADMIN) {
                    if (!PadClient.getIns().isLinked()) {
                        PadClient.getIns().link();
                    }
                    if (PadClient.getIns().isLinked()) {
                        //获取port
                        getPadPort();
                    }
                }
                sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void getPadPort(){
        String s = MyHttpRequest.sendGet(HamaApp.getPortUrl(),null);
        ObjectMapper mapper = new ObjectMapper();
        try {
            Map map = mapper.readValue(s, Map.class);
            int padPort = (int)map.get("padPort");
            int devPort = (int)map.get("devPort");
            int upDownloadPort = (int)map.get("upDownloadPort");
            if(padPort != Config.INSTANCE.getServerPadPort()){
                Config.INSTANCE.setServerPadPort(padPort);
                if (!PadClient.getIns().isLinked()) {
                    PadClient.getIns().link();
                }
                Config.INSTANCE.setServerDevPort((int)map.get("devPort"));
                Config.INSTANCE.setServerUpDownloadPort((int)map.get("upDownloadPort"));
            }
            if(padPort!= Config.INSTANCE.getServerPadPort() || devPort != Config.INSTANCE.getServerDevPort()
                    || upDownloadPort != Config.INSTANCE.getServerUpDownloadPort()){
                Config.INSTANCE.setServerInfo(HamaApp.HAMA_CONTEXT);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
