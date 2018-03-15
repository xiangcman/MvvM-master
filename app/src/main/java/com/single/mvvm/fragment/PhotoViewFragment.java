package com.single.mvvm.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.single.mvvm.R;
import com.single.mvvm.databinding.FragmentPhotoviewBinding;
import com.single.mvvm.download.DownloadUtils;
import com.single.mvvm.download.JsDownloadListener;

import rx.Subscriber;

/**
 * Created by Administrator on 2016/3/30.
 */
public class PhotoViewFragment extends DialogFragment implements JsDownloadListener {
    private static final String TAG = PhotoViewFragment.class.getSimpleName();
    FragmentPhotoviewBinding binding;

    public PhotoViewFragment() {
        // note that empty method is requried for dialogfragment
    }

    public static PhotoViewFragment newInstance(String photoUrl, String desc) {
        PhotoViewFragment fragment = new PhotoViewFragment();
        Bundle args = new Bundle();
        args.putString("photoUrl", photoUrl);
        args.putString("desc", desc);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_photoview, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        //Picasso用来显示图片的框架
        url = getArguments().getString("photoUrl");
        Glide.with(view.getContext()).load(url).into(binding.photoview);
        setupPhotoEvent();
        super.onViewCreated(view, savedInstanceState);
    }

    private DownloadUtils downloadUtils;
    private String baseUrl;
    private String url;

    private void setupPhotoEvent() {
        baseUrl = Uri.parse(url).getScheme() + "//" + Uri.parse(url).getHost();
        downloadUtils = new DownloadUtils(baseUrl, this);
        binding.photoview.setOnViewTapListener((view, x, y) -> dismiss());
        binding.download.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            } else {
                downloadImage();
            }

        });
    }

    private void downloadImage() {
        String photoPath = url.substring(baseUrl.length());
        downloadUtils.download(getArguments().getString("photoUrl"), photoPath, new Subscriber() {
            @Override
            public void onCompleted() {
                Toast.makeText(getActivity(), "下载完成", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Object o) {

            }
        });
    }

    @Override
    public void onResume() {
        // Get existing layout params for the window
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        // Assign window properties to fill the parent
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((WindowManager.LayoutParams) params);
        // Call super onResume after sizing
        super.onResume();
    }

    @Override
    public void onStartDownload() {

    }

    @Override
    public void onProgress(int progress) {
        Log.d(TAG, "progress:" + progress);
        if (progress == 100) {

        }
    }

    @Override
    public void onFinishDownload() {

    }

    @Override
    public void onFail(String errorInfo) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                downloadImage();
            } else {
                Toast.makeText(getActivity(), "没有相关权限", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
