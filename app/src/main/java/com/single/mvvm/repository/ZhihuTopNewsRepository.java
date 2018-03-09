package com.single.mvvm.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.single.mvvm.common.NetworkBoundResource;
import com.single.mvvm.common.Resource;
import com.single.mvvm.db.dao.TopNewsDao;
import com.single.mvvm.entity.TopNews;
import com.single.mvvm.retrofit.RetrofitProvider;
import com.single.mvvm.service.TopNewsService;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by xiangcheng on 18/3/9.
 */
@Singleton
public class ZhihuTopNewsRepository {
    public TopNewsDao topNewsDao;

    @Inject
    public ZhihuTopNewsRepository(TopNewsDao topNewsDao) {
        this.topNewsDao = topNewsDao;
    }

    public LiveData<Resource<TopNewsService.News>> reload() {
        return new NetworkBoundResource<TopNewsService.News, TopNewsService.News>() {
            @Override
            protected void saveCallResult(@NonNull TopNewsService.News item) {
                List<TopNewsService.News.TopStoriesBean> top_stories = item.getTop_stories();
                Observable.from(top_stories).subscribe(topStoriesBean -> {
                    TopNews topNews = new TopNews();
                    topNews.setGa_prefix(topStoriesBean.getGa_prefix());
                    topNews.setId(topStoriesBean.getId());
                    topNews.setImage(topStoriesBean.getImage());
                    topNews.setTitle(topStoriesBean.getTitle());
                    topNews.setType(topStoriesBean.getType());
                    topNewsDao.save(topNews);
                });
            }

            @Override
            protected boolean shouldFetch(@Nullable TopNewsService.News data) {
                return data.getTop_stories() == null || data.getTop_stories().size() <= 0;
            }

            @NonNull
            @Override
            protected LiveData<TopNewsService.News> loadFromDb() {
                LiveData<List<TopNews>> listLiveData = topNewsDao.selectTopNews();
                MediatorLiveData<TopNewsService.News> result = new MediatorLiveData<>();
                result.addSource(listLiveData, topNews -> {
                    result.removeSource(listLiveData);
                    TopNewsService.News news = new TopNewsService.News();
                    List<TopNewsService.News.TopStoriesBean> topStoriesBeans = new ArrayList<>();
                    for (int i = 0; i < topNews.size(); i++) {
                        TopNews topNews1 = topNews.get(i);
                        TopNewsService.News.TopStoriesBean topStoriesBean = new TopNewsService.News.TopStoriesBean();
                        topStoriesBean.setGa_prefix(topNews1.getGa_prefix());
                        topStoriesBean.setId(topNews1.getId());
                        topStoriesBean.setImage(topNews1.getImage());
                        topStoriesBean.setTitle(topNews1.getTitle());
                        topStoriesBean.setType(topNews1.getType());
                        topStoriesBeans.add(topStoriesBean);
                    }
                    news.setTop_stories(topStoriesBeans);
                    result.setValue(news);
                });
                return result;
            }

            @NonNull
            @Override
            protected LiveData<Resource<TopNewsService.News>> createCall(@Nullable TopNewsService.News data) {
                MutableLiveData<Resource<TopNewsService.News>> result = new MutableLiveData<>();
                RetrofitProvider.getInstance().create(TopNewsService.class).getTopNewsList()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<TopNewsService.News>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                result.setValue(Resource.error(e.getMessage(), null));
                            }

                            @Override
                            public void onNext(TopNewsService.News news) {
                                result.setValue(Resource.success(news));
                            }
                        });
                return result;
            }
        }.getAsLiveData();
    }
}
