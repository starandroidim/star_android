package com.jjpicture.star_android.im.util;

import android.os.Handler;

import com.jjpicture.mvvmstar.im_common.request.LoginReq;
import com.jjpicture.mvvmstar.im_common.request.LogoutReq;
import com.jjpicture.mvvmstar.im_common.response.ServerInfoRes;
import com.jjpicture.mvvmstar.utils.KLog;
import com.jjpicture.star_android.im.client.IMClient;
import com.jjpicture.star_android.im.config.UserConfig;
import com.jjpicture.star_android.im.service.impl.RouteServiceImpl;

import java.io.IOException;


/**
 * @author lyl
 * @Title: ReConnectJob
 * @Description: 掉线重连
 * @date 2019/3/719:26
 */
public class ReConnectJob implements Runnable{

    private Handler handler;

    public ReConnectJob(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void run() {
        //先登出
        LogoutReq req = new LogoutReq();

        RouteServiceImpl routeService = RouteServiceImpl.getInstance();
        KLog.d("1:先看routeservice是否有服务器信息：",routeService.getServerInfo());
        req.setUserId(UserConfig.getUserId());
        req.setUserName(UserConfig.getUserName());
        try {
            routeService.logout(req);
        }catch (IOException e){
            e.printStackTrace();
        }
        KLog.d("看下对不对：",IMClient.getInstance().getImServer());
        LoginReq loginReq = new LoginReq();
        loginReq.setUserName(UserConfig.getUserName());
        loginReq.setUserId(UserConfig.getUserId());
        try {
            KLog.i("usname",loginReq.getUserName());
            KLog.i("uid",loginReq.getUserId());
            ServerInfoRes serverInfoRes = routeService.loginAndGetServer(loginReq);
            KLog.d("再看下对不对！！！！！！！！！！！：",serverInfoRes);
            IMClient.getInstance().start(serverInfoRes,handler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
