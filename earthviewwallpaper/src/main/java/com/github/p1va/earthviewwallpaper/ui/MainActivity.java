package com.github.p1va.earthviewwallpaper.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.couchbase.lite.QueryEnumerator;
import com.github.p1va.earthviewwallpaper.R;
import com.github.p1va.earthviewwallpaper.adapters.EarthViewImagesAdapter;
import com.github.p1va.earthviewwallpaper.data.model.EarthViewImage;
import com.github.p1va.earthviewwallpaper.data.persistance.EarthViewImagesStore;
import com.github.p1va.earthviewwallpaper.util.MeasuramentUtils;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    /**
     * The recycler view adapter
     */
    private EarthViewImagesAdapter mImagesAdapter;

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

        // Find recycler view
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        // Create an instance of the images adapter
        mImagesAdapter = new EarthViewImagesAdapter(this,
                EarthViewImagesStore
                        .getInstance()
                        .getAll());

        // Create grid layout manager
        RecyclerView.LayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 2);

        // Set the layout manager to the recycler view
        recyclerView.setLayoutManager(gridLayoutManager);

        // Set the images adapter to the recycler view
        recyclerView.setAdapter(mImagesAdapter);

        // Set spacing between images
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, MeasuramentUtils.convertDpToPx(4, this), true));

        // Run the query to retrieve the images
        //new QueryDatabaseTask().execute(0);
    }

    private ArrayList<EarthViewImage> getAllImages() {

        QueryEnumerator rows = EarthViewImagesStore
                .getInstance()
                .getAll();

        return EarthViewImage.fromQuery(rows);
    }

    /**
     * Task that execute a database query and notify the images adapter
     */
    private class QueryDatabaseTask extends AsyncTask<Integer, Integer, ArrayList<EarthViewImage>> {

        /**
         * Executes the query off of the UI thread
         *
         * @param integers An array of integers
         * @return The list of results
         */
        @Override
        protected ArrayList<EarthViewImage> doInBackground(Integer... integers) {
            return getAllImages();
        }

        /**
         * Called when task completed, notify the images adapter with the changes
         *
         * @param results The list of results
         */
        protected void onPostExecute(ArrayList<EarthViewImage> results) {

            // Add results
            //mImagesAdapter.images.clear();
            //mImagesAdapter.images.addAll(results);

            // Notify changes
            //mImagesAdapter.notifyDataSetChanged();
        }
    }
}
