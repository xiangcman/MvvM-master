package com.single.mvvm;

import android.app.Activity;
import android.app.Application;

import com.facebook.stetho.Stetho;
import com.single.mvvm.di.DaggerAppComponent;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;

/**
 * Created by xiangcheng on 18/3/6.
 */

public class MyApplication extends Application implements HasActivityInjector {
    @Inject
    DispatchingAndroidInjector<Activity> activityInjector;

    @Override
    public void onCreate() {
        super.onCreate();
        // Init database
        Stetho.initializeWithDefaults(this);
        // Init inject for application
        DaggerAppComponent.builder().application(this).build().inject(this);
    }

    @Override
    public AndroidInjector<Activity> activityInjector() {
        return activityInjector;
    }
}
