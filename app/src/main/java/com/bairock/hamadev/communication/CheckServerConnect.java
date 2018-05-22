package com.bairock.hamadev.communication;

import com.bairock.hamadev.app.HamaApp;
import com.bairock.hamadev.app.MainActivity;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;

/**
 * 检查连接服务器状态
 * Created by 44489 on 2017/12/29.
 */

public class CheckServerConnect extends Thread {

    @Override
    public void run() {
        while(true){
            try {
                if(!MainActivity.IS_ADMIN) {
                    if (!PadClient.getIns().isLinked()) {
                        PadClient.getIns().link();
                    }
                    if (!PadClient.getIns().isLinked()) {
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
            if(padPort != HamaApp.SERVER_PAD_PORT){
                HamaApp.SERVER_PAD_PORT = padPort;
                if (!PadClient.getIns().isLinked()) {
                    PadClient.getIns().link();
                }
                HamaApp.SERVER_DEV_PORT = (int)map.get("devPort");
                HamaApp.SERVER_UP_DOWNLOAD_PORT = (int)map.get("upDownloadPort");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
