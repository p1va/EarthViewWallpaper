package com.github.p1va.earthviewwallpaper.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.couchbase.lite.QueryEnumerator;
import com.github.p1va.earthviewwallpaper.R;
import com.github.p1va.earthviewwallpaper.data.model.EarthViewImage;
import com.github.p1va.earthviewwallpaper.ui.SetWallpaperActivity;
import com.squareup.picasso.Picasso;

import jp.wasabeef.picasso.transformations.CropTransformation;

/**
 * The images recycler view adapter
 */
public class EarthViewImagesAdapter extends RecyclerView.Adapter<EarthViewImageViewHolder> {

    /**
     * The context
     */
    private Context mContext;

    /**
     * The query
     */
    private QueryEnumerator mQuery;

    /**
     * Creates new instance of EarthViewImagesAdapter
     *
     * @param context the context
     */
    public EarthViewImagesAdapter(Context context, QueryEnumerator query) {
        mContext = context;
        mQuery = query;
    }

    /**
     * Sets the query
     *
     * @param query the query
     */
    public void setQuery(QueryEnumerator query) {
        mQuery = query;
    }

    /**
     * Called when view holder is created
     *
     * @param parent   the parent view group
     * @param viewType the parent view type
     * @return the view holder
     */
    @Override
    public EarthViewImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // Get layout view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_preview, parent, false);

        // Create view holder
        return new EarthViewImageViewHolder(view);
    }

    /**
     * Called when view holder is binded to view
     *
     * @param holder   the view holder instance
     * @param position the position
     */
    @Override
    public void onBindViewHolder(EarthViewImageViewHolder holder, int position) {

        // Get corresponding image
        final EarthViewImage image = getItem(position);

        // Get thumb URI
        Uri uri = Uri.parse(image.thumbUrl);

        // Set on click listener
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start set wallpaper activity
                Intent intent = new Intent(mContext, SetWallpaperActivity.class);
                intent.putExtra("url", image.photoUrl);
                intent.putExtra("previewUrl", image.thumbUrl);
                intent.putExtra("label", getLabel(image));
                intent.putExtra("attribution", image.attribution);
                intent.putExtra("mapsLink", image.mapsLink);

                mContext.startActivity(intent);
            }
        });

        int width = 250;
        int height = 400;

        // Declare Picasso portrait crop transformation
        CropTransformation transformation = new CropTransformation(
                width, height,
                CropTransformation.GravityHorizontal.CENTER,
                CropTransformation.GravityVertical.CENTER);

        // Load and transform image into image view
        Picasso.with(mContext)
                .load(uri)
                .transform(transformation)
                .into(holder.imageView);

        String label = getLabel(image);

        // Set image label
        holder.textView.setText(label);
    }

    /**
     * Gets the items count
     *
     * @return the items count
     */
    @Override
    public int getItemCount() {
        return mQuery != null ? mQuery.getCount() : 0;
    }

    /**
     * Gets the item at the specified position
     *
     * @param position the position
     * @return the item at the position
     */
    private EarthViewImage getItem(int position) {
        return EarthViewImage.fromDocument(
                mQuery.getRow(position).getDocument());
    }

    /**
     * Gets the image label
     *
     * @param image the image
     * @return the label
     */
    @NonNull
    private String getLabel(EarthViewImage image) {
        String label = "";

        if(image.region != null)
            label += image.region;

        if(image.region != null && image.country != null)
            label += ", ";

        if(image.country != null)
            label += image.country;
        return label;
    }
}