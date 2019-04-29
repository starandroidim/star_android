package com.jjpicture.mvvmstar.im_common.util;


import com.jjpicture.mvvmstar.im_common.protocol.MessageResponseProto;

/**
 * @author lyl
 * @Title: MessageResponseFactory
 * @Description: 消息响应协议工厂
 * @date 2019/3/14 16:25
 */
public class MessageResponseFactory {
    public static MessageResponseProto.MessageResponseProtocol buildMessageResponse(String messageId){
        MessageResponseProto.MessageResponseProtocol messageResponseProtocol =
                MessageResponseProto.MessageResponseProtocol.newBuilder()
                        .setMsgId(messageId)
                        .setTimestamp(System.currentTimeMillis())
                        .build();
        return  messageResponseProtocol;
    }
}
