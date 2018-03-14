package com.single.mvvm;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.single.mvvm.adapter.RecyclerViewDatabingAdapter;
import com.single.mvvm.common.CommonDaggerActivity;
import com.single.mvvm.databinding.ActivityMainBinding;
import com.single.mvvm.databinding.ListitemNewsBinding;
import com.single.mvvm.databinding.ListtitleNewsBinding;
import com.single.mvvm.entity.StoriesBean;
import com.single.mvvm.model.MainModel;
import com.single.mvvm.service.NewsService;
import com.single.mvvm.service.TopNewsService;
import com.single.mvvm.utils.DateUtils;
import com.single.mvvm.utils.GlideImageLoader;
import com.single.mvvm.utils.ViewUtils;
import com.youth.banner.BannerConfig;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

public class MainActivity extends CommonDaggerActivity<ActivityMainBinding>
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private MainModel mainModel;

    private boolean hasLoadTopNews;

    private int index;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_main;
    }

    @Override
    protected void doCreateView(Bundle savedInstanceState) {
        mainModel = getViewModel(MainModel.class);
        initdraw();
        loadTopNews();
        loadNews();
        initRefreshLayout();
    }

    private void initdraw() {
        setSupportActionBar(getDataBing().toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        getDataBing().appBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            int height = appBarLayout.getHeight() - getSupportActionBar().getHeight() - ViewUtils.getStatusBarHeight(MainActivity.this);
            int alpha = 255 * (0 - verticalOffset) / height;
            //在扩张的时候，透明度是直接为0的
            getDataBing().collapsingToolbar.setExpandedTitleColor(Color.argb(0, 255, 255, 255));
            //在合拢的时候，透明度是不断增大的
            getDataBing().collapsingToolbar.setCollapsedTitleTextColor(Color.argb(alpha, 255, 255, 255));
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, getDataBing().drawerLayout, getDataBing().toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        getDataBing().drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        getDataBing().navView.setNavigationItemSelectedListener(this);
    }

    private void loadTopNews() {
        //获取今天的日子
        Observable<String> dateByIndex = DateUtils.getDateByIndex(0);
        dateByIndex.subscribe(today -> mainModel.loadTopNews(today).observe(this, newsResource -> {
            switch (newsResource.status) {
                case EMPTY:
                    break;
                case SUCCESS:
                    Log.d(TAG, "newsResource:" + newsResource.data.toString());
                    initBananer(newsResource.data.getTop_stories());
                    break;
                case LOADING:
                    break;
                case ERROR:
                    Log.d(TAG, "newsResource:" + newsResource.message);
                    break;
            }
        }));
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
                            isLoadMore = false;
                            if (!hasLoadTopNews) {
                                getDataBing().setIsLoading(false);
                                initRecyclerView(newsResource.data.getStories(), newsResource.data, index == 0 ? true : false);
                                hasLoadTopNews = true;
                                index = 1;
                            } else {
                                initRecyclerView(newsResource.data.getStories(), newsResource.data, index == 0 ? true : false);
                                index++;
                            }
                            break;
                        case LOADING:
                            if (!hasLoadTopNews) {
                                getDataBing().setIsLoading(true);
                            }
                            break;
                        case ERROR:
                            Log.d(TAG, "newsResource:" + newsResource.message);
                            break;
                    }
                });
            });
        });
    }

    RecyclerViewDatabingAdapter recyclerViewDatabingAdapter;

    private boolean isLoadMore;

    private LinearLayoutManager layoutManager;

    private void initRecyclerView(List<StoriesBean> storiesBeans, NewsService.News item, boolean isRefesh) {

        StoriesBean storiesBean = new StoriesBean();
        storiesBean.setShowSuspension(true);
        storiesBean.setSuspensionTag(item.getDate());
        storiesBeans.add(0, storiesBean);

        if (recyclerViewDatabingAdapter == null) {

            getDataBing().listNews.setLayoutManager(layoutManager = new LinearLayoutManager(this));
            getDataBing().listNews.addItemDecoration(new DividerItemDecoration(this));

            getDataBing().listNews.setAdapter(recyclerViewDatabingAdapter = new RecyclerViewDatabingAdapter<StoriesBean, ListitemNewsBinding, ListtitleNewsBinding>(storiesBeans) {
                @Override
                protected int getTitleLayout() {
                    return R.layout.listtitle_news;
                }

                @Override
                protected int getItemLayout() {
                    return R.layout.listitem_news;
                }

                @Override
                protected void itemClick(StoriesBean item) {
                    mainModel.updateStoriesBean(item.getId()).observe(MainActivity.this, storiesBeanResource -> {
                        switch (storiesBeanResource.status) {
                            case SUCCESS:
                                item.setRead(true);
                                recyclerViewDatabingAdapter.notifyDataSetChanged();
                                Intent intent = new Intent(MainActivity.this, NewsDetailActivity.class);
                                intent.putExtra(NewsDetailActivity.EXTRA_NEWS_ID, item.getId());
                                startActivity(intent);
                                break;
                        }
                    });

                }
            });
            getDataBing().listNews.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (newState == RecyclerView.SCROLL_STATE_IDLE && !isLoadMore &&
                            layoutManager.findLastVisibleItemPosition() + 1 == recyclerViewDatabingAdapter.getItemCount()) {
                        isLoadMore = true;
                        Log.d(TAG, "loadMore");
                        loadNews();
                    }
                }
            });
        } else {
            if (isRefesh) {
                recyclerViewDatabingAdapter.refreshData(storiesBeans);
                getDataBing().refreshLayout.setRefreshing(false);
            } else {
                recyclerViewDatabingAdapter.loadMore(storiesBeans);
            }
        }
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
        getDataBing().bannerView.setOnBannerListener(i -> {
            Intent intent = new Intent(MainActivity.this, NewsDetailActivity.class);
            intent.putExtra(NewsDetailActivity.EXTRA_NEWS_ID, top_stories.get(i).getId());
            startActivity(intent);
        });
    }

    private void initRefreshLayout() {
        getDataBing().refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                index = 0;
                loadNews();
            }
        });
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        getDataBing().drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

}
