package com.jjpicture.star_android.im.config;


/**
 * @author lyl
 * @Title: UserConfig
 * @Description: 用户信息配置
 * @date 2019/3/15 14:41
 */

public class UserConfig {


    public final static Long FRIEND_ID = 1560325174062L;

    private static Long userId = 1560690539342L;

    private static String userName = "ywq";
    //TODO lyl
//    public final static Long FRIEND_ID = 1560690539342L;
//
//    private static Long userId = 1560325174062L;
//
//    private static String userName = "lyl";

    public static String getUserName() {
        return userName;
    }

    public static Long getUserId() {
        return userId;
    }

    public static void setUserId(Long userId) {
        UserConfig.userId = userId;
    }

    public static void setUserName(String userName) {
        UserConfig.userName = userName;
    }
}
