package com.jjpicture.mvvmstar.im_common.util;

import com.alibaba.fastjson.JSONObject;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.util.Map;

public class HttpRequestFactory {

    private static MediaType mediaType = MediaType.parse("application/json");
    public  static Request buildPost(Map<String,Object> req, String url) {
        JSONObject jsonObject = new JSONObject();

        for(Map.Entry<String,Object> entry: req.entrySet()) {
            jsonObject.put(entry.getKey(),entry.getValue());
        }
        RequestBody requestBody = RequestBody.create(mediaType,jsonObject.toString());
        Request request = new Request.Builder()
                .url(url)  // 路由服务器地址
                .post(requestBody)
                .build();
        return request;
    }

}
