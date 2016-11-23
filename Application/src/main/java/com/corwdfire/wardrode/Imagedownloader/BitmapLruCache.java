package com.corwdfire.wardrode.Imagedownloader;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

public class BitmapLruCache {

    private final LruCache<String, Bitmap> mMemoryCache;

    public BitmapLruCache() {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 4;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (key == null || bitmap == null)
            return;
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    public void clear() {
        mMemoryCache.evictAll();
    }
}