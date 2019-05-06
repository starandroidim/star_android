package com.jjpicture.star_android.ui.vm;


import android.app.Application;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.Button;


import com.jjpicture.mvvmstar.base.BaseViewModel;
import com.jjpicture.mvvmstar.im_common.request.LoginReq;
import com.jjpicture.mvvmstar.im_common.request.LogoutReq;
import com.jjpicture.mvvmstar.im_common.response.BaseResponse;
import com.jjpicture.mvvmstar.im_common.response.ServerInfoRes;
import com.jjpicture.mvvmstar.utils.KLog;
import com.jjpicture.mvvmstar.utils.RxUtils;
import com.jjpicture.star_android.R;
import com.jjpicture.star_android.data.TestRepository;
import com.jjpicture.star_android.im.client.IMClient;
import com.jjpicture.star_android.im.config.UserConfig;
import com.jjpicture.star_android.im.model.TestChatModel;
import com.jjpicture.star_android.im.webrtc.WebRTCClient;
import com.jjpicture.star_android.ui.fragment.MessageFragment;


import org.webrtc.MediaStream;

import java.io.IOException;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;


public class MainViewModel extends BaseViewModel<TestRepository> {

    IMClient client = IMClient.getInstance();

    public MainViewModel(@NonNull Application application, TestRepository repository)  {
        super(application, repository);
        //logout();
        //login();
        //testSendP2P();


        //WebRTCClient webrtcClient = WebRTCClient.INSTANCE;
        //webrtcClient.setWebRTCListener(this);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    public void login() {
        LoginReq loginReq = new LoginReq();
        loginReq.setUserId(UserConfig.getUserId());
        loginReq.setUserName(UserConfig.getUserName());
        addSubscribe(model.login(loginReq)
                .compose(RxUtils.schedulersTransformer()) //线程调度
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        //showDialog();
                    }
                })
                .subscribe(new Consumer<BaseResponse<ServerInfoRes>>() {
                    @Override
                    public void accept(BaseResponse<ServerInfoRes> serverInfoResBaseResponse) throws Exception {
                        //dismissDialog();
                        //保存账号密码

                        //进入DemoActivity页面
                        KLog.d(serverInfoResBaseResponse);
                        //关闭页面
                        //finish();
                        client.start(serverInfoResBaseResponse.getDataBody(),handler);

                        Thread.sleep(5000);

                        //TODO
                        WebRTCClient.INSTANCE.start();
//                        testSendP2P();
                    }
                }));

    }

    public void testSendP2P() {
        TestChatModel testChatModel = new TestChatModel();
        testChatModel.setToId(Long.parseLong("1555912992478"));
        testChatModel.setMessage("test");
        try {
            client.sendP2P(testChatModel);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String m = msg.obj + "";
            KLog.i("客户端收到消息"+m);
            switch (msg.what) {
                case 0: {


                    KLog.i(msg.obj);
                    break;
                }
                default:
                    //Toast.makeText(activity, "UNKNOWN MSG: " + m, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

}
