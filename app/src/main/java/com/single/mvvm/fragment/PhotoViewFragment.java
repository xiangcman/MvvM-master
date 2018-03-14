package com.single.mvvm.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.bumptech.glide.Glide;
import com.single.mvvm.R;
import com.single.mvvm.databinding.FragmentPhotoviewBinding;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by Administrator on 2016/3/30.
 */
public class PhotoViewFragment extends DialogFragment {
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
        Glide.with(view.getContext()).load(getArguments().getString("photoUrl")).into(binding.photoview);
        setupPhotoEvent();
        super.onViewCreated(view, savedInstanceState);
    }

    private void setupPhotoEvent() {
        binding.photoview.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
            @Override
            public void onViewTap(View view, float x, float y) {
                dismiss();
            }
        });

//        photoView.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                NetWordUtil netWordUtil = new NetWordUtil(getActivity());
//                if (netWordUtil.isNetConnected()) {
//                    // 获取存储权限
//                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
//                    } else {
//                        saveBitmap(getArguments().getString("photoUrl"), getArguments().getString("desc"));
//                    }
//                }
//                return false;
//            }
//        });
    }
//
//    private void saveBitmap(String photoUrl, final String desc) {
//        Observable observable = Observable.just(photoUrl).map(new Func1<String, Bitmap>() {
//            @Override
//            public Bitmap call(String s) {
//                try {
//                    return Picasso.with(getActivity()).load(s).get();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                return null;
//            }
//        }).map(new Func1<Bitmap, Uri>() {
//            @Override
//            public Uri call(Bitmap bitmap) {
//                if (bitmap != null) {
//                    File appDir = new File(Environment.getExternalStorageDirectory(), "Girl");
//                    if (!appDir.exists()) {
//                        appDir.mkdir();
//                    }
//                    File file = new File(appDir, desc + ".jpg");
//                    try {
//                        FileOutputStream fos = new FileOutputStream(file);
//                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
//                        fos.flush();
//                        fos.close();
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//
//                    Uri uri = Uri.fromFile(file);
//                    // 通知图库更新
//                    Intent scannerIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri);
//                    getActivity().sendBroadcast(scannerIntent);
//
//                    return uri;
//                }
//                return null;
//            }
//        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
//
//        observable.subscribe(new Action1<Uri>() {
//            @Override
//            public void call(Uri uri) {
//                ToastUtil.showToast(getActivity(), "保存至" + uri.toString());
//            }
//        });
//
//    }

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

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == 1) {
//            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                saveBitmap(getArguments().getString("photoUrl"), getArguments().getString("desc"));
//            } else {
//                ToastUtil.showToast(getActivity(), "没有相关权限");
//            }
//
//        }
//    }

}
