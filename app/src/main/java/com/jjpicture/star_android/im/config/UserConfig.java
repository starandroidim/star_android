package com.jjpicture.star_android.im.config;


/**
 * @author lyl
 * @Title: UserConfig
 * @Description: 用户信息配置
 * @date 2019/3/15 14:41
 */

public class UserConfig {

    //TODO lyl
 public final static Long FRIEND_ID = 1555912992478L;

    private static Long userId = 1552465428829L;

    private static String userName = "lyl";

//    public final static Long FRIEND_ID = 1552465428829L;

 //   private static Long userId = 1555912992478L;

   // private static String userName = "test";

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
