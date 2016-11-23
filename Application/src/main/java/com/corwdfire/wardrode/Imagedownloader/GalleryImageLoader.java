
package com.corwdfire.wardrode.Imagedownloader;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.widget.ImageView;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GalleryImageLoader {

    private Context mContext;
    private BitmapLruCache mMemoryCache = new BitmapLruCache();
    private Map<ImageView, String> mImageViews = Collections
            .synchronizedMap(new WeakHashMap<ImageView, String>());
    private ExecutorService mExecutorService;
    private ContentResolver mContentResolver;
    private int mDstWidth;
    private int mDstHeight;
    ScalingUtilities.ScalingLogic mScalingLogic;

    protected GalleryImageLoader(Context context) {
        mContext = context;
        mExecutorService = Executors.newFixedThreadPool(10);
        mContentResolver = context.getContentResolver();
    }


    public void displayImage(String imageKey, ImageView imageView, int width, int height, ScalingUtilities.ScalingLogic scalingLogic) {
        mImageViews.put(imageView, imageKey);
        mDstWidth = width;
        mDstHeight = height;
        mScalingLogic = scalingLogic;
        Bitmap bitmap = mMemoryCache.getBitmapFromMemCache(imageKey+scalingLogic);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            imageView.setImageDrawable(null);
            queuePhoto(imageKey, imageView);
        }
    }

    private void queuePhoto(String imageId, ImageView imageView) {
        PhotoToLoad p = new PhotoToLoad(imageId, imageView);
        mExecutorService.submit(new PhotosLoader(p));
    }

    // Task for the queue
    private class PhotoToLoad {
        private String imageKey;
        private ImageView imageView;

        PhotoToLoad(String imageId, ImageView imageView) {
            this.imageKey = imageId;
            this.imageView = imageView;
        }
    }

    private class PhotosLoader implements Runnable {
        private PhotoToLoad photoToLoad;

        private PhotosLoader(PhotoToLoad photoToLoad) {
            this.photoToLoad = photoToLoad;
        }

        @Override
        public void run() {
            if (imageViewReused(photoToLoad)) {
                return;
            }
            Bitmap bmp = getBitmap(photoToLoad.imageKey);
            mMemoryCache.addBitmapToMemoryCache(photoToLoad.imageKey+mScalingLogic, bmp);
            if (imageViewReused(photoToLoad)) {
                return;
            }
            BitmapDisplay bd = new BitmapDisplay(bmp, photoToLoad);
            Activity a = (Activity) mContext;
            a.runOnUiThread(bd);
        }
    }

    private Bitmap getBitmap(String imageId) {
        // from database using content uri
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inSampleSize = ScalingUtilities.calculateSampleSize(options.outWidth, options.outHeight, mDstWidth,
                mDstHeight, mScalingLogic);

        Bitmap unscaledBitmap = MediaStore.Images.Thumbnails.getThumbnail(mContentResolver, Long.parseLong(imageId), MediaStore.Images.Thumbnails.MINI_KIND, options);

        // Part 2: Scale image
        Bitmap scaledBitmap = ScalingUtilities.createScaledBitmap(unscaledBitmap, mDstWidth,
                mDstHeight, mScalingLogic);
        unscaledBitmap.recycle();

        return scaledBitmap;
    }

    private boolean imageViewReused(PhotoToLoad photoToLoad) {
        String tag = mImageViews.get(photoToLoad.imageView);
        return tag == null || !tag.equals(photoToLoad.imageKey);
    }

    // Used to display bitmap in the UI thread
    private class BitmapDisplay implements Runnable {
        private Bitmap bitmap;
        private PhotoToLoad photoToLoad;

        BitmapDisplay(Bitmap b, PhotoToLoad p) {
            bitmap = b;
            photoToLoad = p;
        }

        public void run() {
            if (imageViewReused(photoToLoad)) {
                return;
            }
            if (bitmap != null) {
                photoToLoad.imageView.setImageBitmap(bitmap);
            } else {
                photoToLoad.imageView.setImageDrawable(null);
            }
        }
    }
}