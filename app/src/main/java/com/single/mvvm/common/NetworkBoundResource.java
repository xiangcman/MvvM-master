package com.single.mvvm.common;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.os.AsyncTask;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import com.single.mvvm.utils.Assert;

import java.util.Collection;

public abstract class NetworkBoundResource<ResultType, RequestType> {
    // 调用该方法将 API 响应的结果保存到数据库中。
    @WorkerThread
    protected abstract void saveCallResult(@NonNull RequestType item);

    // 调用该方法判断数据库中的数据是否应该从网络获取并更新。
    @MainThread
    protected abstract boolean shouldFetch(@Nullable ResultType data);

    // 调用该方法从数据库中获取缓存数据。
    @NonNull @MainThread
    protected abstract LiveData<ResultType> loadFromDb();

    // 调用该方法创建 API 请求。
    @NonNull @MainThread
    protected abstract LiveData<Resource<RequestType>> createCall(@Nullable ResultType data);

    // 获取失败时调用。
    @MainThread
    protected void onFetchFailed() {
    }

    // 返回一个代表 Resource 的 LiveData。
    @NonNull
    public final LiveData<Resource<ResultType>> getAsLiveData() {
        return result;
    }

    private final MediatorLiveData<Resource<ResultType>> result = new MediatorLiveData<>();

    private LiveData<ResultType> dbSource;

    protected static String logTag = "network";

    @MainThread
    protected NetworkBoundResource() {
        reload();
    }

    @MainThread
    public void reload() {
        result.setValue(Resource.loading(null));

        result.removeSource(dbSource);

        dbSource = loadFromDb();

        result.addSource(dbSource, data -> {
            result.removeSource(dbSource);

            if (shouldFetch(data)) {
                fetchFromNetwork(dbSource, data);
            } else {
                fetchFromDb(dbSource);
            }
        });
    }

    private void fetchFromNetwork(final LiveData<ResultType> dbSource, ResultType data) {
        LiveData<Resource<RequestType>> apiResponse = createCall(data);
        // 重新附加 dbSource 作为新的来源,
        // 它将会迅速发送最新的值。
        result.addSource(dbSource,
                newData -> result.setValue(Resource.loading(newData)));

        result.addSource(apiResponse, response -> {
            result.removeSource(apiResponse);
            result.removeSource(dbSource);

            if (response == null || response.status == Status.SUCCESS
                    || response.status == Status.EMPTY) {
                saveResultAndReInit(response == null ? Resource.empty() : response);
            } else {
                Assert.assertTrue(response.status == Status.ERROR);
                onFetchFailed();
                result.addSource(dbSource,
                        newData -> result.setValue(
                                Resource.error(response.message, newData)));
            }

        });
    }

    private void fetchFromDb(final LiveData<ResultType> dbSource) {
        result.addSource(dbSource,
                newData -> {
                    if (newData == null ||
                            (newData instanceof Collection && ((Collection)newData).size() == 0)) {
                        result.setValue(Resource.empty());
                    } else {
                        result.setValue(Resource.success(newData));
                    }
                });
    }

    @SuppressLint("StaticFieldLeak")
    @MainThread
    private void saveResultAndReInit(@NonNull Resource<RequestType> response) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                if (response.data != null) {
                    saveCallResult(response.data);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (response.data != null) {
                    // We need recreate the dbSource here, so subclass have a chance to change
                    // the load policy
                    result.removeSource(dbSource);
                    dbSource = loadFromDb();
                    fetchFromDb(dbSource);
                }
            }
        }.execute();
    }
}
