package com.jjpicture.star_android.im.handler;

import com.jjpicture.mvvmstar.im_common.protocol.HeartBeatProto;
import com.jjpicture.mvvmstar.utils.KLog;
import com.jjpicture.star_android.im.config.BeanConfig;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;


import java.util.concurrent.TimeUnit;

/**
 * @author ywq
 * @Title: HeartBeatHandler
 * @Description: 心跳handler
 * @date 2019/3/619:42
 */
public class HeartBeatHandler extends SimpleChannelInboundHandler<HeartBeatProto.HeartBeatProtocol> {



    private static final int HEARTBEAT_INTERVAL = 5;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        scheduleSendHeartBeat(ctx);
        super.channelActive(ctx);
    }

    private void scheduleSendHeartBeat(ChannelHandlerContext ctx) {
        ctx.executor().schedule(()-> {
            if(ctx.channel().isActive()) {
                HeartBeatProto.HeartBeatProtocol heartBeat = BeanConfig.heartBeat();
                ctx.writeAndFlush(heartBeat).addListener((ChannelFutureListener) future-> {
                    if(!future.isSuccess()) {
                        KLog.e("io error,close channel");
                        future.channel().close();
                    }
                });
                scheduleSendHeartBeat(ctx);
            }
        },HEARTBEAT_INTERVAL, TimeUnit.SECONDS);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HeartBeatProto.HeartBeatProtocol msg) {
        //KLog.i("heartbeat_recv:  "+msg);
    }

//    @Override
//    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
//        //TODO 心跳 动态
//        scheduleSendHeartBeat(ctx);
//        super.channelReadComplete(ctx);
//    }
}
