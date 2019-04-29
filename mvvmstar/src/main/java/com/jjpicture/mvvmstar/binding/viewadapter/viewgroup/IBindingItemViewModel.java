package com.jjpicture.mvvmstar.binding.viewadapter.viewgroup;

import android.databinding.ViewDataBinding;

public interface IBindingItemViewModel<V extends ViewDataBinding> {
    void injecDataBinding(V binding);
}
