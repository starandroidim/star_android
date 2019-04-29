package com.jjpicture.mvvmstar.im_common;

public enum CodeEnum {
    SUCCESS("9000", "成功"),
    REPEAT_LOGIN("5000", "账号重复登录，请退出一个账号！"),
    ACCOUNT_NOT_MATCH("9100", "登录信息不匹配！"),
    REDIS_FAIL("9200","REDIS操作失效"),
    FAIL("4000", "失败");
    private final String code;
    private final String message;

    CodeEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
