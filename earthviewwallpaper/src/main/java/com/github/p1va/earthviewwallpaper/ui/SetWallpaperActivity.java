package com.github.p1va.earthviewwallpaper.ui;

import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.github.p1va.earthviewwallpaper.R;
import com.github.p1va.earthviewwallpaper.util.LayoutUtils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;

import timber.log.Timber;

public class SetWallpaperActivity extends AppCompatActivity {

    /**
     * The URI of the image
     */
    String mUri;

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
     * The animation view
     */
    LottieAnimationView mAnimationView;

    /**
     * The error layout
     */
    LinearLayoutCompat mErrorLayout;

    private void hideLoadingAnimation() {
        // Stop animation view if playing
        if(mAnimationView.isAnimating()) {
            mAnimationView.pauseAnimation();
        }

        // Hide animation view
        mAnimationView.setVisibility(View.GONE);
    }

    private void showErrorMessage() {
        mErrorLayout.setVisibility(View.VISIBLE);
    }

    /**
     * The target
     */
    Target mTarget = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            Timber.d("on bitmap loaded");

            mImage = bitmap;

            // Get image view
            mImageView = (TouchImageView) findViewById(R.id.set_wallpaper_image);
            mImageView.setImageBitmap(bitmap);
            mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            mImageView.resetZoom();

            // Stop animation view if playing
            hideLoadingAnimation();
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            hideLoadingAnimation();
            showErrorMessage();
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
            Timber.d("on prepare load");
        }
    };

    /**
     * The menu
     */
    Menu mMenu;

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
        mUri = getIntent().getStringExtra("url");
        String label = getIntent().getStringExtra("label");
        String attribution = getIntent().getStringExtra("attribution");
        final String mapsLink = getIntent().getStringExtra("mapsLink");

        // Set system layout to fullscreen
        LayoutUtils.setSystemUiToFullscreen(getWindow());

        // Find both status and nav bars heights
        int statusBarHeight = LayoutUtils.getStatusBarHeight(this);
        int navBarHeight = LayoutUtils.getNavBarHeight(this);

        // Get the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.set_wallpaper_toolbar);

        // Set top margin of app bar layout equals to status bar height
        // In this way they don't overlap each others
        LayoutUtils.setMargins(toolbar, 0, statusBarHeight, 0, 0);

        // Set toolbar as a support action bar
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Find bottom layout
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.set_wallpaper_text_layout);

        int id = getResources().getIdentifier("config_showNavigationBar", "bool", "android");
        if(id > 0 && getResources().getBoolean(id)) {
            // Set his bottom margin equals to nav bar height so that they don't overlap each others
            LayoutUtils.setMargins(linearLayout, 0, 0, 0, navBarHeight);
        }

        // Find text views
        AppCompatTextView locationTextView = (AppCompatTextView) findViewById(R.id.set_wallpaper_location_text_view);
        AppCompatTextView attributionTextView = (AppCompatTextView) findViewById(R.id.set_wallpaper_attribution_text_view);
        AppCompatTextView exploreTextView = (AppCompatTextView) findViewById(R.id.set_wallpaper_explore_text_view);

        // Set label and attribution values
        locationTextView.setText(label);
        attributionTextView.setText(attribution);
        exploreTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent linkIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mapsLink));
                startActivity(linkIntent);
            }
        });

        Drawable normalDrawable = ContextCompat.getDrawable(this, R.mipmap.ic_explore_black_24dp);
        Drawable wrapDrawable = DrawableCompat.wrap(normalDrawable);
        DrawableCompat.setTint(wrapDrawable, ContextCompat.getColor(this, R.color.colorExploreLink));

        exploreTextView.setCompoundDrawablesWithIntrinsicBounds(wrapDrawable, null, null, null);

        // Find error layout
        mErrorLayout = (LinearLayoutCompat) findViewById(R.id.set_wallpaper_error_layout);

        // Find animation view
        mAnimationView = (LottieAnimationView) findViewById(R.id.set_wallpaper_animation_view);
        mAnimationView.setAnimation("whale.json", LottieAnimationView.CacheStrategy.Strong);
        mAnimationView.loop(true);
        mAnimationView.playAnimation();

        // Get image view
        mImageView = (TouchImageView) findViewById(R.id.set_wallpaper_image);
        mImageView.setImageResource(R.mipmap.transparent);

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
        mMenu = menu;
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
        switch (item.getItemId()) {

            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;

            case R.id.action_set_wallpaper:
                if(mImage != null) new SetWallpaperTask().execute();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * The set wallpaper task
     */
    private class SetWallpaperTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... params) {

            WallpaperManager wallpaperManager = WallpaperManager.getInstance(SetWallpaperActivity.this);

            try {
                wallpaperManager.setBitmap(mImage);
            } catch (IOException ex) {
                Timber.w("Something failed while setting wallpaper", ex);
            }

            return 0;
        }

        @Override
        protected void onPreExecute() {
            final MenuItem item = mMenu.findItem(R.id.action_set_wallpaper);
            item.setActionView(R.layout.action_set_wallpaper_indeterminate_progress);
            item.expandActionView();
        }

        @Override
        protected void onPostExecute(Integer result) {

            final MenuItem item = mMenu.findItem(R.id.action_set_wallpaper);
            item.collapseActionView();
            item.setActionView(null);

            // Display a toast
            Toast toast = Toast.makeText(
                    SetWallpaperActivity.this,
                    SetWallpaperActivity.this.getResources().getString(R.string.wallpaper_set_toast),
                    Toast.LENGTH_SHORT);

            toast.show();
        }
    }
}
