package com.geek.libglide47.glide48;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.bumptech.glide.signature.ObjectKey;
import com.geek.libglide47.R;
import com.geek.libglide47.base.progress.GlideApp;
import com.geek.libutils.app.MyLogUtil;
import com.lxj.xpermission.PermissionConstants;
import com.lxj.xpermission.XPermission;

import java.io.File;
import java.util.UUID;

//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.support.annotation.RequiresApi;

public class GlideAppUtils {

    public void setglide1(Context context, ImageView imageView, String url) {
        GlideApp.with(context)
                .load(url)
                .centerCrop()
                .dontAnimate()
                .placeholder(R.drawable.ic_defs_loading)
                .into(imageView);
    }

    public void setglide2(Context context, ImageView imageView, String url) {
        GlideApp.with(context)
                .load(url)
                .skipMemoryCache(true)
                .fitCenter()
                .placeholder(R.drawable.ic_defs_loading)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(imageView);
    }

    public void setglide3(Context context, ImageView imageView, String url) {
        RequestOptions options = new RequestOptions()
                .signature(new ObjectKey(UUID.randomUUID().toString()))  // ???????????????
                .skipMemoryCache(false)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .placeholder(R.drawable.ic_defs_loading)
                .error(R.drawable.ic_defs_loading)
                .fallback(R.drawable.ic_defs_loading); //url???????????????,???????????????;
//        if (FileUtils.isFileExists(CommonUtils.img_file_url + CommonUtils.img_file_name)) {
//            File file = new File(CommonUtils.img_file_url + CommonUtils.img_file_name);
//            Glide.with(context).load(file)
//                    .apply(options)
//                    .into(imageView);
//        } else {
//            Glide.with(context).load(SPUtils.getInstance().getString(CommonUtils.USER_NAME))
//                    .apply(options)
//                    .into(imageView);
//        }
    }

    public void setglide4(Context context, ImageView imageView, String url) {
        RequestOptions options = new RequestOptions()
                .skipMemoryCache(false)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .placeholder(R.drawable.ic_defs_loading)
                .error(R.drawable.ic_defs_loading)
                .override(Target.SIZE_ORIGINAL)
                .transform(new RoundedCorners(50))
                .fallback(R.drawable.ic_defs_loading); //url???????????????,???????????????;
        Glide.with(context).load(url).apply(options).into(imageView);

    }

    public void setglide5(Context context, ImageView imageView, String url) {
        RequestOptions options = new RequestOptions()
                .skipMemoryCache(false)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .placeholder(R.drawable.ic_defs_loading)
                .error(R.drawable.ic_defs_loading)
                .override(Target.SIZE_ORIGINAL)
                .transform(new RoundedCorners(50))
                .fallback(R.drawable.ic_defs_loading); //url???????????????,???????????????;
        Glide.with(context).load(url).apply(options).into(imageView);

    }

    public void setglide6(Context context, ImageView imageView, String url) {
        //1. ????????????, ??????ImageView???centerCrop???????????????Target.SIZE_ORIGINAL?????????Glide???????????????
        // ????????????????????????????????????Matrix?????????????????????????????????
        Glide.with(imageView).load(url).apply(new RequestOptions().placeholder(R.drawable.ic_defs_loading)
                .override(Target.SIZE_ORIGINAL))
                .into(imageView);
    }

    @RequiresApi(api = Build.VERSION_CODES.FROYO)
    public void setglide7(Context context, ImageView imageView, String url) {
        // ??????????????????
        File file = new File(context.getApplicationContext().getExternalCacheDir() + "/image.jpg");
        Glide.with(context).load(file).into(imageView);

        // ??????????????????
        int resource = R.drawable.ic_defs_loading;
        Glide.with(context).load(resource).into(imageView);

        // ??????????????????
        byte[] image = new byte[]{};
        Glide.with(context).load(image).into(imageView);

        // ??????Uri??????
        Uri imageUri = null;
        Glide.with(context).load(imageUri).into(imageView);

        // ???????????????
//        Glide.with(context)
//                .load(R.drawable.ic_defs_loading)
//                .apply(bitmapTransform(new CropCircleTransformation()))
//                .into(imageView);

        // ????????????
//        Glide.with(context)
//                .load(R.drawable.ic_def_loading)
//                .apply(bitmapTransform(new BlurTransformation(25, 4)))
//                .into(imageView);

    }

    public void setglide8(Context context, ImageView imageView, String url) {
        Glide.with(context).downloadOnly().load(url).addListener(new RequestListener<File>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<File> target, boolean isFirstResource) {
//                progressBar.setVisibility(View.GONE);
                String errorMsg = "????????????";
                if (e != null) {
                    errorMsg = errorMsg.concat(":\n").concat(e.getMessage());
                }
                if (errorMsg.length() > 200) {
                    errorMsg = errorMsg.substring(0, 199);
                }
//                ToastUtil.getInstance()._short(context.getApplicationContext(), errorMsg);
                return false;
            }

            @Override
            public boolean onResourceReady(File resource, Object model, Target<File> target, DataSource dataSource, boolean isFirstResource) {
                String imagePath = resource.getAbsolutePath();
//                boolean isLongImage = ImageUtil.isLongImage(imagePath);
//                Print.d(TAG, "isLongImage = " + isLongImage);
//                if (isLongImage) {
//                    view.setOrientation(ImageUtil.getOrientation(imagePath));
//                    view.setMinimumScaleType(SubsamplingScaleImageViewDragClose.SCALE_TYPE_START);
//                }
//                view.setImage(ImageSource.uri(Uri.fromFile(new File(resource.getAbsolutePath()))));
//                progressBar.setVisibility(View.GONE);
                return false;
            }
        }).into(new CommonTarget<File>() {
            @Override
            public void onLoadStarted(@Nullable Drawable placeholder) {
                super.onLoadStarted(placeholder);
//                progressBar.setVisibility(View.VISIBLE);
            }
        });

    }

    public void setglide9(Context context, ImageView imageView, String url) {
        Glide.with(context).downloadOnly().load(url).into(new CommonTarget<File>() {
            @Override
            public void onLoadStarted(@Nullable Drawable placeholder) {
                super.onLoadStarted(placeholder);
                MyLogUtil.e("--glide48-CommonTarget-", "????????????...");
            }

            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                super.onLoadFailed(errorDrawable);
                MyLogUtil.e("--glide48-CommonTarget-", "????????????");
            }

            @Override
            public void onResourceReady(@NonNull File resource, @Nullable Transition<? super File> transition) {
                super.onResourceReady(resource, transition);
                MyLogUtil.e("--glide48-CommonTarget-", "????????????");

            }
        });

    }

    // ??????????????? ?????????????????????

    public void setglide10(final Context context, ImageView imageView, Object url) {
        if (url instanceof String) {
            url = "https://s2.51cto.com/wyfs02/M01/89/BA/wKioL1ga-u7QnnVnAAAfrCiGnBQ946_middle.jpg";
        }
        if (url instanceof Integer) {
            url = R.drawable.gif_robot_walk;
        }
        final Glide48ImageLoaderUtils glide48ImageLoaderUtils = new Glide48ImageLoaderUtils(context);
        final LxImageLoader lxImageLoader = new LxImageLoader();
        glide48ImageLoaderUtils.loadImg(context, lxImageLoader, imageView, url);
        //check permission
        final Object finalUrl = url;
        XPermission.create(context, PermissionConstants.STORAGE)
                .callback(new XPermission.SimpleCallback() {
                    @Override
                    public void onGranted() {
                        //save bitmap to album.
                        glide48ImageLoaderUtils.saveBmpToAlbum(context, lxImageLoader, finalUrl);// ??????
                    }

                    @Override
                    public void onDenied() {
                        Toast.makeText(context, "????????????????????????????????????????????????", Toast.LENGTH_SHORT).show();
                    }
                }).request();
    }

    public void setglide11(final Context context, Object url) {
        if (url != null && url instanceof String && !TextUtils.isEmpty((CharSequence) url)) {
            final Glide48ImageLoaderUtils glide48ImageLoaderUtils = new Glide48ImageLoaderUtils(context);
            final LxImageLoader lxImageLoader = new LxImageLoader();
            //check permission
            final Object finalUrl = url;
            XPermission.create(context.getApplicationContext(), PermissionConstants.STORAGE)
                    .callback(new XPermission.SimpleCallback() {
                        @Override
                        public void onGranted() {
                            //save bitmap to album.
                            glide48ImageLoaderUtils.saveBmpToAlbum(context, lxImageLoader, finalUrl);// ??????
                        }

                        @Override
                        public void onDenied() {
                            Toast.makeText(context, "????????????????????????????????????????????????", Toast.LENGTH_SHORT).show();
                        }
                    }).request();
        } else {
            Toast.makeText(context, "???????????????", Toast.LENGTH_SHORT).show();
        }
    }


}
