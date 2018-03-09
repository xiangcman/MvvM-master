package com.single.mvvm.di;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.single.mvvm.common.ViewModelFactory;
import com.single.mvvm.model.MainModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(MainModel.class)
    abstract ViewModel bindMainViewModel(MainModel mainViewModel);


    abstract ViewModelProvider.Factory bindViewModelFactory(ViewModelFactory factory);
}