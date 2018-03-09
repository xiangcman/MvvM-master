package com.single.mvvm.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.single.mvvm.common.Resource;
import com.single.mvvm.repository.ZhihuNewRepository;
import com.single.mvvm.repository.ZhihuTopNewsRepository;
import com.single.mvvm.service.NewsService;
import com.single.mvvm.service.TopNewsService;

import javax.inject.Inject;

/**
 * Created by xiangcheng on 18/2/28.
 */

public class MainModel extends AndroidViewModel {
    private static final String TAG = MainModel.class.getSimpleName();
    public ZhihuNewRepository zhihuNewRepository;
    public ZhihuTopNewsRepository zhihuTopNewsRepository;

    @Inject
    public MainModel(@NonNull Application application, ZhihuNewRepository zhihuNewRepository, ZhihuTopNewsRepository zhihuTopNewsRepository) {
        super(application);
        this.zhihuNewRepository = zhihuNewRepository;
        this.zhihuTopNewsRepository = zhihuTopNewsRepository;
    }

    public LiveData<Resource<NewsService.News>> loadZhihuNews(String date, String dbDate) {
        Log.d(TAG, "loadZhihuNews");
        return zhihuNewRepository.reload(date, dbDate);
    }

    public LiveData<Resource<TopNewsService.News>> loadTopNews() {
        return zhihuTopNewsRepository.reload();
    }
}
