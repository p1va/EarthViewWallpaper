package com.github.p1va.earthviewwallpaper.ui;

import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.p1va.earthviewwallpaper.R;
import com.github.p1va.earthviewwallpaper.util.LayoutUtils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;

import timber.log.Timber;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SetWallpaperActivity extends AppCompatActivity {

    /**
     * The URI of the image
     */
    Uri mUri;

    /**
     * The image view responsible for showing the image
     * Note that this need to be kept as a field to prevent it from being garbage collected
     * And resulting in Picasso not being able to load image in it as it keeps onlu a weak ref
     */
    TouchImageView mImageView;

    /**
     * The image
     */
    Bitmap mImage;

    /**
     * The target
     */
    Target mTarget = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            Timber.d("on bitmap loaded");
            mImage = bitmap;
            mImageView.setImageBitmap(bitmap);

            //Blurry.with(SetWallpaperActivity.this).from(bitmap).into(mImageView);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            Timber.w("on bitmap failed");
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
            Timber.d("on prepare load");
        }
    };

    /**
     * Called when activity is created
     *
     * @param savedInstanceState the saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_set_wallpaper);

        // Get extra arguments
        String url = getIntent().getStringExtra("url");
        String label = getIntent().getStringExtra("label");
        String attribution = getIntent().getStringExtra("attribution");

        // Parse the uri
        mUri = Uri.parse(url);

        // Find both status and nav bars heights
        int statusBarHeight = LayoutUtils.getStatusBarHeight(this);
        int navBarHeight = LayoutUtils.getNavBarHeight(this);

        // Set top margin of app bar layout equals to status bar height
        // In this way they don't overlap each others
        LayoutUtils.setMargins(findViewById(R.id.set_wallpaper_appbar), 0, statusBarHeight, 0, 0);

        // Get the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.set_wallpaper_toolbar);

        // Set toolbar as a support action bar
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Set system layout to fullscreen
        LayoutUtils.setSystemUiToFullscreen(getWindow());

        // Find bottom layout
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.set_wallpaper_text_layout);

        // Set his bottom margin equals to nav bar height so that they don't overlap each others
        LayoutUtils.setMargins(linearLayout, 0, 0, 0, navBarHeight);

        // Find text views
        TextView locationTextView = (TextView) findViewById(R.id.set_wallpaper_location_text_view);
        TextView attributionTextView = (TextView) findViewById(R.id.set_wallpaper_attribution_text_view);

        // Set label and attribution values
        locationTextView.setText(label);
        attributionTextView.setText(attribution);

        // Get image view
        mImageView = (TouchImageView) findViewById(R.id.set_wallpaper_image);
        mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        mImageView.resetZoom();
        mImageView.setImageResource(R.mipmap.ic_launcher);

        // Load image into the target
        Picasso.with(this)
                .load(mUri)
                .into(mTarget);
    }

    /**
     * Called when options menu is created
     *
     * @param menu the menu
     * @return a flag
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.set_wallpaper_menu, menu);
        return true;
    }

    /**
     * Called when one of the item in the options menu is selected
     *
     * @param item the menu item
     * @return a flag
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home) {
            // This ID represents the Home or Up button.
            NavUtils.navigateUpFromSameTask(this);
            return true;
        } else if(id == R.id.action_set_wallpaper) {
            new SetWallpaperTask().execute(mUri);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * The set wallpaper task
     */
    private class SetWallpaperTask extends AsyncTask<Uri, Integer, Integer> {

        @Override
        protected Integer doInBackground(Uri... urls) {

            WallpaperManager wallpaperManager = WallpaperManager.getInstance(SetWallpaperActivity.this);

            try {
                wallpaperManager.setBitmap(mImage);
            } catch (IOException ex) {
                Timber.w("Something failed while setting wallpaper", ex);
            }

            return 0;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            Toast toast = Toast.makeText(SetWallpaperActivity.this, "Wallpaper set", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}
