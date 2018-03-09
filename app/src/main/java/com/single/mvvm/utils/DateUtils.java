package com.single.mvvm.utils;

import java.util.Calendar;
import java.util.Date;

import rx.Observable;

/**
 * Created by xiangcheng on 18/3/9.
 */

public class DateUtils {
    //index从0开始，如果index为0，那么返回的就是今天
    public static Observable<String> getDateByIndex(int index) {
        return Observable.just(Calendar.getInstance())
                .doOnNext(calendar -> {
                    calendar.setTime(new Date());
                    calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - index);
                }).map(calendar -> NewsListHelper.DAY_FORMAT.format(calendar.getTime()));
    }

    //由于是数据的原因，因此这里从数据库查询的时候，需要天数减去1
    public static Observable<String> getDbDateByIndex(int index) {
        return Observable.just(Calendar.getInstance())
                .doOnNext(calendar -> {
                    calendar.setTime(new Date());
                    calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - index - 1);
                }).map(calendar -> NewsListHelper.DAY_FORMAT.format(calendar.getTime()));
    }
}
