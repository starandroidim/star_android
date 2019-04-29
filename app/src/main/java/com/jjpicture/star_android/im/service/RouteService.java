package com.jjpicture.star_android.im.service;

import com.jjpicture.mvvmstar.im_common.request.LoginReq;
import com.jjpicture.mvvmstar.im_common.request.LogoutReq;
import com.jjpicture.mvvmstar.im_common.response.ServerInfoRes;

import java.io.IOException;

public interface RouteService {

    void saveLoginInfo() throws IOException;

    void logout(LogoutReq logoutReq) throws IOException;

    void setServerInfo(String serverInfo);

    public ServerInfoRes loginAndGetServer(LoginReq loginReq) throws IOException;

}
