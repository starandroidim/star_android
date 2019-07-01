package com.jjpicture.star_android.im.config;


public class RouteURLConfig {

    private static String ROUTE_IP = "39.100.110.18:8081";

    private static String loginURL = "http://" + ROUTE_IP + "/login";


    private static String loginInfoURL = "http://" + ROUTE_IP + "/loginInfo";

    private static String logoutURL = "http://" + ROUTE_IP + "/logout";


    private static String registerUrl = "http://" + ROUTE_IP + "/register";

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
