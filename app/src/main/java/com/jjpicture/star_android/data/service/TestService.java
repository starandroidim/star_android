package com.jjpicture.star_android.data.service;



import com.jjpicture.star_android.entity.WeatherEntity;


import io.reactivex.Observable;

import retrofit2.http.GET;

import retrofit2.http.Query;


/**
 * Created by goldze on 2017/6/15.
 */

public interface TestService {
    @GET("weather_mini")
//  此处回调返回的可为任意类型Call<T>，再也不用自己去解析json数据啦！！！
    Observable<WeatherEntity> getMessage(@Query("city") String city);

}
