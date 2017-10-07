package com.github.p1va.earthviewwallpaper.data.model;

import com.couchbase.lite.Document;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;

import java.util.ArrayList;

/**
 * Created by Stefano Piva on 24/09/2017.
 */

/**
 * The EarthViewImage class
 */
public class EarthViewImage {

    /**
     * The id
     */
    public String id;

    /**
     * The API url
     */
    public String api;

    /**
     * The attribution
     */
    public String attribution;
    /**
     * The country
     */
    public String country;

    /**
     * The download url
     */
    public String downloadUrl;

    /**
     * The Google Earth link
     */
    public String earthLink;

    /**
     * The Google Earth title
     */
    public String earthTitle;

    /**
     * The latitude
     */
    public String latitude;

    /**
     * The longitude
     */
    public String longitude;

    /**
     * The Google Maps link
     */
    public String mapsLink;

    /**
     * The Google Maps title
     */
    public String mapsTitle;

    /**
     * The next image API url
     */
    public String nextApi;

    /**
     * The next image url
     */
    public String nextUrl;

    /**
     * The url of the image
     */
    public String photoUrl;

    /**
     * The previous image API url
     */
    public String prevApi;

    /**
     * The previous image url
     */
    public String prevUrl;

    /**
     * The region of the image
     */
    public String region;

    /**
     * The image slug
     */
    public String slug;

    /**
     * The thumb image url
     */
    public String thumbUrl;

    /**
     * The image title
     */
    public String title;

    /**
     * The image url
     */
    public String url;

    /**
     * Converts the document to an EarthViewImage instance
     * @param document the document
     * @return the image
     */
    public static EarthViewImage fromDocument(Document document) {
        return convertFromDocument(document);
    }

    /**
     * Converts the document to an EarthViewImage instance with only mandatory fields valorized
     *
     * @param document the document
     * @return the image
     */
    private static EarthViewImage convertFromDocument(Document document) {
        EarthViewImage image = new EarthViewImage();
        image.attribution = (String) document.getProperty("attribution");
        image.country = (String) document.getProperty("country");
        image.mapsLink = (String) document.getProperty("mapsLink");
        image.region = (String) document.getProperty("region");
        image.title = (String) document.getProperty("title");
        image.photoUrl = (String) document.getProperty("photoUrl");
        image.thumbUrl = (String) document.getProperty("thumbUrl");
        return image;
    }

    /**
     * Converts the document to an EarthViewImage instance with all the fields valorized
     *
     * @param document the document
     * @return the image
     */
    private static EarthViewImage convertFromDocumentWithAllFields(Document document) {
        EarthViewImage image = new EarthViewImage();
        image.api = (String) document.getProperty("api");
        image.attribution = (String) document.getProperty("attribution");
        image.country = (String) document.getProperty("country");
        image.downloadUrl = (String) document.getProperty("downloadUrl");
        image.earthLink = (String) document.getProperty("earthLink");
        image.earthTitle = (String) document.getProperty("earthTitle");
        image.id = (String) document.getProperty("id");
        image.latitude = (String) document.getProperty("latitude");
        image.longitude = (String) document.getProperty("longitude");
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

    /**
     * Converts query results to an array list of images
     *
     * @param query the query results
     * @return the array of images
     */
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
}