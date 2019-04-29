package com.jjpicture.star_android.im.handler;

import android.os.Handler;

import com.jjpicture.mvvmstar.im_common.constant.Attributes;
import com.jjpicture.mvvmstar.im_common.enums.LogType;
import com.jjpicture.mvvmstar.im_common.protocol.LogRequestProto;
import com.jjpicture.mvvmstar.im_common.response.ServerInfoRes;
import com.jjpicture.mvvmstar.im_common.util.LogRequestFactory;
import com.jjpicture.mvvmstar.utils.KLog;
import com.jjpicture.star_android.im.config.UserConfig;

import com.jjpicture.star_android.im.service.impl.RouteServiceImpl;
import com.jjpicture.star_android.im.util.ReConnectJob;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.Attribute;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author lyl
 * @Title: LogRequestHandler
 * @Description: 登录登出请求
 * @date 2019/3/13 18:36
 */
public class LogRequestHandler extends ChannelInboundHandlerAdapter {
    final ServerInfoRes serverInfo;
    final Handler handler;

    public LogRequestHandler(ServerInfoRes serverInfo,Handler handler) {
        this.serverInfo = serverInfo;
        this.handler = handler;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        KLog.i("客户端成功连接到服务端");
        //向服务端发送登录请求信息
        LogRequestProto.LogRequestProtocol login =
                LogRequestFactory.buildLogRequest(UserConfig.getUserId(), LogType.LOGIN_REQ.getIndex());
        ctx.channel().writeAndFlush(login).addListener(future -> {
            if(future.isSuccess()) {
                KLog.i("用户【{}】登录成功", login.getFrom());
            }
        });
        //向route发送请求修改登录状态和路由信息 TODO
        RouteServiceImpl routeService = RouteServiceImpl.getInstance();
        routeService.setServerInfo(serverInfo.getHost());
        new Thread(()->{
            try {
                routeService.saveLoginInfo();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        //是否客户端主动登出
        Attribute<Boolean> logout = ctx.channel().attr(Attributes.LOGOUT);
        if(null != logout.get()){
            KLog.i("用户退出登录");
            return;
        }
        //因网络或服务器原因，连接中断
        KLog.e("客户端已断开连接！尝试重连");
        //5秒后尝试重连
        ctx.executor().schedule(new ReConnectJob(handler),5, TimeUnit.SECONDS);
        super.channelInactive(ctx);
    }
}
