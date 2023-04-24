package com.jaorcas.fightnet.models;

public class Post {

    private String idUser;
    private String idPost;
    private String gameTitle;
    private String description;
    private String image;
    private long timestamp;

    public Post(){

    }

    public Post(String idUser, String idPost, String gameTitle, String description, String image, long timestamp) {
        this.idUser = idUser;
        this.idPost = idPost;
        this.gameTitle = gameTitle;
        this.description = description;
        this.image = image;
        this.timestamp = timestamp;
    }

    public String getIdUser(){
        return idUser;
    }
    public void setIdUser(String idUser){
        this.idUser = idUser;
    }

    public String getIdPost() {
        return idPost;
    }

    public void setIdPost(String idPost) {
        this.idPost = idPost;
    }

    public String getGameTitle() {
        return gameTitle;
    }

    public void setGameTitle(String gameTitle) {
        this.gameTitle = gameTitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
