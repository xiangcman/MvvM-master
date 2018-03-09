package com.single.mvvm.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by xiangcheng on 18/3/6.
 */
@Entity
public class ExtraField {
    private boolean isHeader;
    private String date;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @PrimaryKey(autoGenerate = true)
    private long id;

    public ExtraField(boolean isHeader, String date) {
        this.isHeader = isHeader;
        this.date = date;
    }

    public boolean isHeader() {
        return isHeader;
    }

    public void setHeader(boolean header) {
        isHeader = header;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
