package com.jjpicture.star_android.data.service;


import com.jjpicture.star_android.data.HttpDataSource;
import com.jjpicture.star_android.entity.WeatherEntity;

import io.reactivex.Observable;

/**
 * Created by goldze on 2019/3/26.
 */
public class HttpDataSourceImpl implements HttpDataSource {
    private TestService apiService;
    private volatile static HttpDataSourceImpl INSTANCE = null;

    public static HttpDataSourceImpl getInstance(TestService apiService) {
        if (INSTANCE == null) {
            synchronized (HttpDataSourceImpl.class) {
                if (INSTANCE == null) {
                    INSTANCE = new HttpDataSourceImpl(apiService);
                }
            }
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    private HttpDataSourceImpl(TestService apiService) {
        this.apiService = apiService;
    }

    @Override
    public Observable<WeatherEntity> testGet(String city) {
        return apiService.getMessage(city);
    }
}
