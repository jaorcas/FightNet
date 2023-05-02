package com.jaorcas.fightnet.models;

import java.io.Serializable;

public class Character implements Serializable {

    private String name;
    private String imageURL;
    private String imageURL_fullbody;

    public Character(){

    }

    public Character(String name, String imageURL, String imageURL_fullbody) {
        this.name = name;
        this.imageURL = imageURL;
        this.imageURL_fullbody = imageURL_fullbody;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getImageURL_fullbody() {
        return imageURL_fullbody;
    }

    public void setImageURL_fullbody(String imageURL_fullbody) {
        this.imageURL_fullbody = imageURL_fullbody;
    }
}
