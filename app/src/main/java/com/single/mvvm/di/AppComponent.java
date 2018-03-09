package com.single.mvvm.di;

/**
 * Created by jin.jianhong on 12/10/17.
 */

import android.app.Application;

import com.single.mvvm.MyApplication;
import com.single.mvvm.db.DbModule;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;

@Singleton
@Component(modules = {
        DbModule.class,
        ActivityModule.class,
        AndroidSupportInjectionModule.class
})
public interface AppComponent extends AndroidInjector<MyApplication>{
    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(Application application);
        AppComponent build();
    }
}
