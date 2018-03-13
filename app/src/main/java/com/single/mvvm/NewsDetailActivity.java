package com.single.mvvm;

import android.os.Bundle;

import com.single.mvvm.common.CommonDaggerActivity;
import com.single.mvvm.databinding.ActivityDetailBinding;

/**
 * Created by xiangcheng on 18/3/13.
 */

public class NewsDetailActivity extends CommonDaggerActivity<ActivityDetailBinding> {
    @Override
    protected int getLayoutRes() {
        return R.layout.activity_detail;
    }

    @Override
    protected void doCreateView(Bundle savedInstanceState) {


    }
}
