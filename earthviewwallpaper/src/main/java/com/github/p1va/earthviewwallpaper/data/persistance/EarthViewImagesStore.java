package com.github.p1va.earthviewwallpaper.data.persistance;

import android.content.Context;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Emitter;
import com.couchbase.lite.Manager;
import com.couchbase.lite.Mapper;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.Reducer;
import com.couchbase.lite.android.AndroidContext;
import com.couchbase.lite.util.ZipUtils;
import com.github.p1va.earthviewwallpaper.data.model.EarthViewImage;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

/**
 * Created by Stefano Piva on 24/09/2017.
 */

public class EarthViewImagesStore {

    /**
     * The couchbase view by country name
     */
    private static final String COUCHBASE_VIEW_BY_COUNTRY = "list/listByCountry";

    private static final String COUCHBASE_VIEW_BY_COUNTRY_COUNT = "list/listByCountryCount";

    /**
     * The mDatabase name
     */
    private static final String DATABASE_NAME = "images";

    /**
     * The zip file containing the initialized mDatabase
     */
    private static final String DATABASE_ZIP = "images.zip";

    private static EarthViewImagesStore mInstance = null;

    private static Manager mManager = null;

    private static Database mDatabase = null;

    private EarthViewImagesStore() {

    }

    public static void initialize(Context applicationContext) {
        try {
            initializeCouchbase(applicationContext);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static EarthViewImagesStore getInstance() {
        if(mInstance == null) {
            mInstance = new EarthViewImagesStore();
        }
        return mInstance;
    }

    private Map<String, Integer> getImagesCountByCountry() {

        Timber.d("Getting images count by country");

        com.couchbase.lite.View imagesCountByCountryView = mDatabase.getView(COUCHBASE_VIEW_BY_COUNTRY_COUNT);
        if(imagesCountByCountryView.getMap() == null) {

            Timber.d("Creating view " + COUCHBASE_VIEW_BY_COUNTRY_COUNT + " as it does not exist yet");

            // Create list by country view
            imagesCountByCountryView.setMapReduce(new Mapper() {
                @Override
                public void map(Map<String, Object> document, Emitter emitter) {
                    emitter.emit(document.get("country"), document);
                }
            }, new Reducer() {
                @Override
                public Object reduce(List<Object> keys, List<Object> values, boolean rereduce) {
                    return values.size();
                }
            }, "1.0");
        } else {
            Timber.d("View " + COUCHBASE_VIEW_BY_COUNTRY_COUNT + " already exists");
        }

        // Create query
        Query query = imagesCountByCountryView.createQuery();

        // Set query to group items by key
        query.setGroupLevel(1);

        QueryEnumerator queryResults = null;

        try {
            queryResults = query.run();
        } catch (CouchbaseLiteException e) {
            //TODO: Handle error
            e.printStackTrace();
        }

        if(query != null) {
            int resultsCount = queryResults.getCount();

            Timber.d(resultsCount + " images count by country found");

            return EarthViewImage.mapDatabaseToImageCount(queryResults);
        }

        return null;
    }

    /**
     * Initializes Couchbase database instance and loads initial data in it.
     *
     * @throws Exception
     */
    private static void initializeCouchbase(Context applicationContext) throws Exception {

        // Try instanciating the manager
        try {
            mManager = new Manager(new AndroidContext(applicationContext), Manager.DEFAULT_OPTIONS);
        } catch (IOException e) {
            Timber.e("Unable to instanciate Couchbase manager", e);
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
            ZipUtils.unzip(applicationContext.getAssets().open(DATABASE_ZIP), mManager.getContext().getFilesDir());
        } catch (IOException e) {
            Timber.e("Unable to creating new " + DATABASE_NAME + " mDatabase from zip file " + DATABASE_ZIP, e);
        }

        try {
            mDatabase = mManager.getExistingDatabase("images");
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }

    public QueryEnumerator getAll() {
        try {

            // Create a query that returns all of the documents
            Query query = mDatabase.createAllDocumentsQuery();

            // Run the query
            return query.run();
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }

        //TODO: Handle
        return null;
    }

    public QueryEnumerator getByCountry(String country) {

        Timber.d("Getting images of " + country);

        com.couchbase.lite.View imagesByCountryView = mDatabase.getView(COUCHBASE_VIEW_BY_COUNTRY);
        if(imagesByCountryView.getMap() == null) {

            Timber.d("Creating view " + COUCHBASE_VIEW_BY_COUNTRY + " as it does not exist yet");

            // Create list by country view
            imagesByCountryView.setMapReduce(new Mapper() {
                @Override
                public void map(Map<String, Object> document, Emitter emitter) {
                    emitter.emit(document.get("country"), document);
                }
            }, null, "1.0");
        } else {
            Timber.d("View " + COUCHBASE_VIEW_BY_COUNTRY + " already exists");
        }

        // Create query
        Query query = imagesByCountryView.createQuery();
        query.setStartKey(country);
        query.setEndKey(country);
        query.setInclusiveStart(true);
        query.setInclusiveEnd(true);

        QueryEnumerator queryResults = null;

        try {
            return query.run();
        } catch (CouchbaseLiteException e) {
            //TODO: Handle error
            e.printStackTrace();
        }

        return null;
    }
}
