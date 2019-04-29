package com.jjpicture.mvvmstar.im_common.request;

/**
 * @author lyl
 * @Title: SaveInfoReq
 * @Description: 保存登录信息req
 * @date 2019/3/15 15:38
 */
public class SaveInfoReq {
    Long userId;

    String serverInfo;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getServerInfo() {
        return serverInfo;
    }

    public void setServerInfo(String serverInfo) {
        this.serverInfo = serverInfo;
    }
}
