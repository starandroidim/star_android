package com.jjpicture.star_android.im.handler;

import com.jjpicture.mvvmstar.utils.KLog;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

public class IMIdleStateHandler extends IdleStateHandler {
    private static final int READER_IDLE_TIME = 15;


    public IMIdleStateHandler() {
        super(READER_IDLE_TIME, 0, 0, TimeUnit.SECONDS);
    }

    @Override
    protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) {
        KLog.i("{}秒内未读到数据，关闭连接",READER_IDLE_TIME);
        ctx.channel().close();
    }
}
