package com.xpyct.apps.anilab.managers;

import android.content.Context;
import android.graphics.Bitmap;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

/**
 * anilab-android
 * Created by XpycT on 19.07.2015.
 */
public class CircleTransform extends BitmapTransformation {
    public CircleTransform(Context context) {
        super(context);
    }

    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap source, int outWidth, int outHeight) {
        return Utils.getCircularBitmapImage(source);
    }

    @Override
    public String getId() {
        return "Glide_Circle_Transformation";
    }
}
