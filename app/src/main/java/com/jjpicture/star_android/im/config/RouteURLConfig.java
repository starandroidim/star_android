package com.jjpicture.star_android.im.config;


public class RouteURLConfig {


    private static String loginURL = "http://39.105.139.90:8081/login";


    private static String loginInfoURL = "http://39.105.139.90:8081/loginInfo";

    private static String logoutURL = "http://39.105.139.90:8081/logout";


    private static String registerUrl = "http://39.105.139.90:8081/register";

    public static String getLoginURL() {
        return loginURL;
    }

    public void setLoginURL(String loginURL) {
        this.loginURL = loginURL;
    }

    public static String getLoginInfoURL() {
        return loginInfoURL;
    }

    public void setLoginInfoURL(String loginInfoURL) {
        this.loginInfoURL = loginInfoURL;
    }

    public static String getLogoutURL() {
        return logoutURL;
    }

    public void setLogoutURL(String logoutURL) {
        this.logoutURL = logoutURL;
    }

    public static String getRegisterUrl() {
        return registerUrl;
    }

    public void setRegisterUrl(String registerUrl) {
        this.registerUrl = registerUrl;
    }

}
