package com.jjpicture.star_android.im.handler;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;

import com.jjpicture.mvvmstar.im_common.enums.ChatType;
import com.jjpicture.mvvmstar.im_common.protocol.ChatMessageProto;
import com.jjpicture.mvvmstar.im_common.protocol.MessageResponseProto;
import com.jjpicture.mvvmstar.im_common.util.ChatMessageFactory;
import com.jjpicture.mvvmstar.im_common.util.MessageResponseFactory;
import com.jjpicture.mvvmstar.utils.KLog;
import com.jjpicture.star_android.im.config.UserConfig;

import org.json.JSONException;
import org.json.JSONObject;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author lyl
 * @Title: ChatMessageHandler
 * @Description: 聊天消息handler
 * @date 2019/3/611:24
 */
public class ChatMessageHandler extends SimpleChannelInboundHandler<ChatMessageProto.ChatMessageProtocol> {

    final Handler handler;

    public ChatMessageHandler(Handler handler) {
        this.handler = handler;
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChatMessageProto.ChatMessageProtocol msg) {
        //TODO 客户端利用messageId去重
        KLog.i("shit");
        KLog.i("收到用户【{}】发来的消息:{}", msg.getFromId() + msg.getBody());
        int type = msg.getType();
        if(type == ChatType.CHAT_SINGLE.getIndex()){

            // TODO 这个response判断是否是自己发的
            MessageResponseProto.MessageResponseProtocol response =
                    MessageResponseFactory.buildMessageResponse(msg.getMsgId());
            Message message = handler.obtainMessage(type);
            message.obj = msg;
            message.sendToTarget();


            ctx.channel().writeAndFlush(response);
        }
        else if(type == ChatType.CHAT_GROUP.getIndex()){

        }
        else if(type == ChatType.CHAT_VOICECALL.getIndex()){
            if (msg.getBody().equals("VOICE_CALL")) {
                // 是否接听
                JSONObject body = new JSONObject();
                try {
                    body.put("type", "init");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ChatMessageProto.ChatMessageProtocol messageProtocol =
                        ChatMessageFactory.buildMessage(
                                UserConfig.getUserId(),
                                msg.getFromId(),
                                type,
                                1,
                                body.toString());
                ctx.channel().writeAndFlush(messageProtocol);
            } else {
                SDPMessageHandler.INSTANCE.handle(msg);
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        KLog.e(cause.getMessage());
    }
}
