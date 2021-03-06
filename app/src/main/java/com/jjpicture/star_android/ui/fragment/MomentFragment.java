package com.jjpicture.star_android.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.jjpicture.mvvmstar.base.BaseFragment;
import com.jjpicture.star_android.BR;
import com.jjpicture.star_android.R;

public class MomentFragment extends BaseFragment {
    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_moment;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }
}
