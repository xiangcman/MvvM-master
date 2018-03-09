package com.single.mvvm.di;

import com.single.mvvm.MainActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by jin.jianhong on 12/12/17.
 */

@Module
public abstract class ActivityModule {
    @ActivityScoped
    @ContributesAndroidInjector
    /*(modules = MainModule.class) can add modules for activity like this*/
    abstract MainActivity mainActivity();

}
