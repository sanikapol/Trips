package com.example.mytripsapplication;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class User implements Serializable {
    private String fname, lname, email,gender,profilePhoto;

    public User(String fname, String lname, String email) {
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


    public HashMap toHashMap(){
        HashMap<String, Object> userMap = new HashMap();
        userMap.put("fname",this.fname);
        userMap.put("lname",this.lname);
        userMap.put("email",this.email);
        userMap.put("gender",this.gender);
        userMap.put("profilePhoto",this.profilePhoto);
        return  userMap;
    }

    @Override
    public String toString() {
        return "User{" +
                "fname='" + fname + '\'' +
                ", lname='" + lname + '\'' +
                ", email='" + email + '\'' +
                ", gender='" + gender + '\'' +
                ", profilePhoto='" + profilePhoto + '\'' +
                '}';
    }
}
