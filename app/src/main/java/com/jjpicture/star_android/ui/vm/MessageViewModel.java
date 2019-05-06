package com.jjpicture.star_android.ui.vm;

import android.app.Application;
import android.databinding.ObservableField;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.jjpicture.mvvmstar.base.BaseViewModel;
import com.jjpicture.mvvmstar.binding.command.BindingAction;
import com.jjpicture.mvvmstar.binding.command.BindingCommand;
import com.jjpicture.mvvmstar.im_common.enums.ChatType;
import com.jjpicture.mvvmstar.im_common.enums.MessageType;
import com.jjpicture.mvvmstar.im_common.protocol.ChatMessageProto;
import com.jjpicture.mvvmstar.im_common.request.LoginReq;
import com.jjpicture.mvvmstar.im_common.response.BaseResponse;
import com.jjpicture.mvvmstar.im_common.response.ServerInfoRes;
import com.jjpicture.mvvmstar.im_common.util.ChatMessageFactory;
import com.jjpicture.mvvmstar.utils.KLog;
import com.jjpicture.mvvmstar.utils.RxUtils;
import com.jjpicture.mvvmstar.utils.ToastUtils;
import com.jjpicture.star_android.data.TestRepository;
import com.jjpicture.star_android.im.client.IMClient;
import com.jjpicture.star_android.im.config.UserConfig;
import com.jjpicture.star_android.im.model.TestChatModel;
import com.jjpicture.star_android.im.service.impl.MessageServiceImpl;
import com.jjpicture.star_android.im.webrtc.WebRTCClient;

import org.webrtc.MediaStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;


public class MessageViewModel extends BaseViewModel<TestRepository>  implements WebRTCClient.WebRTCListener {


    public ObservableField<String> receivedMsg = new ObservableField<>("");
    public ObservableField<String> result = new ObservableField<>("");
    IMClient client = IMClient.getInstance();
    Button btn_start;



    // 录音状态
    // volatile ，可以保证多线程对它的访问，使看到访问的使一样的数据，避免多线程的问题
    // 保证多线程的内存同步，避免出问题
    private volatile boolean isRecording;

    private ExecutorService executorService;
    private Handler mainThreadHandler;
    private File audioFile;
    private FileOutputStream fileOutputStream;
    private long startRecordTime, stopRecordTime;

    private AudioRecord audioRecord;

    // buffer不能太大， 避免OOM
    private static final int BUFFER_SIZE = 2048;
    private byte[] buffer;

    // 播放逻辑
    // 主线程和后台播放线程数据同步
    private volatile boolean isPlaying;


    public MessageViewModel(@NonNull Application application, TestRepository repository)  {
        super(application, repository);
        //logout();
        login();
        //testSendP2P();
        KLog.e("wtf");

        WebRTCClient webrtcClient = WebRTCClient.INSTANCE;
        webrtcClient.setWebRTCListener(this);

        /**
         * 录音相关
         * */

        // 录音JNI函数不具备线程安全性，所以要用单线程
        executorService = Executors.newSingleThreadExecutor();
        mainThreadHandler = new Handler(Looper.getMainLooper());
        buffer = new byte[BUFFER_SIZE];
    }



    public void setStartButton(Button b) {
        this.btn_start = b;
    }

    public void setReceivedMsg(String msg) {
        this.receivedMsg.set(msg);
    }

    public void setResult(String msg) {
        this.result.set(msg);
    }

    @Override
    public void onCallReady(Long callId) {
//        if (callId.equals(UserConfig.getUserId())) {
//
//        }
    }

    @Override
    public void onAddRemoteStream(MediaStream remoteStream) {
        KLog.d("添加远程音频流");
        remoteStream.audioTracks.get(0);
    }

    @Override
    public void onLocalStream(MediaStream localStream) {
        KLog.d("本地音频流");
        localStream.audioTracks.get(0);
    }

    @Override
    public void onRemoveRemoteStream() {
        KLog.d("移除远程音频流");
    }

    @Override
    public void onStatusChanged(String newStatus) {
        KLog.d(newStatus);
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

    public void sendMsg() {
        TestChatModel testChatModel = new TestChatModel();
        testChatModel.setToId(UserConfig.FRIEND_ID);
        testChatModel.setMessage("message"+new Random(25).nextInt());
        try {
            client.sendP2P(testChatModel);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BindingCommand sendOnClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
//            sendMsg();
            WebRTCClient.INSTANCE.start();

            if (!UserConfig.getUserId().equals(UserConfig.FRIEND_ID)) {
                ChatMessageProto.ChatMessageProtocol messageProtocol =
                        ChatMessageFactory.buildMessage(
                                UserConfig.getUserId(),
                                UserConfig.FRIEND_ID,
                                ChatType.CHAT_VOICECALL.getIndex(),
                                MessageType.MESSAGE_CALL.getIndex(),
                                1,
                                "");
                //发送消息
                MessageServiceImpl.getInstance().sendMessage(messageProtocol, IMClient.getChannel(), 3);
            }
        }
    });



    public BindingCommand playOnClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            if (audioFile != null && !isPlaying) {
                // 设置当前播放状态
                isPlaying = true;
                // 提交后台任务，开始播放
                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        doPlay(audioFile);
                    }
                });
            }
        }
    });


    public BindingCommand startRecordOnClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            if (isRecording) {
                // 改变UI状态


                btn_start.setText("开始录音");
                // 改变录音状态
                isRecording = false;
//            // 提交后台任务，执行停止录音逻辑
//            executorService.submit(new Runnable() {
//                @Override
//                public void run() {
//                    // 停止录音， 失败提示用户
//                }
//            });
            } else {
                btn_start.setText("停止录音");
                // 改变录音状态
                isRecording = true;
                // 提交后台任务，执行录音逻辑
                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        // 开始录音， 失败提示用户
                        if (!startRecord()) {
                            recordFail();
                        }
                    }
                });
            }
        }
    });


    /**
     * 录音错误处理
     */
    private void recordFail() {
        ToastUtils.showShort("录音失败");

        // 重置录音状态， 以及UI状态
        isRecording = false;
        btn_start.setText("开始录音");
    }

    /**
     * 启动录音逻辑
     */
    private boolean startRecord() {
        try {
            // 创建录音文件
            audioFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                    + "/imSpeech/"
                    + System.currentTimeMillis()
                    + ".pcm");
            Log.e("录音文件位置 = ", "doStart: " + audioFile);
            audioFile.getParentFile().mkdirs();  // 创建所有的父目录
            audioFile.createNewFile();

            // 创建文件输出流
            fileOutputStream = new FileOutputStream(audioFile);
            // 配置AudioRecord
            // 从麦克风采集
            int audioSource = MediaRecorder.AudioSource.MIC;
            int sampleRate = 44100; // 所有安卓系统都支持的频率；
            // 单声道输入
            int channelConfig = AudioFormat.CHANNEL_IN_MONO;
            // PCM 16是所有安卓系统都支持
            int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
            // 计算AudioRecord 内部 buffer最小的大小
            int minBufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);

            // buffer 不能小于最低要求，也不能小于我们每次读取的大小
            audioRecord = new AudioRecord(
                    audioSource,
                    sampleRate,
                    channelConfig,
                    audioFormat,
                    Math.max(minBufferSize, BUFFER_SIZE));
            // 开始录音
            audioRecord.startRecording();
            // 记录录音开始时间，用于统计时长
            startRecordTime = System.currentTimeMillis();
            // 循环夺取数据， 写到输出流中
            while (isRecording) {
                // 只要还在录音状态，就一直读取数据
                int read = audioRecord.read(buffer, 0, BUFFER_SIZE);
                if (read > 0) {
                    // 读取成功写道文件中
                    fileOutputStream.write(buffer, 0, read);
                } else {
                    // 读取失败， 返回false， 提示用户
                    return false;
                }
            }
            // 退出循环，停止录音，释放资源
            return stopRecord();
        } catch (IOException | RuntimeException e) {
            e.printStackTrace();
            // 捕获异常，避免闪退返回false,提醒用户失败
            return false;
        } finally {
            // 释放资源
            if (audioRecord != null) {
                audioRecord.release();
            }
        }

    }

    /**
     * 停止录音逻辑
     *
     * @return
     */
    private boolean stopRecord() {
        try {
            // 停止录音， 关闭文件输出流
            audioRecord.stop();
            audioRecord.release();
            audioRecord = null;
            fileOutputStream.close();

            // 记录结束时间，统计时长
            stopRecordTime = System.currentTimeMillis();
            // 大于3秒， 在主线程UI显示
            final int second = (int) ((stopRecordTime - startRecordTime) / 1000);
            if (second > 3) {
                setResult( "录音成功，时常为" + second + "秒");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }



    @Override
    public void onDestroy() {
        // activity 销毁时，停止后台任务， 避免内存泄漏
        executorService.shutdownNow();
        receivedMsg = null;
        super.onDestroy();

    }

    /**
     * 播放逻辑
     *
     * @param audioFile
     */
    private void doPlay(File audioFile) {

        // 配置播放器
        // 音乐类型, 扬声器播放
        int streamType = AudioManager.STREAM_MUSIC;
        // 录音时采用的采样频率，所以播放时使用同样的采样频率
        int sampleRate = 44100;
        // MONO 表示单声道，录音输入单声道, 播放用输出单声道
        int channelConfig = AudioFormat.CHANNEL_OUT_MONO;
        // 录音时使用16 bit，所以播放时使用同样的格式
        int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
        // 流模式
        int mode = AudioTrack.MODE_STREAM;
        // 计算最小buffer大小
        int minBufferSize = AudioTrack.getMinBufferSize(sampleRate, channelConfig, audioFormat);
        // 构造AudioTrack
        AudioTrack audioTrack = new AudioTrack(streamType, sampleRate,
                channelConfig, audioFormat,
                // 不能小于AudioTrack的最低要求， 也不能小于我们每次读的大小
                Math.max(minBufferSize, BUFFER_SIZE),
                mode);

        audioTrack.play();
        // 从文件流读数据
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(audioFile);
            int read;
            while ((read = inputStream.read(buffer)) > 0) {
                int ret = audioTrack.write(buffer, 0, read);
                switch (ret) {
                    case AudioTrack.ERROR_INVALID_OPERATION:
                    case AudioTrack.ERROR_BAD_VALUE:
                    case AudioManager.ERROR_DEAD_OBJECT:
                        playFail();
                        return;
                    default:
                        break;
                }
            }
        } catch (IOException | RuntimeException e) {
            e.printStackTrace();
            // 错误处理， 防止闪退
            playFail();
        } finally {
            isPlaying = false;
            if (inputStream != null) {
                closeQuietly(inputStream);
            }
            resetQuietly(audioTrack);
        }


    }

    private void resetQuietly(AudioTrack audioTrack) {
        try {
            audioTrack.stop();
            audioTrack.release();
        } catch (RuntimeException e) {
            e.printStackTrace();
        }

    }

    /**
     * 播放错误处理
     */
    private void playFail() {
        audioFile = null;
        ToastUtils.showShort("播放失败");

    }

    /**
     * 静默关闭输入流
     *
     * @param inputStream
     */
    private void closeQuietly(FileInputStream inputStream) {
        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
