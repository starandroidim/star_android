package com.jjpicture.mvvmstar.im_common.decoder;

import com.google.protobuf.MessageLite;
import com.jjpicture.mvvmstar.im_common.protocol.ChatMessageProto;
import com.jjpicture.mvvmstar.im_common.protocol.HeartBeatProto;
import com.jjpicture.mvvmstar.im_common.protocol.LogRequestProto;
import com.jjpicture.mvvmstar.im_common.protocol.MessageResponseProto;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lyl
 * @Title: CustomProtobufDecoder
 * @Description: 自定义解码器
 * @date 2019/3/618:52
 */
public class CustomProtobufDecoder extends ByteToMessageDecoder {
    public final static CustomProtobufDecoder INSTANCE = new CustomProtobufDecoder();
    private final Map<Byte, MessageLite> map = new HashMap<>();

    private CustomProtobufDecoder() {
        map.put((byte)0, ChatMessageProto.ChatMessageProtocol.getDefaultInstance());
        map.put((byte)1, HeartBeatProto.HeartBeatProtocol.getDefaultInstance());
        map.put((byte)2, LogRequestProto.LogRequestProtocol.getDefaultInstance());
        map.put((byte)3, MessageResponseProto.MessageResponseProtocol.getDefaultInstance());
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        while (in.readableBytes() > 4) { // 如果可读长度小于包头长度，退出。
            in.markReaderIndex();

            // 获取包头中的body长度
            byte low = in.readByte();
            byte mid = in.readByte();
            byte high = in.readByte();
            int s0 = low & 0xff;
            int s1 = mid & 0xff;
            int s2 = high & 0xff;

            s1 <<= 8;
            s2 <<= 16;
            int length = s0 | s1 | s2;

            // 获取包头中的protobuf类型
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

                int readableLen = bodyByteBuf.readableBytes();
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

    private MessageLite decodeBody(byte dataType, byte[] array, int offset, int length) throws Exception {
        return map.get(dataType).getParserForType().parseFrom(array, offset, length);
    }
}
