package com.single.mvvm.common;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class Resource<T> {
    @NonNull
    public final Status status;
    @Nullable
    public final T data;
    @Nullable
    public final String message;
    @Nullable
    public final String tag;

    private Resource(@NonNull Status status, @Nullable T data, @Nullable String message, @Nullable String tag) {
        this.status = status;
        this.data = data;
        this.message = message;
        this.tag = tag;
    }

    public static <T> Resource<T> success(@NonNull T data) {
        return new Resource<>(Status.SUCCESS, data, null, null);
    }

    public static <T> Resource<T> error(String msg, @Nullable T data) {
        return new Resource<>(Status.ERROR, data, msg, null);
    }

    public static <T> Resource<T> loading(@Nullable T data) {
        return new Resource<>(Status.LOADING, data, null, null);
    }

    public static <T> Resource<T> empty() {
        return new Resource<>(Status.EMPTY, null, null, null);
    }

    public static <T> Resource<T> reload(@Nullable T data) {
        return new Resource<>(Status.RELOAD, data, null, null);
    }

    public static <T> Resource<T> success(@NonNull T data, String tag) {
        return new Resource<>(Status.SUCCESS, data, null, tag);
    }

    public static <T> Resource<T> error(String msg, @Nullable T data, String tag) {
        return new Resource<>(Status.ERROR, data, msg, tag);
    }

    public static <T> Resource<T> loading(@Nullable T data, String tag) {
        return new Resource<>(Status.LOADING, data, null, tag);
    }

    public static <T> Resource<T> empty(String tag) {
        return new Resource<>(Status.EMPTY, null, null, tag);
    }

    public static <T> Resource<T> reload(@Nullable T data, String tag) {
        return new Resource<>(Status.RELOAD, data, null, tag);
    }

    @Override
    public String toString() {
        return "Resource{" +
                "status=" + status +
                ", data=" + data +
                ", message='" + message + '\'' +
                '}';
    }
}
