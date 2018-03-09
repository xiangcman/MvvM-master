package com.single.mvvm.utils;

import android.databinding.BindingAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

/**
 * Created by xiangcheng on 18/3/8.
 */

public class ImageUtils {
    @BindingAdapter({"imageUrl"})
    public static void loadImage(ImageView view, String url) {
        Glide.with(view.getContext()).load(url).into(view);
    }
}
