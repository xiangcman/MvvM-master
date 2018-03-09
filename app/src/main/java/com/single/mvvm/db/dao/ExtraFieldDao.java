package com.single.mvvm.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.single.mvvm.entity.ExtraField;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;
/**
 * Created by xiangcheng on 18/3/6.
 */
@Dao
public interface ExtraFieldDao {

    @Query("SELECT * FROM ExtraField where date= :date")
    LiveData<ExtraField> loadExtraField(String date);

    @Insert(onConflict = REPLACE)
    void save(ExtraField extraField);

    @Update
    void update(ExtraField extraField);
}
