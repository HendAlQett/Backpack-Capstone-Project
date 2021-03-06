package com.hend.backpack.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hend on 8/12/16.
 */

public class Landmark implements Parcelable {
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
    int radius;
    @SerializedName("street_view")
    boolean street_view;

    public Landmark(int id, String name_en, String name_ar, String description_en, String description_ar, String image_url, double latitude, double longitude, int radius, boolean street_view) {
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

    protected Landmark(Parcel in) {
        id = in.readInt();
        name_en = in.readString();
        name_ar = in.readString();
        description_en = in.readString();
        description_ar = in.readString();
        image_url = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        radius = in.readInt();
        street_view = in.readByte() != 0;
    }

    public static final Creator<Landmark> CREATOR = new Creator<Landmark>() {
        @Override
        public Landmark createFromParcel(Parcel in) {
            return new Landmark(in);
        }

        @Override
        public Landmark[] newArray(int size) {
            return new Landmark[size];
        }
    };

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

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public boolean isStreet_view() {
        return street_view;
    }

    public void setStreet_view(boolean street_view) {
        this.street_view = street_view;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(name_en);
        parcel.writeString(name_ar);
        parcel.writeString(description_en);
        parcel.writeString(description_ar);
        parcel.writeString(image_url);
        parcel.writeDouble(latitude);
        parcel.writeDouble(longitude);
        parcel.writeInt(radius);
        parcel.writeByte((byte) (street_view ? 1 : 0));
    }
}
