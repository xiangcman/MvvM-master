package com.single.mvvm.db;

/**
 * Created by jin.jianhong on 12/10/17.
 */

import android.app.Application;
import android.arch.persistence.room.Room;

import com.single.mvvm.db.dao.ExtraFieldDao;
import com.single.mvvm.db.dao.ListNewsDao;
import com.single.mvvm.db.dao.TopNewsDao;
import com.single.mvvm.di.ViewModelModule;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(includes = ViewModelModule.class)
public class DbModule {
    //region DB
    @Singleton
    @Provides
    AppDatabase provideDb(Application app) {
        return Room.databaseBuilder(app,
                AppDatabase.class, "zhihu_database")
                .fallbackToDestructiveMigration()
                .build();
    }

    @Singleton
    @Provides
    ListNewsDao provideConfigDao(AppDatabase db) {
        return db.listNewsDao();
    }

    @Singleton
    @Provides
    TopNewsDao provideDeviceDao(AppDatabase db) {
        return db.topNewsDao();
    }

    @Singleton
    @Provides
    ExtraFieldDao provideMotocareServiceDao(AppDatabase db) {
        return db.extraFieldDao();
    }

}
