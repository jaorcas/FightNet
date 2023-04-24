package com.jaorcas.fightnet.models;

public class User {

    private String id;
    private String email;
    private String username;
    private String userDescription;
    private String imageProfile;
    private String imageBanner;

    private long timestamp;

    public User(){

    }

    public User(String id, String email, String username, long timeStamp, String imageProfile, String imageBanner, String userDescription){
        this.id = id;
        this.email = email;
        this.username = username;
        this.timestamp = timeStamp;
        this.imageProfile = imageProfile;
        this.imageBanner = imageBanner;
        this.userDescription = userDescription;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getImageProfile() {
        return imageProfile;
    }

    public void setImageProfile(String imageProfile) {
        this.imageProfile = imageProfile;
    }

    public String getImageBanner() {
        return imageBanner;
    }

    public void setImageBanner(String imageBanner) {
        this.imageBanner = imageBanner;
    }

    public String getUserDescription() {return userDescription;}
    
    public void setUserDescription(String userDescription) {this.userDescription = userDescription;   }
}

