package com.jjpicture.star_android.im.clientHolder;



import android.os.Handler;

import com.jjpicture.mvvmstar.im_common.response.ServerInfoRes;
import com.jjpicture.star_android.im.client.IMClient;
import com.jjpicture.star_android.im.model.TestChatModel;
import java.io.IOException;

public class ClientHolder {
    private IMClient client;

    private volatile static ClientHolder INSTANCE = null;

    public static ClientHolder getInstance(IMClient imClient) {
        if (INSTANCE == null) {
            synchronized (ClientHolder.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ClientHolder(imClient);
                }
            }
        }
        return INSTANCE;
    }

    public ClientHolder(IMClient client) {
        this.client = client;
    }

    /**
     * 客户端发送消息给指定客户端  中间再经过sendmsg 由服务端发送给指定客户端
     * 调用sendp2pmsg
     */
    public void sendToClient(TestChatModel testChatModel) throws IOException {
        // client.connect(bootstrap, HOST, PORT, MAX_RETRY);
        //client.loginSpecificServer();
        // client.start();
        client.sendP2P(testChatModel);

    }

    public void start(ServerInfoRes imServer,Handler handler) {
        client.start(imServer, handler);
    }
}
