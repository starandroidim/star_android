package com.jjpicture.star_android.data;

import com.jjpicture.mvvmstar.im_common.request.LoginReq;
import com.jjpicture.mvvmstar.im_common.request.LogoutReq;
import com.jjpicture.mvvmstar.im_common.request.RegisterReq;
import com.jjpicture.mvvmstar.im_common.response.BaseResponse;
import com.jjpicture.mvvmstar.im_common.response.RegisterRes;
import com.jjpicture.mvvmstar.im_common.response.ServerInfoRes;

import io.reactivex.Observable;

public interface IMWork {


    Observable<BaseResponse<RegisterRes>> register(RegisterReq registerReq);

    Observable<BaseResponse<Object>> logout(LogoutReq logoutReq);

    Observable<BaseResponse<ServerInfoRes> > login(LoginReq loginReq);





}
