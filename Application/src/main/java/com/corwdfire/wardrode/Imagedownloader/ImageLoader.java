package com.corwdfire.wardrode.Imagedownloader;

import android.content.Context;

/**
 * Created by rahul on 11/23/16.
 */

public class ImageLoader {
    private static GalleryImageLoader mImageLoader;

    public static synchronized GalleryImageLoader getInstance(Context context) {
        if (mImageLoader == null) {
            mImageLoader = new GalleryImageLoader(context);
        }
        return mImageLoader;
    }

}
