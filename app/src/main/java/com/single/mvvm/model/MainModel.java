package com.single.mvvm.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.single.mvvm.common.Resource;
import com.single.mvvm.repository.ZhihuNewRepository;
import com.single.mvvm.service.NewsService;

import javax.inject.Inject;

/**
 * Created by xiangcheng on 18/2/28.
 */

public class MainModel extends AndroidViewModel {
    private static final String TAG = MainModel.class.getSimpleName();
    public ZhihuNewRepository zhihuNewRepository;

    @Inject
    public MainModel(@NonNull Application application, ZhihuNewRepository zhihuNewRepository) {
        super(application);
        this.zhihuNewRepository = zhihuNewRepository;
    }

    public LiveData<Resource<NewsService.News>> loadZhihuNews(String date,String dbDate) {
        Log.d(TAG, "loadZhihuNews");
        return zhihuNewRepository.reload(date,dbDate);
    }
}
