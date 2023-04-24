package com.jaorcas.fightnet.models;

public class Like {

    private String id;
    private String postID;
    private String userID;
    private long timestamp;

    public Like(){}

    public Like(String id, String postID, String userID, long timestamp) {
        this.id = id;
        this.postID = postID;
        this.userID = userID;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
