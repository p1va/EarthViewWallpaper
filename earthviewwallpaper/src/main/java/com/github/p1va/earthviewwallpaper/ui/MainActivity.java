package com.github.p1va.earthviewwallpaper.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Emitter;
import com.couchbase.lite.Manager;
import com.couchbase.lite.Mapper;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;
import com.couchbase.lite.Reducer;
import com.couchbase.lite.android.AndroidContext;
import com.couchbase.lite.util.ZipUtils;
import com.github.p1va.earthviewwallpaper.R;
import com.github.p1va.earthviewwallpaper.adapters.ImagesAdapter;
import com.github.p1va.earthviewwallpaper.data.model.GoogleEarthViewImage;
import com.github.p1va.earthviewwallpaper.util.MeasuramentUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

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

    /**
     * The mDatabase instance
     */
    private Database mDatabase = null;

    /**
     * The recycler view adapter
     */
    private ImagesAdapter mImagesAdapter;

    /**
     * Initializes Couchbase database instance and loads initial data in it.
     *
     * @throws Exception
     */
    private void initializeCouchbase() throws Exception {

        // Create a manager
        Manager manager = null;

        // Try instanciating the manager
        try {
            manager = new Manager(new AndroidContext(getApplicationContext()), Manager.DEFAULT_OPTIONS);
        } catch (IOException e) {
            Timber.e("Unable to instanciate Couchbase manager", e);
        }

        if(manager == null) {
            throw new Exception("Unable to initialize Couchbase");
        }

        Timber.d("Trying to open " + DATABASE_NAME + " mDatabase");

        // Try to get existing mDatabase
        try {
            mDatabase = manager.getExistingDatabase(DATABASE_NAME);
        } catch (CouchbaseLiteException e) {
            Timber.d("Database " + DATABASE_NAME + " does not exists yet");
        }

        if(mDatabase != null) {
            return;
        }

        Timber.d("Creating new " + DATABASE_NAME + " mDatabase from zip file " + DATABASE_ZIP);

        try {
            ZipUtils.unzip(getAssets().open(DATABASE_ZIP), manager.getContext().getFilesDir());
        } catch (IOException e) {
            Timber.e("Unable to creating new " + DATABASE_NAME + " mDatabase from zip file " + DATABASE_ZIP, e);
        }

        try {
            mDatabase = manager.getExistingDatabase("images");
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Called when the activity is created
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Call super class
        super.onCreate(savedInstanceState);

        // Set the content view
        setContentView(R.layout.activity_main);

        try {

            // Initialize Couchbase
            initializeCouchbase();
        } catch (Exception e) {

            View layout = findViewById(R.id.main_layout);
            Snackbar.make(layout, "Unable to create images mDatabase", Snackbar.LENGTH_LONG).show();
        }

        // Find recycler view
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        // Create an instance of the images adapter
        mImagesAdapter = new ImagesAdapter(this);

        // Create grid layout manager
        RecyclerView.LayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 2);

        // Set the layout manager to the recycler view
        recyclerView.setLayoutManager(gridLayoutManager);

        // Set the images adapter to the recycler view
        recyclerView.setAdapter(mImagesAdapter);

        // Set spacing between images
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, MeasuramentUtils.convertDpToPx(4, this), true));

        // Run the query to retrieve the images
        new QueryDatabaseTask().execute(0);
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

            return mapDatabaseToImageCount(queryResults);
        }

        return null;
    }

    private ArrayList<GoogleEarthViewImage> getImagesByCountry(String country) {

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
            queryResults = query.run();
        } catch (CouchbaseLiteException e) {
            //TODO: Handle error
            e.printStackTrace();
        }

        if(query != null) {
            int resultsCount = queryResults.getCount();

            Timber.d(resultsCount + " images found having " + country + " as a country");

            return mapDatabaseToImage(queryResults);
        }

        return null;
    }

    private ArrayList<GoogleEarthViewImage> mapDatabaseToImage(QueryEnumerator databaseResults) {
        ArrayList<GoogleEarthViewImage> results = new ArrayList<>();

        // Iterate on each one of the results
        for (QueryRow row : databaseResults) {

            Document document = row.getDocument();

            // Create an image
            GoogleEarthViewImage image = new GoogleEarthViewImage();
            image.api = (String) document.getProperty("api");
            image.attribution = (String) document.getProperty("attribution");
            image.country = (String) document.getProperty("country");
            image.downloadUrl = (String) document.getProperty("downloadUrl");
            image.earthLink = (String) document.getProperty("earthLink");
            image.earthTitle = (String) document.getProperty("earthTitle");
            image.id = (String) document.getProperty("id");
            image.lat = (String) document.getProperty("lat");
            image.lng = (String) document.getProperty("lng");
            image.mapsLink = (String) document.getProperty("mapsLink");
            image.mapsTitle = (String) document.getProperty("mapsTitle");
            image.nextApi = (String) document.getProperty("nextApi");
            image.region = (String) document.getProperty("region");
            image.slug = (String) document.getProperty("slug");
            image.title = (String) document.getProperty("title");
            image.url = (String) document.getProperty("url");
            image.photoUrl = (String) document.getProperty("photoUrl");
            image.thumbUrl = (String) document.getProperty("thumbUrl");

            // Add it to the adapter collection
            results.add(image);
        }

        return results;
    }

    private Map<String, Integer> mapDatabaseToImageCount(QueryEnumerator databaseResults) {

        Map<String, Integer> results = new HashMap<>();

        // Iterate on each one of the results
        for (QueryRow row : databaseResults) {

            results.put((String) row.getKey(), (Integer) row.getValue());

            // Create
            Timber.d(row.toString());
        }

        return results;
    }

    private ArrayList<GoogleEarthViewImage> queryImages() {

        ArrayList<GoogleEarthViewImage> results = new ArrayList<>();

        try {

            // Create a query that returns all of the documents
            Query query = mDatabase.createAllDocumentsQuery();

            // Run the query
            QueryEnumerator rows = query.run();

            // Map results
            results = mapDatabaseToImage(rows);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }

        return results;
    }

    /**
     * Task that execute a database query and notify the images adapter
     */
    private class QueryDatabaseTask extends AsyncTask<Integer, Integer, ArrayList<GoogleEarthViewImage>> {

        /**
         * Executes the query off of the UI thread
         *
         * @param integers An array of integers
         * @return The list of results
         */
        @Override
        protected ArrayList<GoogleEarthViewImage> doInBackground(Integer... integers) {

            getImagesCountByCountry();

            return getImagesByCountry("Italy");
        }

        /**
         * Called when task completed, notify the images adapter with the changes
         *
         * @param results The list of results
         */
        protected void onPostExecute(ArrayList<GoogleEarthViewImage> results) {

            // Add results
            mImagesAdapter.images.clear();
            mImagesAdapter.images.addAll(results);

            // Notify changes
            mImagesAdapter.notifyDataSetChanged();
        }
    }
}
