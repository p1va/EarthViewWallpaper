package com.github.p1va.earthviewwallpaper;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.github.p1va.earthviewwallpaper.data.persistance.EarthViewImagesStore;
import com.github.p1va.earthviewwallpaper.util.CacheMemoryUtils;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.robotpajamas.stetho.couchbase.CouchbaseInspectorModulesProvider;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;

import timber.log.Timber;

/**
 * Created by Stefano Piva on 24/09/2017.
 */

public class EarthviewWallpaperApplication extends Application {

    /**
     * Called on application creation
     */
    @Override
    public void onCreate() {

        super.onCreate();

        // Setup Leak Canary
        if(BuildConfig.DEBUG) {
            if(LeakCanary.isInAnalyzerProcess(this)) return;
            LeakCanary.install(this);
        }

        // Setup Timber
        if(BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        // Setup Picasso
        Picasso picasso = new Picasso.Builder(this)
                .memoryCache(new LruCache(CacheMemoryUtils.calculateSize(this)))
                .downloader(new OkHttp3Downloader(this))
                .build();

        if(BuildConfig.DEBUG) {
            picasso.setLoggingEnabled(true);
            picasso.setIndicatorsEnabled(true);
        }

        Picasso.setSingletonInstance(picasso);

        // Setup Stetho
        if(BuildConfig.DEBUG) {
            Stetho.initialize(
                    Stetho.newInitializerBuilder(this)
                            .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                            .enableWebKitInspector(new CouchbaseInspectorModulesProvider.Builder(this)
                                    .showMetadata(true)
                                    .build())
                            .build());
        }

        // Setup data layer
        EarthViewImagesStore.initialize(this);
    }
}
