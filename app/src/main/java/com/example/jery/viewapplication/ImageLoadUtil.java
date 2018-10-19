package com.example.jery.viewapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.RequestOptions;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.concurrent.ExecutionException;

public class ImageLoadUtil {

    @SuppressLint("CheckResult")
    public static void loadVideoScreenshot(final Context context, ImageView imageView, final Uri uri, final File file, long frameTimeMicros) {
        final RequestOptions requestOptions = RequestOptions.frameOf(frameTimeMicros);
        requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
        requestOptions.transform(new BitmapTransformation() {

            @Override
            protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
                if (toTransform == null) return null;
                Bitmap result = Bitmap.createBitmap(toTransform, 0, 0, toTransform.getWidth(), toTransform.getHeight());
                if (result == null) {
                    result = Bitmap.createBitmap(toTransform.getWidth(), toTransform.getHeight(), Bitmap.Config.RGB_565);
                }
                saveBitmapFile(result, file);
                return result;
            }


            @Override
            public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {

            }
        });
        Glide.with(context).load(uri).apply(requestOptions).into(imageView);
    }

    public static void saveBitmapFile(Bitmap bitmap, File file) {
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void simpleLoad(final Context context, final Uri uri, final ImageView imageView, long frameTimeMicros) {
        RequestOptions requestOptions = RequestOptions.frameOf(frameTimeMicros);
        requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
        Glide.with(context).load(uri).apply(requestOptions).into(imageView);
    }

    public static File loadVideoImage(final Context context, final Uri uri, final String filePath, long frameTimeMicros) {
        RequestOptions requestOptions = RequestOptions.frameOf(frameTimeMicros);
        requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
        File file = null;
        FutureTarget<Bitmap> bitmap = Glide.with(context).asBitmap().load(uri).apply(requestOptions).submit();
        try {

            Bitmap result = bitmap.get();
            if (result == null) {
                return file;
            }
            if (result.getWidth() == 0 || result.getHeight() == 0) {
                return file;
            }
            float rato = 1.0f * result.getHeight() / result.getWidth();
            float realHeight;
            float realWidth;
            if (rato > 1.0f * 16 / 9) {
                realWidth = result.getWidth();
                realHeight = 1.0f * 16 / 9 * result.getWidth();
            } else {
                realWidth = 1.0f * result.getHeight() * 9 / 16;
                realHeight = result.getHeight();
            }
            int x = (int) (result.getWidth() - realWidth) / 2;
            int y = (int) (result.getHeight() - realHeight) / 2;

            Bitmap newBitmap = Bitmap.createBitmap(result, x, y, (int) realWidth, (int) realHeight);
            file = new File(filePath);
            saveBitmapFile(newBitmap, file);
            if (result != null) {
                result.recycle();
            }
            return file;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } finally {
            return file;
        }
    }



    public static File getCacheDir(Context context) {
        return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED) ? context.getExternalCacheDir() : context.getCacheDir();
    }

    public static File getFileParent(Context context) {
        File parent = new File(getCacheDir(context.getApplicationContext()) + "/images");
        if (!parent.exists()) {
            parent.mkdirs();
        }
        return parent;
    }

    public static File getFile(Context context, String name) {
        String fileName = name;
        return new File(getFileParent(context.getApplicationContext()), fileName);
    }
}
