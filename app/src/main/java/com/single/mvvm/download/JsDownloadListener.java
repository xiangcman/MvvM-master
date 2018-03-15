package com.single.mvvm.download;

/**
 * Created by xiangcheng on 18/3/15.
 */

public interface JsDownloadListener {
    void onStartDownload();

    void onProgress(int progress);

    void onFinishDownload();

    void onFail(String errorInfo);
}
