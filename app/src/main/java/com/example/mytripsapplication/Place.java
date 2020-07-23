package com.example.mytripsapplication;

import java.io.Serializable;

public class Place implements Serializable {
    private String placeId,placeName,placeIcon;
    private Double latitude,longitude,rating;
    private boolean openNow;

    public Place() {
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getPlaceIcon() {
        return placeIcon;
    }

    public void setPlaceIcon(String placeIcon) {
        this.placeIcon = placeIcon;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public boolean isOpenNow() {
        return openNow;
    }

    public void setOpenNow(boolean openNow) {
        this.openNow = openNow;
    }


    @Override
    public String toString() {
        return "Place{" +
                "placeId='" + placeId + '\'' +
                ", placeName='" + placeName + '\'' +
                ", placeIcon='" + placeIcon + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", rating=" + rating +
                ", openNow=" + openNow +
                '}';
    }
}
