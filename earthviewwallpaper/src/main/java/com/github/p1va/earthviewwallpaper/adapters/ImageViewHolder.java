package com.github.p1va.earthviewwallpaper.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.p1va.earthviewwallpaper.R;

/**
 * Created by Stefano Piva on 24/09/2017.
 */

/**
 * The image view holder
 */
class ImageViewHolder extends RecyclerView.ViewHolder {

    /**
     * the image view
     */
    ImageView imageView;

    /**
     * the text view
     */
    TextView textView;

    /**
     * Initializes new instance of view holder
     * @param view the parent view
     */
    ImageViewHolder(View view) {
        super(view);

        // Create new image view
        this.imageView = view.findViewById(R.id.image_preview);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        // Create new text view
        this.textView = view.findViewById(R.id.image_label);
    }
}