package com.github.p1va.earthviewwallpaper.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;

/**
 * Created by Stefano Piva on 03/10/2017.
 */

public class DrawableUtils {
    public static Drawable tint(Context context, Drawable drawable, int id ){
        Drawable drawableCompat = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawableCompat, ContextCompat.getColor(context, id));
        return drawableCompat;
    }
}
