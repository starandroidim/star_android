package com.jjpicture.star_android.im.client;



import android.os.Handler;

import com.jjpicture.mvvmstar.im_common.constant.Attributes;
import com.jjpicture.mvvmstar.im_common.decoder.CustomProtobufDecoder;
import com.jjpicture.mvvmstar.im_common.encoder.CustomProtobufEncoder;
import com.jjpicture.mvvmstar.im_common.enums.ChatType;
import com.jjpicture.mvvmstar.im_common.enums.LogType;
import com.jjpicture.mvvmstar.im_common.enums.MessageType;
import com.jjpicture.mvvmstar.im_common.protocol.ChatMessageProto;
import com.jjpicture.mvvmstar.im_common.protocol.LogRequestProto;
import com.jjpicture.mvvmstar.im_common.request.LogoutReq;
import com.jjpicture.mvvmstar.im_common.response.ServerInfoRes;
import com.jjpicture.mvvmstar.im_common.util.ChatMessageFactory;
import com.jjpicture.mvvmstar.im_common.util.LogRequestFactory;
import com.jjpicture.mvvmstar.utils.KLog;
import com.jjpicture.star_android.im.config.UserConfig;
import com.jjpicture.star_android.im.handler.ChatMessageHandler;
import com.jjpicture.star_android.im.handler.HeartBeatHandler;
import com.jjpicture.star_android.im.handler.IMIdleStateHandler;
import com.jjpicture.star_android.im.handler.LogRequestHandler;
import com.jjpicture.star_android.im.handler.MessageResponseHandler;
import com.jjpicture.star_android.im.model.TestChatModel;
import com.jjpicture.star_android.im.service.MessageService;
import com.jjpicture.star_android.im.service.RouteService;
import com.jjpicture.star_android.im.service.impl.MessageServiceImpl;
import com.jjpicture.star_android.im.service.impl.RouteServiceImpl;

import java.io.IOException;
import java.util.concurrent.TimeUnit;


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;


public class IMClient {
    private volatile static IMClient INSTANCE = null;
    private ServerInfoRes imServer;

    public ServerInfoRes getImServer() {
        return imServer;
    }

    public void setImServer(ServerInfoRes imServer) {
        this.imServer = imServer;
    }

    public static IMClient getInstance() {
        if (INSTANCE == null) {
            synchronized (IMClient.class) {
                if (INSTANCE == null) {
                    INSTANCE = new IMClient();
                }
            }
        }
        return INSTANCE;
    }

    private static NioSocketChannel channel;
    MessageService messageService = MessageServiceImpl.getInstance();
    RouteService routeService = RouteServiceImpl.getInstance();
    private final static int MAX_CONNECT_RETRY = 5;

    public void start(ServerInfoRes newIMServer,Handler handler) {
        this.imServer = newIMServer;
        this.routeService.setServerInfo(newIMServer.getHost());
        startClient(handler);
    }

    public void startClient(Handler handler) {
        NioEventLoopGroup workGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(workGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new IMIdleStateHandler())
                                .addLast("decoder",new CustomProtobufDecoder())
                                .addLast("encoder",new CustomProtobufEncoder())
                                .addLast(new LogRequestHandler(imServer,handler))
                                .addLast(new HeartBeatHandler())
                                .addLast(new ChatMessageHandler(handler))
                                .addLast(new MessageResponseHandler(handler));
                    }
                });

        KLog.i("服务器信息：{}",imServer.getHost());
//        connect(bootstrap, imServer.getIp(), imServer.getPort());
        connect(bootstrap, imServer.getIp(), imServer.getPort(), MAX_CONNECT_RETRY);
    }

    public Boolean connect(Bootstrap bootstrap, String host, int port, int retry){
        Boolean res = true;
        ChannelFuture channelfuture = bootstrap.connect(host, port).addListener(future -> {
            if(future.isSuccess()){
                KLog.i("客户端和服务端连接建立成功！");
            } else if (retry == 0){
                //清除登录状态路由信息
                LogoutReq req = new LogoutReq();
                req.setUserId(UserConfig.getUserId());
                req.setUserName(UserConfig.getUserName());
                new Thread(() -> {
                    try {
                        routeService.logout(req);
                        this.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    KLog.e("重试次数已用完，放弃连接!");
                }).start();
            } else {
                //连接失败重连
                KLog.d("连接失败，尝试重连!");
                int order = (MAX_CONNECT_RETRY - retry) + 1;
                int delay = 1 << order;
                bootstrap.config().group().schedule(()->connect(bootstrap, host, port,retry-1), delay,
                        TimeUnit.SECONDS);
            }
        });
        if(null == channelfuture) return false;
        channel = (NioSocketChannel) channelfuture.channel();
        KLog.d(channel);
        return res;
    }

    public void sendP2P(TestChatModel testChatModel) throws IOException {
        ChatMessageProto.ChatMessageProtocol chatMsg = ChatMessageFactory.buildMessage(
                UserConfig.getUserId(),
                testChatModel.getToId() ,
                ChatType.CHAT_SINGLE.getIndex(),
                MessageType.MESSAGE_TEXT.getIndex(),
                0,
                testChatModel.getMessage());
        messageService.sendMessage(chatMsg, channel, 100);
    }

    /**
     　* @Description: 登出 主动断开连接
     　* @param []
     　* @return void
     　* @author lyl
     　* @date 2019/3/5 15:12
     　*/
    public void close() {
        if(channel != null) {
            LogRequestProto.LogRequestProtocol logout =
                    LogRequestFactory.buildLogRequest(UserConfig.getUserId(), LogType.LOGOUT_REQ.getIndex());
            channel.writeAndFlush((logout));
            channel.attr(Attributes.LOGOUT).set(true);
            channel.close();
        }
//        channel.disconnect();
    }

    public static NioSocketChannel getChannel() {
        return channel;
    }
}
