package com.jjpicture.mvvmstar.im_common.decoder;

import com.google.protobuf.MessageLite;
import com.jjpicture.mvvmstar.im_common.protocol.ChatMessageProto;
import com.jjpicture.mvvmstar.im_common.protocol.HeartBeatProto;
import com.jjpicture.mvvmstar.im_common.protocol.LogRequestProto;
import com.jjpicture.mvvmstar.im_common.protocol.MessageResponseProto;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author lyl
 * @Title: CustomProtobufDecoder
 * @Description: 自定义解码器
 * @date 2019/3/618:52
 */
public class CustomProtobufDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        while (in.readableBytes() > 4) { // 如果可读长度小于包头长度，退出。
            in.markReaderIndex();

            // 获取包头中的body长度
            byte low = in.readByte();
            byte high = in.readByte();
            short s0 = (short) (low & 0xff);
            short s1 = (short) (high & 0xff);
            s1 <<= 8;
            short length = (short) (s0 | s1);

            // 获取包头中的protobuf类型
            in.readByte();
            byte dataType = in.readByte();

            // 如果可读长度小于body长度，恢复读指针，退出。
            if (in.readableBytes() < length) {
                in.resetReaderIndex();
                return;
            }


            // 读取body
            ByteBuf bodyByteBuf = in.readBytes(length);

            try {
                byte[] array;
                int offset;

                int readableLen= bodyByteBuf.readableBytes();
                if (bodyByteBuf.hasArray()) {
                    array = bodyByteBuf.array();
                    offset = bodyByteBuf.arrayOffset() + bodyByteBuf.readerIndex();
                } else {
                    array = new byte[readableLen];
                    bodyByteBuf.getBytes(bodyByteBuf.readerIndex(), array, 0, readableLen);
                    offset = 0;
                }

                //反序列化
                MessageLite result = decodeBody(dataType, array, offset, readableLen);
                out.add(result);
            } finally {
                bodyByteBuf.release();
            }
        }
    }

    public MessageLite decodeBody(byte dataType, byte[] array, int offset, int length) throws Exception {
        if (dataType == 0x00) {
            return ChatMessageProto.ChatMessageProtocol.getDefaultInstance().
                    getParserForType().parseFrom(array, offset, length);

        } else if (dataType == 0x01) {
            return HeartBeatProto.HeartBeatProtocol.getDefaultInstance().
                    getParserForType().parseFrom(array, offset, length);
        } else if (dataType == 0x02) {
            return LogRequestProto.LogRequestProtocol.getDefaultInstance().
                    getParserForType().parseFrom(array, offset ,length);
        } else if (dataType == 0x03) {
            return MessageResponseProto.MessageResponseProtocol.getDefaultInstance().
                    getParserForType().parseFrom(array, offset ,length);
        }

        return null; // or throw exception
    }
}
