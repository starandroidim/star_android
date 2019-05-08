package com.jjpicture.star_android.app;

import com.jjpicture.star_android.data.HttpDataSource;
import com.jjpicture.star_android.data.IMWork;
import com.jjpicture.star_android.data.LocalDataSource;
import com.jjpicture.star_android.data.local.LocalDataSourceImpl;
import com.jjpicture.star_android.data.service.IMService;
import com.jjpicture.star_android.data.TestRepository;
import com.jjpicture.star_android.data.service.HttpDataSourceImpl;
import com.jjpicture.star_android.data.service.IMWorkImpl;
import com.jjpicture.star_android.data.service.TestService;
import com.jjpicture.star_android.utils.RetrofitClient;

public class Injection {
    public static TestRepository provideTestRepository() {
        TestService testService = RetrofitClient.getInstance().create(TestService.class);
        IMService imService = RetrofitClient.getInstance().create(IMService.class);
        HttpDataSource httpDataSource = HttpDataSourceImpl.getInstance(testService);
        LocalDataSource localDataSource = LocalDataSourceImpl.getInstance();

        IMWork imWork = IMWorkImpl.getInstance(imService);
        return TestRepository.getInstance(httpDataSource,imWork,localDataSource);
    }
}
