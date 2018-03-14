package com.single.mvvm.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.single.mvvm.entity.ListNews;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * Created by xiangcheng on 18/3/6.
 */
@Dao
public interface ListNewsDao {
    @Query("SELECT * FROM ListNews where date =:date")
    LiveData<List<ListNews>> selectListNews(String date);

    @Insert(onConflict = REPLACE)
    void save(ListNews listNews);

    @Update
    void update(ListNews listNews);

    @Query("SELECT * FROM ListNews where id =:id")
    LiveData<ListNews> selectListNews(long id);
}
