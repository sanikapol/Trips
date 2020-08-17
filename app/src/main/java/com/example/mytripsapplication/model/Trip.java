package com.example.mytripsapplication.model;

import com.example.mytripsapplication.model.Place;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;


public class Trip implements Serializable {
    private double latitude,longitude;
    private String title,coverPhoto,location,tripId;
    private String creatorId;
    private ArrayList<String> users;
    private ArrayList<Place> places;
    private boolean expanded;

    public Trip() {
        this.places = new ArrayList<Place>();
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCoverPhoto() {
        return coverPhoto;
    }

    public void setCoverPhoto(String coverPhoto) {
        this.coverPhoto = coverPhoto;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public ArrayList<String> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<String> users) {
        this.users = users;
    }


    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public ArrayList<Place> getPlaces() {
        return places;
    }

    public void setPlaces(ArrayList<Place> places) {
        this.places = places;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    @Override
    public String toString() {
        return "Trip{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", title='" + title + '\'' +
                ", coverPhoto='" + coverPhoto + '\'' +
                ", location='" + location + '\'' +
                ", tripId=" + tripId +
                ", creatorId=" + creatorId +
                ", users=" + users +
                ", places=" + places +
                '}';
    }

    public HashMap toHashMap(){
        HashMap<String, Object> tripMap = new HashMap<>();
        tripMap.put("tripId",this.tripId);
        tripMap.put("title",this.title);
        tripMap.put("location",this.location);
        tripMap.put("coverPhoto",this.coverPhoto);
        tripMap.put("latitude",this.latitude);
        tripMap.put("longitude",this.longitude);
        tripMap.put("creatorId",this.creatorId);
        tripMap.put("users",this.users);
        tripMap.put("places",this.places);
        tripMap.put("expanded",this.expanded);
        return tripMap;
    }

}
