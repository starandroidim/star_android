package com.jjpicture.star_android.data.service;

import com.jjpicture.mvvmstar.im_common.request.LoginReq;
import com.jjpicture.mvvmstar.im_common.request.LogoutReq;
import com.jjpicture.mvvmstar.im_common.request.RegisterReq;
import com.jjpicture.mvvmstar.im_common.response.BaseResponse;
import com.jjpicture.mvvmstar.im_common.response.RegisterRes;
import com.jjpicture.mvvmstar.im_common.response.ServerInfoRes;
import com.jjpicture.star_android.im.model.TestChatModel;

import java.io.IOException;

import io.reactivex.Observable;
import retrofit2.http.Body;

import retrofit2.http.POST;

public interface IMService {
    @POST("register")
    Observable<BaseResponse<RegisterRes>> register(@Body RegisterReq registerReq);


    @POST("logout")
    Observable<BaseResponse<Object> > logout(@Body LogoutReq logoutReq);

    @POST("login")
    Observable<BaseResponse<ServerInfoRes> > login(@Body LoginReq loginReq);

}
