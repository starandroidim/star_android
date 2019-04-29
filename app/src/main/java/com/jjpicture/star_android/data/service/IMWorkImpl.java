package com.jjpicture.star_android.data.service;

import com.jjpicture.mvvmstar.im_common.request.LoginReq;
import com.jjpicture.mvvmstar.im_common.request.LogoutReq;
import com.jjpicture.mvvmstar.im_common.request.RegisterReq;
import com.jjpicture.mvvmstar.im_common.response.BaseResponse;
import com.jjpicture.mvvmstar.im_common.response.RegisterRes;
import com.jjpicture.mvvmstar.im_common.response.ServerInfoRes;
import com.jjpicture.star_android.data.IMWork;


import io.reactivex.Observable;

public class IMWorkImpl implements IMWork {

    private IMService imService;
    private volatile static IMWorkImpl INSTANCE = null;

    public IMWorkImpl(IMService imService) {
        this.imService = imService;
    }


    public static IMWorkImpl getInstance(IMService imService) {
        if (INSTANCE == null) {
            synchronized (IMWorkImpl.class) {
                if (INSTANCE == null) {
                    INSTANCE = new IMWorkImpl(imService);
                }
            }
        }
        return INSTANCE;
    }


    @Override
    public Observable<BaseResponse<RegisterRes>> register(RegisterReq registerReq) {
        return imService.register(registerReq);
    }

    @Override
    public Observable<BaseResponse<Object>> logout(LogoutReq logoutReq) {
        return imService.logout(logoutReq);
    }

    @Override
    public Observable<BaseResponse<ServerInfoRes>> login(LoginReq loginReq) {
        return imService.login(loginReq);
    }


}
