package com.jjpicture.mvvmstar.base;

import android.support.annotation.NonNull;

public class ItemViewModel<VM extends BaseViewModel> {
    protected VM viewModel;

    public ItemViewModel(@NonNull VM vieewModel) { this.viewModel = vieewModel; }
}
