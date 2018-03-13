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
import com.single.mvvm.entity.ExtraField;
import com.single.mvvm.entity.ListNews;
import com.single.mvvm.entity.StoriesBean;
import com.single.mvvm.retrofit.RetrofitProvider;
import com.single.mvvm.service.NewsService;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
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

    @Inject
    public ZhihuNewRepository(ExtraFieldDao extraFieldDao, ListNewsDao listNewsDao) {
        this.extraFieldDao = extraFieldDao;
        this.listNewsDao = listNewsDao;
    }

    public LiveData<Resource<NewsService.News>> reload(final String date, final String dbDate) {

        Log.d(TAG, "date:" + date);

        return new NetworkBoundResource<NewsService.News, NewsService.News>() {
            @Override
            protected void saveCallResult(@NonNull NewsService.News news) {
                List<StoriesBean> storiesBeans = news.getStories();
                Observable.from(storiesBeans).filter(storiesBean -> storiesBean != null).subscribe(storiesBean -> {
                    ListNews listNews = new ListNews();
                    listNews.setTitle(storiesBean.getTitle());
                    listNews.setDate(news.getDate());
                    listNews.setGa_prefix(storiesBean.getGa_prefix());
                    listNews.setId(storiesBean.getId());
                    listNews.setType(storiesBean.getType());
                    listNews.setMultipic(storiesBean.isMultipic());
                    List<String> images = storiesBean.getImages();
                    StringBuilder sb = new StringBuilder();
                    Observable.from(images).subscribe(s -> sb.append(s + ","));
                    listNews.setImages(sb.toString().substring(0, sb.toString().length() - 1));
                    listNewsDao.save(listNews);
                    StoriesBean.ExtraField extraField = storiesBean.getExtraField();
                    if (extraField != null) {
                        ExtraField dbextraField = new ExtraField(storiesBean.getExtraField().isHeader(), storiesBean.getExtraField().getDate());
                        extraFieldDao.save(dbextraField);
                    }
                });
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
                    List<StoriesBean> storiesBean = new ArrayList<>();
                    for (int i = 0; i < listNews.size(); i++) {
                        ListNews listNews1 = listNews.get(i);
                        StoriesBean storiesBean1 = new StoriesBean();
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
                        if (extraFieldLiveData.getValue() != null) {
                            result.addSource(extraFieldLiveData, extraField -> {
                                result.removeSource(extraFieldLiveData);
                                if (extraField != null) {
                                    StoriesBean.ExtraField extraField1 =
                                            new StoriesBean.ExtraField(extraField.isHeader(), extraField.getDate());
                                    storiesBean1.setExtraField(extraField1);
                                }
                                storiesBean.add(storiesBean1);
                            });
                        } else {
                            storiesBean.add(storiesBean1);
                        }
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
