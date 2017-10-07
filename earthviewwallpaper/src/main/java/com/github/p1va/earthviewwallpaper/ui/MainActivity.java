package com.github.p1va.earthviewwallpaper.ui;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.couchbase.lite.QueryEnumerator;
import com.github.p1va.earthviewwallpaper.R;
import com.github.p1va.earthviewwallpaper.adapters.EarthViewImagesAdapter;
import com.github.p1va.earthviewwallpaper.data.persistance.EarthViewImagesStore;
import com.github.p1va.earthviewwallpaper.util.DrawableUtils;
import com.github.p1va.earthviewwallpaper.util.MeasuramentUtils;
import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;

public class MainActivity extends AppCompatActivity {

    /**
     * Grid column width in dp
     */
    private static final int GRID_COLUMN_WIDTH_DP = 180;

    /**
     * Grid column height in dp
     */
    private static final int GRID_COLUMN_HEIGHT_DP = 288;

    /**
     * The images adapter
     */
    EarthViewImagesAdapter mImagesAdapter;

    /**
     * The action bar menu
     */
    Menu mMenu;

    /**
     * Called when the activity is created
     *
     * @param savedInstanceState the saved instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Call super class
        super.onCreate(savedInstanceState);

        // Set the content view
        setContentView(R.layout.activity_main);

        // Set support toolbar
        setSupportActionBar((Toolbar) findViewById(R.id.main_toolbar));

        // Set icon
        getSupportActionBar().setIcon(R.mipmap.ic_launcher_no_background);

        // Find recycler view
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.main_recycler_view);

        // Create an instance of the images adapter
        mImagesAdapter = new EarthViewImagesAdapter(this, GRID_COLUMN_HEIGHT_DP);

        // Calculate number of columns
        int columns = MeasuramentUtils.calculateNoOfColumns(this, GRID_COLUMN_WIDTH_DP);

        // Create grid layout manager
        RecyclerView.LayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), columns);

        // Set the layout manager to the recycler view
        recyclerView.setLayoutManager(gridLayoutManager);

        // Set the images adapter to the recycler view
        recyclerView.setAdapter(mImagesAdapter);

        // Set the max row height in pixels
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(columns, MeasuramentUtils.convertDpToPx(4, this), true));

        // Execute async task to load items
        new LoadImagesTask().execute(false);
    }

    /**
     * Called when creating options menu
     *
     * @param menu the menu
     * @return a flag describing success
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Get the inflater
        MenuInflater inflater = getMenuInflater();

        // Inflate the menue
        inflater.inflate(R.menu.main_menu, menu);

        // Keep track of the menu in a field
        mMenu = menu;

        // Retrieve icon to tint
        Drawable drawable = menu.findItem(R.id.action_shuffle).getIcon();

        // Apply tinting
        Drawable shuffleIcon = DrawableUtils.tint(this, drawable, R.color.actionBarActionsTint);

        // Set tinted icon to menu
        menu.findItem(R.id.action_shuffle).setIcon(shuffleIcon);

        return true;
    }

    /**
     * Called when one of the menu options is selected
     *
     * @param item the selected menu item
     * @return a flag describing success
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            // User selected the About option
            case R.id.action_about:
                startAboutActivity();
                return true;

            // User selected the Shuffle option
            case R.id.action_shuffle:
                // Execute async task to load items
                new LoadImagesTask().execute(true);
                return true;

            // Any other case
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Starts the about app activity
     */
    private void startAboutActivity() {
        new LibsBuilder()
                .withActivityStyle(Libs.ActivityStyle.LIGHT)
                .withAboutIconShown(true)
                .withAboutVersionShown(true)
                .withAboutDescription(getResources().getString(R.string.app_description))
                .start(this);
    }

    /**
     * The task that loads images and sets them on the adapter
     */
    private class LoadImagesTask extends AsyncTask<Boolean, Void, QueryEnumerator> {

        /**
         * Called before task execution
         */
        @Override
        protected void onPreExecute() {
            if(mMenu != null) {
                final MenuItem item = mMenu.findItem(R.id.action_shuffle);
                item.setActionView(R.layout.action_shuffle_indeterminate_progress);
                item.expandActionView();
            }
        }

        /**
         * Call the database in a background task
         *
         * @param booleen flag describing if view needs to be recalculated
         * @return the query results
         */
        @Override
        protected QueryEnumerator doInBackground(Boolean... booleen) {

            // Get the flag that indicates if random order view
            // Needs to be re executed
            boolean forceViewUpdate = booleen[0];

            // Execute query
            return EarthViewImagesStore
                    .getInstance()
                    .getAllInRandomOrder(forceViewUpdate);
        }

        /**
         * Called when query is completed.
         * Sets results to the adapter and notify data set changed
         *
         * @param queryRows the query results
         */
        @Override
        protected void onPostExecute(QueryEnumerator queryRows) {

            // Update progress bar
            if(mMenu != null) {
                final MenuItem item = mMenu.findItem(R.id.action_shuffle);
                item.collapseActionView();
                item.setActionView(null);
            }

            // Notify adapter
            mImagesAdapter.setQuery(queryRows);
            mImagesAdapter.notifyDataSetChanged();
        }
    }
}
