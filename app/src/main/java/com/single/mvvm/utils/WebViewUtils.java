package com.single.mvvm.utils;

import android.databinding.BindingAdapter;
import android.webkit.WebView;

/**
 * Created by xiangcheng on 18/3/8.
 */

public class WebViewUtils {
    @BindingAdapter({"webViewUrl"})
    public static void loadImage(WebView view, String html) {
        view.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);
    }
}
