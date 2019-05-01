package com.jjpicture.star_android.ui.vm;

import android.app.Application;
import android.databinding.ObservableField;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;

import com.jjpicture.mvvmstar.base.BaseViewModel;
import com.jjpicture.mvvmstar.binding.command.BindingAction;
import com.jjpicture.mvvmstar.binding.command.BindingCommand;
import com.jjpicture.mvvmstar.im_common.request.LoginReq;
import com.jjpicture.mvvmstar.im_common.response.BaseResponse;
import com.jjpicture.mvvmstar.im_common.response.ServerInfoRes;
import com.jjpicture.mvvmstar.utils.KLog;
import com.jjpicture.mvvmstar.utils.RxUtils;
import com.jjpicture.star_android.data.TestRepository;
import com.jjpicture.star_android.im.client.IMClient;
import com.jjpicture.star_android.im.config.UserConfig;
import com.jjpicture.star_android.im.model.TestChatModel;

import java.io.IOException;
import java.util.Random;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;


public class MessageViewModel extends BaseViewModel<TestRepository> {
    public ObservableField<String> receivedMsg = new ObservableField<>("");

    IMClient client = IMClient.getInstance();

    public MessageViewModel(@NonNull Application application, TestRepository repository)  {
        super(application, repository);
        //logout();
        login();
        //testSendP2P();
        KLog.e("wtf");

        //WebRTCClient webrtcClient = WebRTCClient.INSTANCE;
        //webrtcClient.setWebRTCListener(this);

    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String m = msg.obj + "";
            KLog.i("客户端收到消息"+m);
            switch (msg.what) {
                case 0:
                    setReceivedMsg(m);
                   break;
                default:
                    //Toast.makeText(activity, "UNKNOWN MSG: " + m, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

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
                        KLog.d(serverInfoResBaseResponse);
                        client.start(serverInfoResBaseResponse.getDataBody(),handler);
                    }
                }));

    }

    public void setReceivedMsg(String msg) {
        this.receivedMsg.set(msg);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        receivedMsg = null;
    }

    public BindingCommand sendOnClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            sendMsg();
        }
    });


    public void sendMsg() {
        TestChatModel testChatModel = new TestChatModel();
        testChatModel.setToId(Long.parseLong("1552465428829"));
        testChatModel.setMessage("message"+new Random(25).nextInt());
        try {
            client.sendP2P(testChatModel);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
