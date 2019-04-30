package com.jjpicture.mvvmstar.im_common.util;

import com.google.protobuf.ByteString;
import com.jjpicture.mvvmstar.im_common.protocol.ChatMessageProto;

import java.util.UUID;

/**
 * @author lyl
 * @Title: ChatMessageFactory
 * @Description: 消息协议
 * @date 2019/3/610:32
 */
public class ChatMessageFactory {
    /**
     *
     * @param fromId
     * @param toId
     * @param chatType
     * @param msgType
     * @param level
     * @param body
     * @return
     */
    public static ChatMessageProto.ChatMessageProtocol buildMessage(Long fromId, Long toId, int chatType, int msgType, int level, String body){
        UUID uuid = UUID.randomUUID();
        String msgId = uuid.toString().replace("-", "").toLowerCase();
        Long timestamp = System.currentTimeMillis();

        ChatMessageProto.ChatMessageProtocol messageProtocol = ChatMessageProto.ChatMessageProtocol.newBuilder()
                .setMsgId(msgId)
                .setFromId(fromId)
                .setToId(toId)
                .setChatType(chatType)
                .setMsgType(msgType)
                .setTimestamp(timestamp)
                .setLevel(level)
                .setMsgBody(ByteString.copyFromUtf8(body))
                //可选参数
                .setExt("")
                .build();

        return messageProtocol;
    }
}
