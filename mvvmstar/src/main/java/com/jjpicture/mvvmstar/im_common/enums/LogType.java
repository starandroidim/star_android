package com.jjpicture.mvvmstar.im_common.enums;

public enum LogType {
    LOGIN_REQ("登录请求", 0),
    LOGOUT_REQ("登出请求", 1);

    private String type;
    private int index;

    LogType(String type, int index){
        this.type = type;
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
