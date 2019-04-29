package com.jjpicture.mvvmstar.im_common.protocol;

import com.google.protobuf.InvalidProtocolBufferException;


public class ProtocolUtil {


//    public static void main(String[] args) throws InvalidProtocolBufferException{
//        STARIMRequestProto.STARIMReqProtocol protocol = STARIMRequestProto.STARIMReqProtocol.newBuilder()
//                .setType(1)
//                .setRequestId(2)
//                .setReqMsg("")
//                .build();
//
//        byte[] encode = encode(protocol);
//
//        STARIMRequestProto.STARIMReqProtocol parseFrom = decode(encode);
//
//        System.out.println(protocol.toString());
//        System.out.println(protocol.toString().equals(parseFrom.toString()));
//    }
//
//    /**
//     * 编码
//     * @param protocol
//     * @return
//     */
//    public static byte[] encode(STARIMRequestProto.STARIMReqProtocol protocol){
//        return protocol.toByteArray() ;
//    }
//
//    /**
//     * 解码
//     * @param bytes
//     * @return
//     * @throws InvalidProtocolBufferException
//     */
//    public static STARIMRequestProto.STARIMReqProtocol decode(byte[] bytes) throws InvalidProtocolBufferException {
//        return STARIMRequestProto.STARIMReqProtocol.parseFrom(bytes);
//    }

}
