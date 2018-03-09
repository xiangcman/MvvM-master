package com.single.mvvm.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.single.mvvm.common.NetworkBoundResource;
import com.single.mvvm.common.Resource;
import com.single.mvvm.db.dao.ExtraFieldDao;
import com.single.mvvm.db.dao.ListNewsDao;
import com.single.mvvm.db.dao.TopNewsDao;
import com.single.mvvm.entity.ExtraField;
import com.single.mvvm.entity.ListNews;
import com.single.mvvm.retrofit.RetrofitProvider;
import com.single.mvvm.service.NewsService;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by xiangcheng on 18/3/6.
 */
@Singleton
public class ZhihuNewRepository {
    private static final String TAG = ZhihuNewRepository.class.getSimpleName();
    private ExtraFieldDao extraFieldDao;
    private ListNewsDao listNewsDao;
    private TopNewsDao topNewsDao;

    @Inject
    public ZhihuNewRepository(ExtraFieldDao extraFieldDao, ListNewsDao listNewsDao, TopNewsDao topNewsDao) {
        this.extraFieldDao = extraFieldDao;
        this.listNewsDao = listNewsDao;
        this.topNewsDao = topNewsDao;
    }

    public LiveData<Resource<NewsService.News>> reload(final String date, final String dbDate) {

        Log.d(TAG, "date:" + date);
        Log.d(TAG, "dbDate:" + dbDate);

        return new NetworkBoundResource<NewsService.News, NewsService.News>() {

            @Override
            protected void saveCallResult(@NonNull NewsService.News news) {
                List<NewsService.News.StoriesBean> storiesBeans = news.getStories();
                for (int i = 0; i < storiesBeans.size(); i++) {
                    NewsService.News.StoriesBean storiesBean = storiesBeans.get(i);
                    ListNews listNews = new ListNews();
                    listNews.setTitle(storiesBean.getTitle());
                    listNews.setDate(news.getDate());
                    listNews.setGa_prefix(storiesBean.getGa_prefix());
                    listNews.setId(storiesBean.getId());
                    listNews.setType(storiesBean.getType());
                    listNews.setMultipic(storiesBean.isMultipic());
                    List<String> images = storiesBean.getImages();
                    StringBuilder sb = new StringBuilder();
                    for (int j = 0; j < images.size(); j++) {
                        sb.append(images.get(j) + ",");
                    }
                    listNews.setImages(sb.toString().substring(0, sb.toString().length() - 1));

                    listNewsDao.save(listNews);
                    NewsService.News.StoriesBean.ExtraField extraField = storiesBean.getExtraField();
                    if (extraField != null) {
                        ExtraField dbextraField = new ExtraField(storiesBean.getExtraField().isHeader(), storiesBean.getExtraField().getDate());
                        extraFieldDao.save(dbextraField);
                    }
                }

            }

            @Override
            protected boolean shouldFetch(@Nullable NewsService.News news) {
                return news.getStories() == null || news.getStories().size() <= 0;
            }

            @NonNull
            @Override
            protected LiveData<NewsService.News> loadFromDb() {
                MediatorLiveData<NewsService.News> result = new MediatorLiveData<>();
                LiveData<List<ListNews>> listNewsLiveData = listNewsDao.selectListNews(dbDate);
                result.addSource(listNewsLiveData, listNews -> {
                    result.removeSource(listNewsLiveData);
                    NewsService.News news = new NewsService.News();
                    List<NewsService.News.StoriesBean> storiesBean = new ArrayList<>();
                    for (int i = 0; i < listNews.size(); i++) {
                        ListNews listNews1 = listNews.get(i);
                        NewsService.News.StoriesBean storiesBean1 = new NewsService.News.StoriesBean();
                        storiesBean1.setId(listNews1.getId());
                        storiesBean1.setGa_prefix(listNews1.getGa_prefix());
                        List<String> images = new ArrayList<>();
                        String images1 = listNews1.getImages();
                        String[] split = images1.split(",");
                        for (int j = 0; j < split.length; j++) {
                            images.add(split[j]);
                        }
                        storiesBean1.setImages(images);
                        storiesBean1.setMultipic(listNews1.isMultipic());
                        storiesBean1.setTitle(listNews1.getTitle());
                        storiesBean1.setType(listNews1.getType());
                        LiveData<ExtraField> extraFieldLiveData = extraFieldDao.loadExtraField(date);
                        result.addSource(extraFieldLiveData, extraField -> {
                            result.removeSource(extraFieldLiveData);
                            if (extraField != null) {
                                NewsService.News.StoriesBean.ExtraField extraField1 =
                                        new NewsService.News.StoriesBean.ExtraField(extraField.isHeader(), extraField.getDate());
                                storiesBean1.setExtraField(extraField1);
                            }
                            storiesBean.add(storiesBean1);
                        });

                    }
                    news.setStories(storiesBean);
                    news.setDate(date);
                    result.setValue(news);
                });
                return result;
            }

            @NonNull
            @Override
            protected LiveData<Resource<NewsService.News>> createCall(NewsService.News config) {
                MutableLiveData<Resource<NewsService.News>> result = new MutableLiveData<>();
                //todo network
                //just关键字只是将多个数据遍历，在观察者里面直接可以拿来用
//                Observable.just(Calendar.getInstance())
//                        //doOnNewxt里面也相当于一个观察者，只不过里面只是简单的获取just中传过来的数据
//                        .doOnNext(calendar -> calendar.add(Calendar.DAY_OF_MONTH, 1))
//                        //map关键字是将一种数据类型转换成另外一种
//                        .map(calendar -> NewsListHelper.DAY_FORMAT.format(calendar.getTime()))
//                        .subscribe(s -> loadNewsList(s, result));

                loadNewsList(date, result);
                return result;
            }
        }.getAsLiveData();
    }

    private void loadNewsList(String date, MutableLiveData<Resource<NewsService.News>> result) {
        RetrofitProvider.getInstance().create(NewsService.class)
                .getNewsList(date)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<NewsService.News>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        result.setValue(Resource.error(e.getMessage(), null));
                    }

                    @Override
                    public void onNext(NewsService.News news) {
                        result.setValue(Resource.success(news));
                    }
                });

    }
}
