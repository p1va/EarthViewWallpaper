package com.github.p1va.earthviewwallpaper.util;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

/**
 * Created by Stefano Piva on 29/09/2017.
 */

/**
 * Layout utilities class
 */
public class LayoutUtils {

    /**
     * Sets the margin to the given view
     * @param view the view
     * @param leftMargin the left margin
     * @param topMargin the top margin
     * @param rightMargin the right margin
     * @param bottomMargin the bottom margin
     */
    public static void setMargins(View view, int leftMargin, int topMargin, int rightMargin, int bottomMargin) {
        if(view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);
            view.requestLayout();
        }
    }

    /**
     * Gets the status bar height
     * @param context the context
     * @return the status bar height
     */
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if(resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * Gets the navigation bar height
     * @param context the context
     * @return the navigation bar height
     */
    public static int getNavBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if(resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    public static void setSystemUiToFullscreen(Window window){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            window.getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
    }
}
