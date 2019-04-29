package com.jjpicture.mvvmstar.im_common.encoder;

import com.google.protobuf.MessageLite;
import com.jjpicture.mvvmstar.im_common.protocol.ChatMessageProto;
import com.jjpicture.mvvmstar.im_common.protocol.HeartBeatProto;
import com.jjpicture.mvvmstar.im_common.protocol.LogRequestProto;
import com.jjpicture.mvvmstar.im_common.protocol.MessageResponseProto;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author lyl
 * @Title: CustomProtobufEncoder
 * @Description: 自定义编码器
 * @date 2019/3/617:25
 */
@Sharable
public class CustomProtobufEncoder extends MessageToByteEncoder<MessageLite> {

    @Override
    protected void encode(ChannelHandlerContext ctx, MessageLite msg, ByteBuf out) throws Exception {

        byte[] body = msg.toByteArray();
        byte[] header = encodeHeader(msg, (short)body.length);

        out.writeBytes(header);
        out.writeBytes(body);

        return;
    }

    private byte[] encodeHeader(MessageLite msg, short bodyLength) {
        byte messageType = 0x0f;

        if (msg instanceof ChatMessageProto.ChatMessageProtocol) {
            messageType = 0x00;
        } else if (msg instanceof HeartBeatProto.HeartBeatProtocol) {
            messageType = 0x01;
        } else if (msg instanceof LogRequestProto.LogRequestProtocol)
            messageType = 0x02;
        else if (msg instanceof MessageResponseProto.MessageResponseProtocol)
            messageType = 0x03;

        byte[] header = new byte[4];
        header[0] = (byte) (bodyLength & 0xff);
        header[1] = (byte) ((bodyLength >> 8) & 0xff);
        header[2] = 0; // 保留字段
        header[3] = messageType;

        return header;

    }
}