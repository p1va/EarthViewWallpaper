package com.github.p1va.earthviewwallpaper.ui;

import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.github.p1va.earthviewwallpaper.R;
import com.squareup.picasso.Picasso;

import java.io.IOException;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SetWallpaperActivity extends AppCompatActivity {

    Uri mUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_set_wallpaper);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TouchImageView mImageView = (TouchImageView) findViewById(R.id.image);
        mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        mImageView.resetZoom();

        String url = getIntent().getStringExtra("url");
        String label = getIntent().getStringExtra("label");

        getSupportActionBar().setTitle(label);

        mUri = Uri.parse(url);

        Picasso.with(this)
                .load(mUri)
                .into(mImageView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.set_wallpaper_menu, menu);
        return true;
    }

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

    private class SetWallpaperTask extends AsyncTask<Uri, Integer, Integer> {

        @Override
        protected Integer doInBackground(Uri... urls) {

            Bitmap result = null;
            try {
                result = Picasso.with(SetWallpaperActivity.this)
                        .load(urls[0])
                        .get();
            } catch (IOException e) {
                e.printStackTrace();
            }

            WallpaperManager wallpaperManager = WallpaperManager.getInstance(SetWallpaperActivity.this);
            try {
                wallpaperManager.setBitmap(result);
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            return 0;
        }
    }
}
