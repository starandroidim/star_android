package com.jjpicture.star_android.im.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jjpicture.mvvmstar.im_common.enums.CodeEnum;
import com.jjpicture.mvvmstar.im_common.request.LoginReq;
import com.jjpicture.mvvmstar.im_common.request.LogoutReq;
import com.jjpicture.mvvmstar.im_common.response.BaseResponse;
import com.jjpicture.mvvmstar.im_common.response.ServerInfoRes;
import com.jjpicture.mvvmstar.im_common.util.HttpRequestFactory;
import com.jjpicture.mvvmstar.utils.KLog;
import com.jjpicture.star_android.im.config.RouteURLConfig;
import com.jjpicture.star_android.im.config.UserConfig;
import com.jjpicture.star_android.im.service.RouteService;

import okhttp3.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;



public class RouteServiceImpl implements RouteService {

    private volatile static RouteServiceImpl INSTANCE = null;


    public static RouteServiceImpl getInstance() {
        if (INSTANCE == null) {
            synchronized (RouteServiceImpl.class) {
                if (INSTANCE == null) {
                    INSTANCE = new RouteServiceImpl();
                }
            }
        }
        return INSTANCE;
    }

    private OkHttpClient okHttpClient = new OkHttpClient();
    private MediaType mediaType = MediaType.parse("application/json");
    private  String serverInfo;

    public String getServerInfo() {
        return serverInfo;
    }




    /*
    @Override
    public BaseResponse<RegisterRes> register(String userName) throws IOException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userName", userName);
        RequestBody requestBody = RequestBody.create(mediaType,jsonObject.toString());
        Request request = new Request.Builder()
                .url(RouteURLConfig.getRegisterUrl())
                .post(requestBody)
                .build();
        Response response = okHttpClient.newCall(request).execute() ;

        if (!response.isSuccessful()){
            throw new IOException("Unexpected code " + response);
        }

        ResponseBody body = response.body();
//        RegisterInfoRes registerInfoRes;
        BaseResponse baseResponse;
        try {
            String json = body.string();

            baseResponse = JSON.parseObject(json, BaseResponse.class);

            if (!baseResponse.getCode().equals(CodeEnum.SUCCESS.getCode())) {
                KLog.e("注册失败！");
            }else{
                KLog.i("注册成功！");
                RegisterRes registerRes = JSON.parseObject(baseResponse.getDataBody().toString(), RegisterRes.class);
                UserConfig.setUserId(registerRes.getUserId());
                UserConfig.setUserName(registerRes.getUserName());
            }
        } finally {
            body.close();
        }
        return baseResponse;
    }
    */

    /**
     　* @Description: 请求登录并获取服务器
     　* @param [loginReq]
     　* @return com.star.common.response.ServerInfoRes
     　* @author lyl
     　* @date 2019/3/15 16:02
     　*/
    @Override
    public ServerInfoRes loginAndGetServer(LoginReq loginReq) throws IOException {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userId", loginReq.getUserId());
        jsonObject.put("userName", loginReq.getUserName());
        RequestBody requestBody = RequestBody.create(mediaType,jsonObject.toString());
        Request request = new Request.Builder()
                .url(RouteURLConfig.getLoginURL())  // 路由服务器地址
                .post(requestBody)
                .build();
        KLog.d("JSONOBJECT",jsonObject);
        Response response = okHttpClient.newCall(request).execute() ;
        if (!response.isSuccessful()){
            throw new IOException("Unexpected code " + response);
        }
//        ServerInfoRes serverInfoRes;
        BaseResponse baseResponse;
        ResponseBody body = response.body();
        try {
            String json = body.string();
//            serverInfoRes = JSON.parseObject(json, ServerInfoRes.class);
            baseResponse = JSON.parseObject(json, BaseResponse.class);

            if(!baseResponse.getCode().equals(CodeEnum.SUCCESS.getCode())) {
                KLog.e("登陆失败");
            }
        }finally {
            body.close();
        }
        ServerInfoRes serverInfoRes = JSON.parseObject(baseResponse.getDataBody().toString(), ServerInfoRes.class);
        return serverInfoRes;
    }


    /**
     　* @Description: 向route发送请求修改登录状态和路由信息，拉取离线消息
     　* @param []
     　* @return void
     　* @author Administrator
     　* @date 2019/3/15 16:03
     　*/
    @Override
    public void saveLoginInfo() throws IOException{

//        HttpUrl.Builder hBuilder = HttpUrl.parse(routeURLConfig.getLoginURL()).newBuilder();
//        hBuilder.addQueryParameter("userId", userConfig.getUserId().toString());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userId", UserConfig.getUserId());
        jsonObject.put("serverInfo", serverInfo);
        RequestBody requestBody = RequestBody.create(mediaType,jsonObject.toString());
        Request request = new Request.Builder()
                .url(RouteURLConfig.getLoginInfoURL())
                .post(requestBody)
                .build();
        Response response = okHttpClient.newCall(request).execute() ;
        KLog.d("fuck:",response);

        BaseResponse baseResponse;
        ResponseBody body = response.body();
        String json = body.string();
        KLog.d("fuck:",json);
        /*baseResponse = JSON.parseObject(json, BaseResponse.class);
        OfflineMessageResponse offlineMessageResponse =
                JSON.parseObject(baseResponse.getDataBody().toString(), OfflineMessageResponse.class);
        for(Map.Entry<Long, List<String>> entry : offlineMessageResponse.getMap().entrySet()){
            System.out.println(entry.getKey());
            for (String s:entry.getValue())
                System.out.println(s);
        }*/
        body.close();
    }

    @Override
    public void logout(LogoutReq logoutReq) throws IOException {
        Map<String, Object> userReq = new HashMap<>();
        userReq.put("userId",logoutReq.getUserId());
        userReq.put("userName",logoutReq.getUserName());
        Request request = HttpRequestFactory.buildPost(userReq, RouteURLConfig.getLogoutURL());
        Response response = okHttpClient.newCall(request).execute() ;
        if (!response.isSuccessful()){
            throw new IOException("Unexpected code " + response);
        }
        KLog.i("登出成功");
        ResponseBody body = response.body();

    }

    @Override
    public void setServerInfo(String serverInfo) {
        this.serverInfo = serverInfo;
    }
}
