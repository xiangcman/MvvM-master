package com.single.mvvm.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.single.mvvm.entity.TopNews;
import static android.arch.persistence.room.OnConflictStrategy.REPLACE;
/**
 * Created by xiangcheng on 18/3/6.
 */
@Dao
public interface TopNewsDao {
    @Query("SELECT * FROM TopNews where date =:date")
    LiveData<TopNews> selectTopNews(String date);
    @Insert(onConflict = REPLACE)
    void save(TopNews topNews);

    @Update
    void update(TopNews topNews);
}
