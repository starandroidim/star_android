package com.jjpicture.star_android.im.service.impl;


import com.jjpicture.mvvmstar.im_common.protocol.ChatMessageProto;
import com.jjpicture.mvvmstar.utils.KLog;

import com.jjpicture.star_android.im.service.MessageService;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.socket.nio.NioSocketChannel;

public class MessageServiceImpl implements MessageService {

    private volatile static MessageServiceImpl INSTANCE = null;

    public static MessageServiceImpl getInstance() {
        if (INSTANCE == null) {
            synchronized (MessageServiceImpl.class) {
                if (INSTANCE == null) {
                    INSTANCE = new MessageServiceImpl();
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public void sendMessage(ChatMessageProto.ChatMessageProtocol message, NioSocketChannel channel, int retry) {
        ChannelFuture future = channel.writeAndFlush((message));
        future.addListener((ChannelFutureListener) channelFuture ->{
            if(channelFuture.isSuccess())
                KLog.i("用户向服务器发送消息成功！");
            else if (retry == 0)
                //网络连接等原因
                KLog.i("重发次数已用完，放弃发送！");
            else {
                //重发消息
                KLog.i("消息发送失败，重新发送！");
                sendMessage(message, channel, retry-1);
            }
        });
    }
/*
    @Override
    public void getOfflineMessage() {
        //先获取离线消息总数和该用户的每个发送方发送的离线消息数以及对应的最新一条消息
        //类似 30-A:10:"拜拜"-B:5:"在吗"-C:7:"好吧"-D:8:"人呢人呢"
        //发送给route route去查询数据库
        //收到第一条汇总消息后将陆续拉取后续离线消息
//        HttpUrl.Builder hBuilder = HttpUrl.parse(routeURLConfig.getOfflineMessageURL()).newBuilder();
//        hBuilder.addQueryParameter("userId", userConfig.getUserId().toString());
//        Request request = new Request.Builder()
//                .url(hBuilder.build())
//                .get()
//                .build();
//        try {
//            Response response = okHttpClient.newCall(request).execute() ;
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }
*/
}
