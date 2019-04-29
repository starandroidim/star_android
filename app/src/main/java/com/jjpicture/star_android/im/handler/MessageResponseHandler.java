package com.jjpicture.star_android.im.handler;

import android.os.Handler;
import android.os.Message;

import com.jjpicture.mvvmstar.im_common.protocol.MessageResponseProto;
import com.jjpicture.mvvmstar.utils.KLog;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;


/**
 * @author lyl
 * @Title: MessageResponseHandler
 * @Description: 客户端消息响应
 * @date 2019/3/14 16:30
 */
public class MessageResponseHandler extends SimpleChannelInboundHandler<MessageResponseProto.MessageResponseProtocol> {

    final Handler handler;

    public MessageResponseHandler(Handler handler) {
        this.handler = handler;
    }
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageResponseProto.MessageResponseProtocol msg) {
        KLog.i("服务端已收到消息【{}】", msg.getMsgId());
        //Message message = handler.obtainMessage(0);// 硬编码
        //message.obj = msg;
        //message.sendToTarget();

    }
}
