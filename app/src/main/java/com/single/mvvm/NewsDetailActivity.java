package com.single.mvvm;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.view.MenuItem;

import com.single.mvvm.common.CommonDaggerActivity;
import com.single.mvvm.databinding.ActivityDetailBinding;
import com.single.mvvm.retrofit.RetrofitProvider;
import com.single.mvvm.service.NewsDetailService;
import com.single.mvvm.utils.ViewUtils;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by xiangcheng on 18/3/13.
 */

public class NewsDetailActivity extends CommonDaggerActivity<ActivityDetailBinding> {
    public static final String EXTRA_NEWS_ID = "extra_news_id";

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_detail;
    }

    @Override
    protected void doCreateView(Bundle savedInstanceState) {
        long longExtra = getIntent().getLongExtra(EXTRA_NEWS_ID, 0);
        getDataBing().setIsLoading(true);
        doNewsDetail(longExtra);

        getDataBing().refreshLayout.setOnRefreshListener(() -> doNewsDetail(longExtra));

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(getDataBing().toolbar);
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
//        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedText);
//        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedTitleText);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getDataBing().appBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            int height = appBarLayout.getHeight() - getSupportActionBar().getHeight() - ViewUtils.getStatusBarHeight(NewsDetailActivity.this);
            int alpha = 255 * (0 - verticalOffset) / height;
            //在扩张的时候，透明度是直接为0的
            getDataBing().collapsingToolbar.setExpandedTitleColor(Color.argb(0, 255, 255, 255));
            //在合拢的时候，透明度是不断增大的
            getDataBing().collapsingToolbar.setCollapsedTitleTextColor(Color.argb(alpha, 255, 255, 255));
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    //实际网络请求的代码写在model里面
    private void doNewsDetail(long longExtra) {
        RetrofitProvider.getInstance().create(NewsDetailService.class).getNewsDetail(longExtra)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<NewsDetailService.NewsDetail>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(NewsDetailService.NewsDetail news) {
                        getDataBing().setIsLoading(false);
                        getDataBing().setDetail(news);
                        Observable.just(news.getBody())
                                .map(s -> s + "<style type=\"text/css\">" + news.getCssStr())
                                .map(s -> s + "</style>")
                                .subscribe(s -> getDataBing().setUrl(s));
                        getDataBing().refreshLayout.setRefreshing(false);
                    }
                });
    }
}
