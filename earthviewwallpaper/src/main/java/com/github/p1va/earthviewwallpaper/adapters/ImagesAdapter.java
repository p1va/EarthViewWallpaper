package com.github.p1va.earthviewwallpaper.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.p1va.earthviewwallpaper.R;
import com.github.p1va.earthviewwallpaper.data.model.EarthViewImage;
import com.github.p1va.earthviewwallpaper.ui.SetWallpaperActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import jp.wasabeef.picasso.transformations.CropTransformation;

/**
 * The images recycler view adapter
 */
public class ImagesAdapter extends RecyclerView.Adapter<ImageViewHolder> {

    /**
     * The images array list
     */
    public ArrayList<EarthViewImage> images = new ArrayList<>();

    /**
     * The context
     */
    private Context mContext;

    /**
     * Creates new instance of ImagesAdapter
     *
     * @param context the context
     */
    public ImagesAdapter(Context context) {
        mContext = context;
    }

    /**
     * Called when view holder is created
     *
     * @param parent   the parent view group
     * @param viewType the parent view type
     * @return the view holder
     */
    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // Get layout view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_preview, parent, false);

        // Create view holder
        return new ImageViewHolder(view);
    }

    /**
     * Called when view holder is binded to view
     *
     * @param holder   the view holder instance
     * @param position the position
     */
    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {

        // Get corresponding image
        final EarthViewImage image = images.get(position);

        // Get thumb URI
        Uri uri = Uri.parse(image.thumbUrl);

        // Set on click listener
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, SetWallpaperActivity.class);
                intent.putExtra("url", image.photoUrl);
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
        return images.size();
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