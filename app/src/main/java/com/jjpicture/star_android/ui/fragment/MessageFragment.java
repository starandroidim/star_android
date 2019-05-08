package com.jjpicture.star_android.ui.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;

import com.jjpicture.mvvmstar.base.BaseFragment;

import com.jjpicture.mvvmstar.utils.KLog;
import com.jjpicture.star_android.BR;
import com.jjpicture.star_android.R;
import com.jjpicture.star_android.app.AppViewModelFactory;
import com.jjpicture.star_android.databinding.FragmentMessageBinding;
import com.jjpicture.star_android.ui.vm.MainViewModel;
import com.jjpicture.star_android.ui.vm.MessageViewModel;


public class MessageFragment extends BaseFragment<FragmentMessageBinding, MessageViewModel> {
    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_message;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {

        viewModel.setReceivedMsg("test");
        viewModel.setStartButton(binding.btnStart);
    }

    @Override
    public MessageViewModel initViewModel() {
        AppViewModelFactory factory = AppViewModelFactory.getInstance(getActivity().getApplication());
        return ViewModelProviders.of(this, factory).get(MessageViewModel.class);
    }







}
