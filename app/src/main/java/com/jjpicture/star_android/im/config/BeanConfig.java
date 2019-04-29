package com.jjpicture.star_android.im.config;


import com.jjpicture.mvvmstar.im_common.protocol.HeartBeatProto;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class BeanConfig {

    public static OkHttpClient okHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true);
        return builder.build();
    }


    public static HeartBeatProto.HeartBeatProtocol heartBeat() {
        HeartBeatProto.HeartBeatProtocol heartbeat = HeartBeatProto.HeartBeatProtocol.newBuilder()
                .setId(155103219)
                .setMsg("ping")
                .build();   // 同上 之后再封装

        return heartbeat;

    }
}
