package com.jjpicture.star_android.data;

import com.jjpicture.star_android.entity.WeatherEntity;

import io.reactivex.Observable;


/**
 * Created by goldze on 2019/3/26.
 */
public interface HttpDataSource {
    Observable<WeatherEntity> testGet(String city);
}
