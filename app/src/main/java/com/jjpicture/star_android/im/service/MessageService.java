package com.jjpicture.star_android.im.service;


import com.jjpicture.mvvmstar.im_common.protocol.ChatMessageProto;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * 发送消息的服务
 *
 *
 */
public interface MessageService {

    /**
     * 发送消息
     */
    void sendMessage(ChatMessageProto.ChatMessageProtocol message, NioSocketChannel channel, int retry);
}
