package com.single.mvvm;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.single.mvvm.adapter.RecyclerViewDatabingAdapter;
import com.single.mvvm.common.CommonDaggerActivity;
import com.single.mvvm.databinding.ActivityMainBinding;
import com.single.mvvm.databinding.ListitemNewsBinding;
import com.single.mvvm.model.MainModel;
import com.single.mvvm.service.NewsService;
import com.single.mvvm.service.TopNewsService;
import com.single.mvvm.utils.DateUtils;
import com.single.mvvm.utils.GlideImageLoader;
import com.youth.banner.BannerConfig;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

public class MainActivity extends CommonDaggerActivity<ActivityMainBinding> {
    private static final String TAG = MainActivity.class.getSimpleName();
    private MainModel mainModel;
    private boolean hasInitRecyclerView;

    private int index;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_main;
    }

    @Override
    protected void doCreateView(Bundle savedInstanceState) {
        mainModel = getViewModel(MainModel.class);
        loadNews();
        loadTopNews();
    }

    private void loadTopNews() {
        mainModel.loadTopNews().observe(this, newsResource -> {
            switch (newsResource.status) {
                case EMPTY:
                    break;
                case SUCCESS:
                    Log.d(TAG, "newsResource:" + newsResource.data.toString());
//                    getDataBing().setIsLoading(false);
//                    if (!hasInitRecyclerView) {
                    initBananer(newsResource.data.getTop_stories());
//                        hasInitRecyclerView = true;
//                    }
                    break;
                case LOADING:
//                    getDataBing().setIsLoading(true);
                    break;
                case ERROR:
                    Log.d(TAG, "newsResource:" + newsResource.message);
                    break;
            }
        });
    }

    private void loadNews() {
        Observable<String> dateByIndex = DateUtils.getDateByIndex(index);
        Observable<String> dbDateByIndex = DateUtils.getDbDateByIndex(index);
        dateByIndex.subscribe(s -> {
            dbDateByIndex.subscribe(s1 -> {
                mainModel.loadZhihuNews(s, s1).observe(this, newsResource -> {
                    switch (newsResource.status) {
                        case EMPTY:
                            break;
                        case SUCCESS:
                            Log.d(TAG, "newsResource:" + newsResource.data.toString());
                            getDataBing().setIsLoading(false);
                            if (!hasInitRecyclerView) {
                                initRecyclerView(newsResource.data.getStories());
                                hasInitRecyclerView = true;
                            }
                            break;
                        case LOADING:
                            getDataBing().setIsLoading(true);
                            break;
                        case ERROR:
                            Log.d(TAG, "newsResource:" + newsResource.message);
                            break;
                    }
                });
            });
        });
    }

    private void initRecyclerView(List<NewsService.News.StoriesBean> storiesBeans) {
        getDataBing().listNews.setLayoutManager(new LinearLayoutManager(this));
        getDataBing().listNews.addItemDecoration(new DividerItemDecoration(this));
        getDataBing().listNews.setAdapter(new RecyclerViewDatabingAdapter<NewsService.News.StoriesBean, ListitemNewsBinding>(storiesBeans) {
            @Override
            protected int getItemLayout() {
                return R.layout.listitem_news;
            }
        });
        getDataBing().listNews.setVisibility(View.VISIBLE);

    }

    private void initBananer(List<TopNewsService.News.TopStoriesBean> top_stories) {
        getDataBing().bannerView.setBannerStyle(BannerConfig.NUM_INDICATOR_TITLE);
        getDataBing().bannerView.setImageLoader(new GlideImageLoader());
        List<String> images = new ArrayList<>();
        List<String> titles = new ArrayList<>();
        for (int i = 0; i < top_stories.size(); i++) {
            images.add(top_stories.get(i).getImage());
            titles.add(top_stories.get(i).getTitle());
        }
        getDataBing().bannerView.setImages(images);
        getDataBing().bannerView.setBannerTitles(titles);
        getDataBing().bannerView.start();
    }

    public static class DividerItemDecoration extends RecyclerView.ItemDecoration {
        private Drawable mDivider;

        public DividerItemDecoration(Context context) {
            mDivider = context.getResources().getDrawable(R.drawable.divider);
        }

        @Override
        public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
            int left = parent.getPaddingLeft();
            int right = parent.getWidth() - parent.getPaddingRight();

            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = parent.getChildAt(i);

                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

                int top = child.getBottom() + params.bottomMargin;
                int bottom = top + mDivider.getIntrinsicHeight();

                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }
    }

}
