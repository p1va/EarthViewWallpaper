package com.github.p1va.earthviewwallpaper.data.persistance;

import android.content.Context;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Emitter;
import com.couchbase.lite.Manager;
import com.couchbase.lite.Mapper;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.View;
import com.couchbase.lite.android.AndroidContext;
import com.couchbase.lite.util.ZipUtils;

import java.io.IOException;
import java.util.Map;
import java.util.Random;

import timber.log.Timber;

/**
 * Created by Stefano Piva on 24/09/2017.
 */

/**
 * The EarthViewImagesStore class
 */
public class EarthViewImagesStore {

    /**
     * The Couchbase view by random
     */
    private static final String COUCHBASE_VIEW_BY_RANDOM = "list/listByRandom";

    /**
     * The database name
     */
    private static final String DATABASE_NAME = "images";

    /**
     * The zip file containing the initialized database
     */
    private static final String DATABASE_ZIP = "images.zip";

    /**
     * The instance
     */
    private static EarthViewImagesStore mInstance = null;

    /**
     * The manager
     */
    private static Manager mManager = null;

    /**
     * The database
     */
    private static Database mDatabase = null;

    /**
     * Initializes the singleton
     *
     * @param applicationContext the application context
     */
    public static void initialize(Context applicationContext) {
        try {
            initializeCouchbase(applicationContext);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the single instance
     *
     */
    public static EarthViewImagesStore getInstance() {
        if(mInstance == null) {
            mInstance = new EarthViewImagesStore();
        }
        return mInstance;
    }

    /**
     * Initializes Couchbase database instance and loads initial data in it.
     *
     */
    private static void initializeCouchbase(Context applicationContext) throws Exception {

        try {
            mManager = new Manager(new AndroidContext(applicationContext), Manager.DEFAULT_OPTIONS);
        } catch (IOException e) {
            Timber.e("Unable to create new instance of Couchbase manager", e);
        }

        if(mManager == null) {
            throw new Exception("Unable to initialize Couchbase");
        }

        Timber.d("Trying to open " + DATABASE_NAME + " mDatabase");

        // Try to get existing mDatabase
        try {
            mDatabase = mManager.getExistingDatabase(DATABASE_NAME);
        } catch (CouchbaseLiteException e) {
            Timber.d("Database " + DATABASE_NAME + " does not exists yet");
        }

        if(mDatabase != null) {
            return;
        }

        Timber.d("Creating new " + DATABASE_NAME + " mDatabase from zip file " + DATABASE_ZIP);

        try {

            ZipUtils.unzip(
                    applicationContext.getAssets().open(DATABASE_ZIP),
                    mManager.getContext().getFilesDir());

        } catch (IOException e) {
            Timber.e("Unable to creating new " + DATABASE_NAME + " mDatabase from zip file " + DATABASE_ZIP, e);
        }

        try {

            mDatabase = mManager.getExistingDatabase(DATABASE_NAME);

        } catch (CouchbaseLiteException e) {
            Timber.e("Unable to open database instance of Couchbase manager", e);
        }
    }

    /**
     * Gets all the images in random order
     *
     * @return the query enumerator containing results
     */
    public QueryEnumerator getAllInRandomOrder(boolean forceUpdate) {

        Timber.d("Getting all the images in random order");

        // If force update is specified
        if(forceUpdate) {

            // Retrieve the existing view
            View existingView = mDatabase.getExistingView(COUCHBASE_VIEW_BY_RANDOM);

            if(existingView != null) {
                // Delete it
                existingView.delete();
            }
        }

        // Get existing view or create it
        View view = mDatabase.getView(COUCHBASE_VIEW_BY_RANDOM);

        // If no map yet
        if(view.getMap() == null) {

            Timber.d("Creating view " + COUCHBASE_VIEW_BY_RANDOM + " as it does not exist yet");

            // Set the map function
            view.setMap(new Mapper() {
                @Override
                public void map(Map<String, Object> document, Emitter emitter) {

                    // Generate random integer
                    int randomInt = new Random().nextInt(100) + 1;

                    // Emit the document
                    emitter.emit(randomInt, document);
                }
            }, "1.0");
        }

        // Create query
        Query query = view.createQuery();

        try {
            return query.run();
        } catch (CouchbaseLiteException e) {
            Timber.e("An error occurred retrieving all the document in random order", e);
        }

        return null;
    }
}
