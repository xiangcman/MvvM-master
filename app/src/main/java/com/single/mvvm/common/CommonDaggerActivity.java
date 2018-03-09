package com.single.mvvm.common;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

/**
 * Created by jin.jianhong on 12/12/17.
 */

public abstract class CommonDaggerActivity<T extends ViewDataBinding> extends AppCompatActivity {

    private T dataBing;

    @Inject
    public ViewModelFactory viewModelFactory;//这里实际上已经依赖注入了

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        // 让子类的activity支持dagger2
        AndroidInjection.inject(this);

        super.onCreate(savedInstanceState);

        dataBing = DataBindingUtil.setContentView(this, getLayoutRes());

        doCreateView(savedInstanceState);
    }

    protected abstract int getLayoutRes();

    protected abstract void doCreateView(Bundle savedInstanceState);

    protected T getDataBing() {
        return dataBing;
    }

    public <V extends ViewModel> V getViewModel(Class<V> viewModelClass) {
        return ViewModelProviders.of(this, viewModelFactory).get(viewModelClass);
    }
}
