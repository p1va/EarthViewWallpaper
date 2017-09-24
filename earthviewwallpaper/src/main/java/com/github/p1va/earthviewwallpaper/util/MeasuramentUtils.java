package com.github.p1va.earthviewwallpaper.util;

import android.content.Context;
import android.util.TypedValue;

/**
 * Created by Stefano Piva on 24/09/2017.
 */

public class MeasuramentUtils {
    public static int convertPxToDp(int px, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, px,
                context.getResources().getDisplayMetrics());
    }

    public static int convertDpToPx(float dp, Context context) {
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
    }
}
