package com.jjpicture.mvvmstar.base;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class BaseModel implements IModel {
    //管理RxJava,主要针对RxJava异步操作造成内存泄漏
    private CompositeDisposable mCompositeDisposable;
    public BaseModel()
    {
        mCompositeDisposable = new CompositeDisposable();
    }
    protected void addSubscrible(Disposable disposable) {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = new CompositeDisposable();
        }
        mCompositeDisposable.add(disposable);
    }

    @Override
    public void onCleared(){
        //ViewModel销毁时会执行，同时取消所有异步任务
        if (mCompositeDisposable != null) {
            mCompositeDisposable.clear();
        }
    }
}
