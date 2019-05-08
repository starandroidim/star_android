package com.jjpicture.star_android.ui.fragment;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;


import com.jjpicture.mvvmstar.base.BaseFragment;
import com.jjpicture.star_android.BR;
import com.jjpicture.star_android.R;
import com.jjpicture.star_android.app.AppViewModelFactory;
import com.jjpicture.star_android.databinding.FragmentTestBinding;
import com.jjpicture.star_android.ui.vm.TestViewModel;


import java.util.ArrayList;
import java.util.List;


public class TestFragment extends BaseFragment<FragmentTestBinding, TestViewModel> {
    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_test;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        /*
        tv_result = binding.tvResult;
        btn_play = binding.btnPlay;
        btn_speech = binding.btnSpeech;
        fl_layout = binding.flLayout;

        voice_indicator_list.add(binding.ivIndicator1);
        voice_indicator_list.add(binding.ivIndicator2);
        voice_indicator_list.add(binding.ivIndicator3);
        voice_indicator_list.add(binding.ivIndicator4);
        voice_indicator_list.add(binding.ivIndicator5);
        voice_indicator_list.add(binding.ivIndicator6);
        voice_indicator_list.add(binding.ivIndicator7);
*/
        viewModel.setFl_layout(binding.flLayout);

        List<ImageView> voice_indicator_list = new ArrayList<>();
        voice_indicator_list.add(binding.ivIndicator1);
        voice_indicator_list.add(binding.ivIndicator2);
        voice_indicator_list.add(binding.ivIndicator3);
        voice_indicator_list.add(binding.ivIndicator4);
        voice_indicator_list.add(binding.ivIndicator5);
        voice_indicator_list.add(binding.ivIndicator6);
        voice_indicator_list.add(binding.ivIndicator7);

        viewModel.setVoice_indicator_list(voice_indicator_list);
        // 按下说话, 释放发送，所以我们不要onClickListener
        binding.btnSpeech.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // 根据不同的touch action,执行不同的逻辑
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        viewModel.startRecord();
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        viewModel.stopRecord();
                        break;
                    default:
                        break;
                }
                // 处理了touch事件，返回true
                return true;
            }
        });

        /*
        binding.btnPlay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
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
        */
        // TODO BTNPLAY mvvm binding
        //viewModel.setReceivedMsg("test");

    }

    @Override
    public TestViewModel initViewModel() {
        AppViewModelFactory factory = AppViewModelFactory.getInstance(getActivity().getApplication());
        return ViewModelProviders.of(this, factory).get(TestViewModel.class);
    }

}
