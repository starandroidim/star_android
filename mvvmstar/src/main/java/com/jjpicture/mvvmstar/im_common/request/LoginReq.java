package com.jjpicture.mvvmstar.im_common.request;
public class LoginReq {
    private Long userId;
    private String userName;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {
        return "LoginReq{" +
                "userId=" + userId +
                ", userName='" + userName + '\'' +
                '}';
    }

    public LoginReq(Long userId, String userName) {
        this.userId = userId;
        this.userName = userName;
    }

    public LoginReq(){}
}