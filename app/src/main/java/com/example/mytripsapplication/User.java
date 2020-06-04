package com.example.mytripsapplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class User {
    private static final AtomicInteger count = new AtomicInteger(0);
    private String fname, lname, email,gender,profilePhoto;
    private int userId;
    private ArrayList<Trip> trips;

    public User(String fname, String lname, String email) {
        this.userId = count.incrementAndGet();
        this.fname = fname;
        this.lname = lname;
        this.email = email;
    }

    public User() {
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public ArrayList<Trip> getTrips() {
        return trips;
    }

    public void setTrips(ArrayList<Trip> trips) {
        this.trips = trips;
    }

    public HashMap toHashMap(){
        HashMap<String, Object> userMap = new HashMap();
        userMap.put("userId",this.userId);
        userMap.put("fname",this.fname);
        userMap.put("lname",this.lname);
        userMap.put("email",this.email);
        userMap.put("gender",this.gender);
        userMap.put("profilePhoto",this.profilePhoto);
        userMap.put("MyTrips",this.trips);
        return  userMap;
    }


}
