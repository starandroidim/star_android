package com.jjpicture.mvvmstar.im_common.util;


import com.jjpicture.mvvmstar.im_common.protocol.LogRequestProto;

/**
 * @author Administrator
 * @Title: LogRequestFactory
 * @Description: 登录/登出请求工厂
 * @date 2019/3/610:46
 */
public class LogRequestFactory {
    public static LogRequestProto.LogRequestProtocol buildLogRequest(Long userId, int type){
        LogRequestProto.LogRequestProtocol logRequestProtocol = LogRequestProto.LogRequestProtocol.newBuilder()
                .setFrom(userId)
                .setType(type)
                .setTimestamp(System.currentTimeMillis())
                .build();
        return  logRequestProtocol;
    }
}
