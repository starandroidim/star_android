package com.jjpicture.mvvmstar.im_common.response;

import java.io.Serializable;

public class BaseResponse<T> implements Serializable {
    private String code;
    private String message;
    private T dataBody;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public T getDataBody() {
        return dataBody;
    }

    public void setDataBody(T dataBody) {
        this.dataBody = dataBody;
    }

    public BaseResponse(T dataBody) {
        this.dataBody = dataBody;
    }

    public BaseResponse(String code, String message, String requestNum, T dataBody) {
        this.code = code;
        this.message = message;
        this.dataBody = dataBody;
    }

    @Override
    public String toString() {
        return "BaseResponse{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", dataBody=" + dataBody +
                '}';
    }

    public BaseResponse() {
    }
}



