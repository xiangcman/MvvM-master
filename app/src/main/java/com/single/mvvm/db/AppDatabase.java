package com.single.mvvm.db;

import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.DatabaseConfiguration;
import android.arch.persistence.room.InvalidationTracker;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import com.single.mvvm.db.dao.ExtraFieldDao;
import com.single.mvvm.db.dao.ListNewsDao;
import com.single.mvvm.db.dao.TopNewsDao;
import com.single.mvvm.entity.ExtraField;
import com.single.mvvm.entity.ListNews;
import com.single.mvvm.entity.TopNews;

/**
 * Created by xiangcheng on 18/3/6.
 */

@Database(entities = {
        ListNews.class,
        TopNews.class,
        ExtraField.class,
        }, version = 3)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    public abstract ListNewsDao listNewsDao();

    public abstract TopNewsDao topNewsDao();

    public abstract ExtraFieldDao extraFieldDao();


    @Override
    protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration config) {
        return null;
    }

    @Override
    protected InvalidationTracker createInvalidationTracker() {
        return null;
    }
}
