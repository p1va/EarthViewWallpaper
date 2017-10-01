package com.github.p1va.earthviewwallpaper.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.github.p1va.earthviewwallpaper.R;
import com.github.p1va.earthviewwallpaper.adapters.EarthViewImagesAdapter;
import com.github.p1va.earthviewwallpaper.data.persistance.EarthViewImagesStore;
import com.github.p1va.earthviewwallpaper.util.MeasuramentUtils;
import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;

public class MainActivity extends AppCompatActivity {

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
        EarthViewImagesAdapter mImagesAdapter = new EarthViewImagesAdapter(this,
                EarthViewImagesStore
                        .getInstance()
                        .getAllInRandomOrder());

        // Create grid layout manager
        RecyclerView.LayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 2);

        // Set the layout manager to the recycler view
        recyclerView.setLayoutManager(gridLayoutManager);

        // Set the images adapter to the recycler view
        recyclerView.setAdapter(mImagesAdapter);

        // Set spacing between images
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, MeasuramentUtils.convertDpToPx(4, this), true));
    }

    /**
     * Called when creating options menu
     *
     * @param menu the menu
     * @return a flag describing success
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
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

        int id = item.getItemId();

        if(id == R.id.action_about) {
            startAboutActivity();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Starts the about app activity
     */
    private void startAboutActivity() {
        new LibsBuilder()
                .withActivityStyle(Libs.ActivityStyle.LIGHT)
                .withAboutIconShown(true)
                .withAboutVersionShown(true)
                .withAboutDescription("This is a small sample which can be set in the about my app description file.<br /><b>You can style this with html markup :D</b>")
                .start(this);
    }
}
