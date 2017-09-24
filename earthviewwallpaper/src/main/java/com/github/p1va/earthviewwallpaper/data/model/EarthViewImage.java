package com.github.p1va.earthviewwallpaper.data.model;

import com.couchbase.lite.Document;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

/**
 * Created by Stefano Piva on 24/09/2017.
 */

public class EarthViewImage {

    public String id;
    public String api;
    public String attribution;
    public String country;
    public String downloadUrl;
    public String earthLink;
    public String earthTitle;
    public String lat;
    public String lng;
    public String mapsLink;
    public String mapsTitle;
    public String nextApi;
    public String nextUrl;
    public String photoUrl;
    public String prevApi;
    public String prevUrl;
    public String region;
    public String slug;
    public String thumbUrl;
    public String title;
    public String url;

    public static EarthViewImage fromDocument(Document document) {
        EarthViewImage image = new EarthViewImage();
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
        return image;
    }

    public static ArrayList<EarthViewImage> fromQuery(QueryEnumerator query) {
        ArrayList<EarthViewImage> results = new ArrayList<>();

        // Iterate on each one of the results
        for (QueryRow row : query) {

            Document document = row.getDocument();

            // Create an image from the document
            EarthViewImage image = EarthViewImage.fromDocument(document);

            // Add it to the adapter collection
            results.add(image);
        }

        return results;
    }

    public static Map<String, Integer> mapDatabaseToImageCount(QueryEnumerator databaseResults) {

        Map<String, Integer> results = new HashMap<>();

        // Iterate on each one of the results
        for (QueryRow row : databaseResults) {

            results.put((String) row.getKey(), (Integer) row.getValue());

            // Create
            Timber.d(row.toString());
        }

        return results;
    }
}