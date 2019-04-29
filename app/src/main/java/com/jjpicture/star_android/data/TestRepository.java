package com.jjpicture.star_android.data;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.jjpicture.mvvmstar.base.BaseModel;
import com.jjpicture.mvvmstar.im_common.request.LoginReq;
import com.jjpicture.mvvmstar.im_common.request.LogoutReq;
import com.jjpicture.mvvmstar.im_common.request.RegisterReq;
import com.jjpicture.mvvmstar.im_common.response.BaseResponse;
import com.jjpicture.mvvmstar.im_common.response.RegisterRes;
import com.jjpicture.mvvmstar.im_common.response.ServerInfoRes;
import com.jjpicture.star_android.data.service.IMService;
import com.jjpicture.star_android.entity.WeatherEntity;
import com.jjpicture.star_android.im.model.TestChatModel;

import java.io.IOException;

import io.reactivex.Observable;

public class TestRepository extends BaseModel implements HttpDataSource, IMService {

    private volatile static TestRepository INSTANCE = null;
    private final HttpDataSource mHttpDataSource;
    private final IMWork imWork;


    private TestRepository(@NonNull HttpDataSource httpDataSource, @NonNull IMWork imWork) {
        this.mHttpDataSource = httpDataSource;
        this.imWork = imWork;

    }

    public static TestRepository getInstance(HttpDataSource httpDataSource,IMWork imWork) {
        if (INSTANCE == null) {
            synchronized (TestRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new TestRepository(httpDataSource,imWork);
                }
            }
        }
        return INSTANCE;
    }

    @VisibleForTesting
    public static void destroyInstance() {
        INSTANCE = null;
    }


    @Override
    public Observable<WeatherEntity> testGet(String city) {
        return mHttpDataSource.testGet(city);
    }

    @Override
    public Observable<BaseResponse<RegisterRes>> register(RegisterReq registerReq) {
        return imWork.register(registerReq);
    }

    @Override
    public Observable<BaseResponse<Object>> logout(LogoutReq logoutReq) {
        return imWork.logout(logoutReq);
    }

    @Override
    public Observable<BaseResponse<ServerInfoRes>> login(LoginReq loginReq) {
        return imWork.login(loginReq);
    }
}
