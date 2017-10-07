package com.github.p1va.earthviewwallpaper.util;

import android.app.ActivityManager;
import android.content.Context;

import static android.content.Context.ACTIVITY_SERVICE;
import static android.content.pm.ApplicationInfo.FLAG_LARGE_HEAP;

/**
 * Created by Stefano Piva on 24/09/2017.
 */

public class CacheMemoryUtils {
    /**
     * Calculates the cache memory size to allocate
     *
     * @return an integer describing cache memory size
     */
    public static int calculateSize(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        boolean largeHeap = (context.getApplicationInfo().flags & FLAG_LARGE_HEAP) != 0;
        int memoryClass = am.getMemoryClass();

        //TODO: investigate

        if(largeHeap) {
            memoryClass = am.getLargeMemoryClass();
        }

        // Target ~50% of the available heap.
        return 1024 * 1024 * memoryClass / 2;
    }

}
