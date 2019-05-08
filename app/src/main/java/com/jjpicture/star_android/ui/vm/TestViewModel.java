package com.jjpicture.star_android.ui.vm;

import android.app.Application;
import android.databinding.ObservableField;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import android.widget.FrameLayout;
import android.widget.ImageView;

import com.jjpicture.mvvmstar.base.BaseViewModel;
import com.jjpicture.mvvmstar.binding.command.BindingAction;
import com.jjpicture.mvvmstar.binding.command.BindingCommand;

import com.jjpicture.mvvmstar.utils.KLog;

import com.jjpicture.mvvmstar.utils.ToastUtils;
import com.jjpicture.star_android.data.TestRepository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TestViewModel extends BaseViewModel<TestRepository>{

    FrameLayout fl_layout;

    List<ImageView> voice_indicator_list = new ArrayList<>();

    public ObservableField<String> btnSpeechText = new ObservableField<>("");
    public ObservableField<String> resultText = new ObservableField<>("");


    public void setFl_layout(FrameLayout fl_layout) {
        this.fl_layout = fl_layout;
    }

    public void setVoice_indicator_list(List<ImageView> voice_indicator_list) {
        this.voice_indicator_list = voice_indicator_list;
    }

    private MediaRecorder mediaRecorder;

    private File audioFile;

    private long startRecordTime, stopRecordTime;

    private Handler mainThreadHandler;
    // 播放逻辑
    // 主线程和后台播放线程数据同步
    private volatile boolean isPlaying;

    private MediaPlayer mediaPlayer;

    // mediaRecorder.getMaxAmplitude() 返回值最大是32767;
    private static final int MAX_AMPLITUDE = 32767;

    private static final int MAX_LEVEL = 8;

    private ScheduledExecutorService executorService;

    private volatile boolean isRecording;

    private Random random;

    public TestViewModel(@NonNull Application application, TestRepository repository)  {
        super(application, repository);
        // 录音JNI函数不具备线程安全性，所以要用单线程
        executorService = Executors.newSingleThreadScheduledExecutor();
        mainThreadHandler = new Handler(Looper.getMainLooper());

        random = new Random(System.currentTimeMillis());
    }

    public BindingCommand startOnClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            KLog.d("开始播放录音");
            // 检查当前状态，防止重复播放
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

    /**
     * 停止录音
     */
    public void stopRecord() {
        // 改变UI状态
        btnSpeechText.set("开始录音");
        // 隐藏音量提示UI
        fl_layout.setVisibility(View.GONE);
        // 提交后台任务， 执行停止录音逻辑
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                // 执行停止录音逻辑，失败提示用户
                if (!doStop()) {
                    recordFail();
                }
                // 释放MediaRecorder
                releaseRecorder();
            }
        });
    }
    /**
     * 开始录音
     */
    public void startRecord() {
        // 改变UI的状态108108
        btnSpeechText.set("正在说话");
        // 显示提示View
        fl_layout.setVisibility(View.VISIBLE);
        // 提交后台任务， 执行录音逻辑
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                // 释放之前录音的recorder
                releaseRecorder();
                // 执行录音逻辑， 如果失败提示用户
                if (!doStart()) {
                    recordFail();
                }
            }
        });
        // 提交后台获取音量大小的任务
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                monitorRecordAmplitude();
            }
        });
    }
    /**
     * 定期获取录音的音量
     */
    private void monitorRecordAmplitude() {
        if (mediaRecorder == null) {
            return;
        }
        int amplitude;
        try {
            // 获取音量大小
            amplitude = mediaRecorder.getMaxAmplitude();
        } catch (RuntimeException e) {
            // 捕获异常，避免闪退
            // 异常发生后，用一个随机数代表当前音量大小
            amplitude = random.nextInt(MAX_AMPLITUDE);
        }
        // 把音量归一化到8个等级
        final int level = amplitude / (MAX_AMPLITUDE / MAX_LEVEL);
        //refreshAudioAmplitude(level);
        // 把等级显示在ui上
        mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                refreshAudioAmplitude(level);
            }
        });

        // 如果仍在录音，50ms之后，再次获取音量大小
        if (isRecording) {
            executorService.schedule(new Runnable() {
                @Override
                public void run() {
                    monitorRecordAmplitude();
                }
            }, 50, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * 显示音量等级
     * @param level
     */
    private void refreshAudioAmplitude(int level) {
        // 对所有的ImageView进行遍历， 如果它的位置小于level， 就应该显示
        for (int i = 0; i < voice_indicator_list.size(); i++) {
            // i < level, 而不是i <= level, 否则0级就会显示1个
            voice_indicator_list.get(i).setVisibility(i < level ? View.VISIBLE : View.INVISIBLE);
        }
    }

    /**
     * 录音错误处理
     */
    private void recordFail() {
        audioFile = null;
        ToastUtils.showShort("录音失败");
    }

    /**
     * 释放MediaRecorder
     */
    private void releaseRecorder() {
        // 检查MediaRecorder 不为null
        if (mediaRecorder != null) {
            mediaRecorder.release();
            mediaRecorder = null;
        }
    }

    /**
     * 停止录音逻辑
     * @return 抛出异常:
     * W/System.err: java.lang.RuntimeException: stop failed.
     * W/System.err:     at android.media.MediaRecorder.stop(Native Method)
     */
    private boolean doStop() {
        // 停止录音
        try {
            mediaRecorder.stop();
            // 记录停止时间， 统计时长
            stopRecordTime = System.currentTimeMillis();
            isRecording = false;
            // 只接受超过3秒的录音， 在UI上显示出来
            final int second = (int) ((stopRecordTime - startRecordTime) / 1000);
            if (second > 3) {
                KLog.d("录音成功————————————————————————————————————————————————————————");
//               tv_result.setText(tv_result.getText() + "\n录音成功 " + second + "秒");
                resultText.set("录音成功：" + second + "秒");
            } else {
                resultText.set("录音时常较短");
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            // 捕获异常，避免闪退返回false,提醒用户失败
            return false;
        }
        // 停止成功
        return true;
    }
    /**
     * 启动录音逻辑
     * @return
     */
    private boolean doStart() {
        try {
            // 创建 MediaRecorder
            mediaRecorder = new MediaRecorder();
            // 创建录音文件
            audioFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                    + "/SpeechProcessDemo/"
                    + System.currentTimeMillis()
                    + ".m4a");
            Log.e("录音文件位置 = ", "doStart: " + audioFile);
            audioFile.getParentFile().mkdirs();  // 创建所有的父目录
            audioFile.createNewFile();
            // 配置MediaRecorder
            // 从麦克风采集
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            // 保存文件为MP4格式
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            // 所有安卓系统都支持的采样频率
            mediaRecorder.setAudioSamplingRate(44100);
            // 通用的AAC编码格式
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            // 音质比较好的频率
            mediaRecorder.setAudioEncodingBitRate(96000);
            // 设置录音文件的位置
            mediaRecorder.setOutputFile(audioFile.getAbsolutePath());

            // 开始录音, prepare与start会抛出异常，我们捕获它，用RuntimeException, 避免闪退
            mediaRecorder.prepare();
            mediaRecorder.start();
            // 记录开始录音的时间， 用于统计时长
            startRecordTime = System.currentTimeMillis();
            isRecording = true;
        } catch (IOException | RuntimeException e) {
            e.printStackTrace();
            // 捕获异常，避免闪退返回false,提醒用户失败
            return false;
        }
        // 录音成功
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // activity 销毁时，停止后台任务， 避免内存泄漏
        executorService.shutdownNow();
        // 释放录音
        releaseRecorder();
        stopPlay();
    }

    /**
     * 播放录音文件
     *
     * @param audioFile
     */
    private void doPlay(File audioFile) {
        // 配置播放器 MediaPlayer
        mediaPlayer = new MediaPlayer();
        try {
            // 设置声音文件
            mediaPlayer.setDataSource(audioFile.getAbsolutePath());
            // 设置监听回调
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    // 播放结束， 释放播放器
                    stopPlay();
                }
            });
            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    // 提示用户
                    playFail();
                    // 释放播放器
                    stopPlay();
                    // 错误已经处理，返回true
                    return true;
                }
            });
            // 配置音量，是否循环
            mediaPlayer.setVolume(1, 1);
            mediaPlayer.setLooping(false);
            // 准备，开始
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException | RuntimeException e) {
            // 异常处理，防止闪退
            e.printStackTrace();
            // 提示用户
            playFail();
            // 释放播放器
            stopPlay();
        }
    }

    /**
     * 停止播放逻辑
     */
    private void stopPlay() {
        // 重置播放状态
        isPlaying = false;
        // 释放播放器
        if (mediaPlayer != null) {
            // 重置播放器， 防止内存泄漏
            mediaPlayer.setOnCompletionListener(null);
            mediaPlayer.setOnErrorListener(null);
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
    /**
     * 提醒用户播放失败
     */
    private void playFail() {
        ToastUtils.showShort("播放失败");
    }
}
