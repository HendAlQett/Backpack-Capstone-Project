package com.hend.backpack.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hend on 8/12/16.
 */

public class Landmark {
    @SerializedName("id")
    int id;
    @SerializedName("name_en")
    String name_en;
    @SerializedName("name_ar")
    String name_ar;
    @SerializedName("description_en")
    String description_en;
    @SerializedName("description_ar")
    String description_ar;
    @SerializedName("image_url")
    String image_url;
    @SerializedName("latitude")
    double latitude;
    @SerializedName("longitude")
    double longitude;
    @SerializedName("radius")
    double radius;
    @SerializedName("street_view")
    boolean street_view;

    public Landmark(int id, String name_en, String name_ar, String description_en, String description_ar, String image_url, double latitude, double longitude, double radius, boolean street_view) {
        this.id = id;
        this.name_en = name_en;
        this.name_ar = name_ar;
        this.description_en = description_en;
        this.description_ar = description_ar;
        this.image_url = image_url;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
        this.street_view = street_view;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName_en() {
        return name_en;
    }

    public void setName_en(String name_en) {
        this.name_en = name_en;
    }

    public String getName_ar() {
        return name_ar;
    }

    public void setName_ar(String name_ar) {
        this.name_ar = name_ar;
    }

    public String getDescription_en() {
        return description_en;
    }

    public void setDescription_en(String description_en) {
        this.description_en = description_en;
    }

    public String getDescription_ar() {
        return description_ar;
    }

    public void setDescription_ar(String description_ar) {
        this.description_ar = description_ar;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public boolean isStreet_view() {
        return street_view;
    }

    public void setStreet_view(boolean street_view) {
        this.street_view = street_view;
    }
}
