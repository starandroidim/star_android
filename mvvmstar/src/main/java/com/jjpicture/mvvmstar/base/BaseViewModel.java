package com.jjpicture.mvvmstar.base;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.jjpicture.mvvmstar.bus.event.SingleLiveEvent;
import com.trello.rxlifecycle2.LifecycleProvider;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.disposables.Disposable;

public class BaseViewModel<M extends BaseModel> extends AndroidViewModel implements IBaseViewModel {
    protected M model;
    private UIChangeLiveData uc;
    //弱引用持有
    private WeakReference<LifecycleProvider> lifecycle;

    public BaseViewModel(@NonNull Application application) {
        super(application);
        model = null;
    }

    public BaseViewModel(@NonNull Application application, M model) {
        super(application);
        this.model = model;
    }

    protected void addSubscribe(Disposable disposable) { model.addSubscrible(disposable); }

    /**
     * 注入RxLifeCycle 生命周期
     */
    public void injectLifecycleProvider(LifecycleProvider lifecycle) {
        this.lifecycle = new WeakReference<>(lifecycle);
    }

    public LifecycleProvider getLifecycleProvider() { return lifecycle.get(); }

    public UIChangeLiveData getUC(){
        if (uc == null) {
            uc = new UIChangeLiveData();
        }
        return uc;
    }

    public void showDialog() { showDialog("请稍后..."); }

    public void showDialog(String title) { uc.showDialogEvent.postValue(title); }

    public void dismissDialog() { uc.dismissDialogEvent.call(); }

    /**
     * 跳转页面
     *
     * @param clz 所跳转的目的Activity类
     */
    public void startActivity(Class<?> clz) {
        startActivity(clz, null);
    }

    /**
     * 跳转页面
     *  @param clz    所跳转的目的Activity类
     *  @param bundle 跳转所携带的信息
     */
    public void startActivity(Class<?> clz, Bundle bundle) {
        Map<String, Object> params = new HashMap<>();
        params.put(ParameterField.CLASS, clz);
        if (bundle != null) {
            params.put(ParameterField.BUNDLE, bundle);
        }
        uc.startActivityEvent.postValue(params);
    }
    /**
     * 跳转容器页面
     *
     * @param canonicalName 规范名 : Fragment.class.getCanonicalName()
     */
    public void startContainerActivity(String canonicalName) {
        startContainerActivity(canonicalName, null);
    }

    /**
     * 跳转容器页面
     *
     * @param canonicalName 规范名 : Fragment.class.getCanonicalName()
     * @param bundle        跳转所携带的信息
     */
    public void startContainerActivity(String canonicalName, Bundle bundle) {
        Map<String, Object> params = new HashMap<>();
        params.put(ParameterField.CANONICAL_NAME, canonicalName);
        if (bundle != null) {
            params.put(ParameterField.BUNDLE, bundle);
        }
        uc.startContainerActivityEvent.postValue(params);
    }
    /**
     * 关闭界面
     */
    public void finish() {
        uc.finishEvent.call();
    }

    /**
     * 返回上一层
     */
    public void onBackPressed() {
        uc.onBackPressedEvent.call();
    }
    @Override
    public void onAny(LifecycleOwner owner, Lifecycle.Event event) {
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDestroy() {
    }

    @Override
    public void onStart() {
    }

    @Override
    public void onStop() {
    }

    @Override
    public void onResume() {
    }

    @Override
    public void onPause() {
    }

    @Override
    public void registerRxBus() {
    }

    @Override
    public void removeRxBus() {
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (model != null) {
            model.onCleared();
        }
    }


    public final class UIChangeLiveData extends SingleLiveEvent {
        private SingleLiveEvent<String> showDialogEvent;
        private SingleLiveEvent<Void> dismissDialogEvent;
        private SingleLiveEvent<Map<String, Object>> startActivityEvent;
        private SingleLiveEvent<Map<String, Object>> startContainerActivityEvent;
        private SingleLiveEvent<Void> finishEvent;
        private SingleLiveEvent<Void> onBackPressedEvent;

        public SingleLiveEvent<String> getShowDialogEvent() {
            return showDialogEvent = createLiveData(showDialogEvent);
        }

        public SingleLiveEvent<Void> getDismissDialogEvent() {
            return dismissDialogEvent = createLiveData(dismissDialogEvent);
        }

        public SingleLiveEvent<Map<String, Object>> getStartActivityEvent() {
            return startActivityEvent = createLiveData(startActivityEvent);
        }

        public SingleLiveEvent<Map<String, Object>> getStartContainerActivityEvent() {
            return startContainerActivityEvent = createLiveData(startContainerActivityEvent);
        }

        public SingleLiveEvent<Void> getFinishEvent() {
            return finishEvent = createLiveData(finishEvent);
        }

        public SingleLiveEvent<Void> getOnBackPressedEvent() {
            return onBackPressedEvent = createLiveData(onBackPressedEvent);
        }

        private SingleLiveEvent createLiveData(SingleLiveEvent liveData) {
            if (liveData == null) {
                liveData = new SingleLiveEvent();
            }
            return liveData;
        }

        @Override
        public void observe(LifecycleOwner owner, Observer observer) {
            super.observe(owner, observer);
        }
    }

    public static final class ParameterField {
        public static String CLASS = "CLASS";
        public static String CANONICAL_NAME = "CANONICAL_NAME";
        public static String BUNDLE = "BUNDLE";
    }
}
