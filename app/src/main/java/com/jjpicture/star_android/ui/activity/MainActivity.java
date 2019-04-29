package com.jjpicture.star_android.ui.activity;


import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;

import com.jjpicture.mvvmstar.base.BaseActivity;
import com.jjpicture.mvvmstar.base.BaseViewModel;

import com.jjpicture.mvvmstar.im_common.enums.ChatType;
import com.jjpicture.mvvmstar.utils.KLog;
import com.jjpicture.star_android.BR;
import com.jjpicture.star_android.R;

import com.jjpicture.star_android.app.AppViewModelFactory;

import com.jjpicture.star_android.databinding.ActivityMainBinding;

import com.jjpicture.star_android.ui.custom.SpecialTab;
import com.jjpicture.star_android.ui.fragment.HomeFragment;
import com.jjpicture.star_android.ui.fragment.MeFragment;
import com.jjpicture.star_android.ui.fragment.MessageFragment;
import com.jjpicture.star_android.ui.fragment.MomentFragment;
import com.jjpicture.star_android.ui.vm.MainViewModel;

import java.util.ArrayList;
import java.util.List;

import me.majiajie.pagerbottomtabstrip.NavigationController;
import me.majiajie.pagerbottomtabstrip.PageNavigationView;
import me.majiajie.pagerbottomtabstrip.item.BaseTabItem;
import me.majiajie.pagerbottomtabstrip.listener.OnTabItemSelectedListener;



public class MainActivity extends BaseActivity<ActivityMainBinding, BaseViewModel> {
    private List<Fragment> mFragments;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_main;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public BaseViewModel initViewModel() {
        AppViewModelFactory factory = AppViewModelFactory.getInstance(getApplication());
        return ViewModelProviders.of(this,factory).get(MainViewModel.class);
    }


    @Override
    public void initData() {
        //初始化Fragment
        initFragment();
        //初始化底部Button
        initBottomTab();
        //sendRequest();



    }

    private void initFragment() {
        mFragments = new ArrayList<>();
        mFragments.add(new HomeFragment());
        mFragments.add(new MomentFragment());
        mFragments.add(new MessageFragment());
        mFragments.add(new MeFragment());
        //默认选中第一个
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.frameLayout, mFragments.get(0));
        transaction.commitAllowingStateLoss();
    }

    private void initBottomTab() {

        PageNavigationView pageBottomTablayout = findViewById(R.id.bottom_tab);
        NavigationController navigationController = pageBottomTablayout.custom()
                .addItem(newItem(R.mipmap.home,R.mipmap.home_select,"星你"))
                .addItem(newItem(R.mipmap.moment,R.mipmap.moment_select,"广场"))
                .addItem(newItem(R.mipmap.message,R.mipmap.message_select,"消息"))
                .addItem(newItem(R.mipmap.me,R.mipmap.me_select,"个人"))
                .build();
        //设置消息数
        navigationController.setMessageNumber(2, 8);

        //设置显示小圆点
        navigationController.setHasMessage(0, true);

        //底部按钮的点击事件监听
        navigationController.addTabItemSelectedListener(new OnTabItemSelectedListener() {
            @Override
            public void onSelected(int index, int old) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frameLayout, mFragments.get(index));
                transaction.commitAllowingStateLoss();
                test();
            }

            @Override
            public void onRepeat(int index) {
            }
        });
    }

    /**
     * 正常tab
     */
    private BaseTabItem newItem(int drawable, int checkedDrawable, String text){
        SpecialTab mainTab = new SpecialTab(this);
        mainTab.initialize(drawable, checkedDrawable, text);
        mainTab.setTextDefaultColor(ContextCompat.getColor(this, R.color.textColorVice));
        mainTab.setTextCheckedColor(ContextCompat.getColor(this, R.color.textColorSelect));
        return mainTab;
    }

    public void test() {

    }


}
