package com.example.mytripsapplication;

import java.util.ArrayList;
import java.util.HashMap;


public class Trip {
    private double latitude,longitude;
    private String title,coverPhoto,location,tripId;
    private int creatorId;
    private ArrayList<Integer> users;
    private ArrayList<Message> chatRoom;

    public Trip() {
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

    public int getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(int creatorId) {
        this.creatorId = creatorId;
    }

    public ArrayList<Integer> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<Integer> users) {
        this.users = users;
    }

    public ArrayList<Message> getChatRoom() {
        return chatRoom;
    }

    public void setChatRoom(ArrayList<Message> chatRoom) {
        this.chatRoom = chatRoom;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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
                ", chatRoom=" + chatRoom +
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
        tripMap.put("chatroom",this.chatRoom);
        return tripMap;
    }

}
