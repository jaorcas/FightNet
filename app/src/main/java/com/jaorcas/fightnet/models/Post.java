package com.jaorcas.fightnet.models;

public class Post {

    private String idUser;
    private String idPost;
    private String gameTitle;
    private String description;
    private String descriptionLowCase;
    private String image;
    private String video;
    private String character;
    private long timestamp;

    public Post(){

    }

    public Post(String idUser, String idPost, String gameTitle, String description, String image, String video, long timestamp, String character) {
        this.idUser = idUser;
        this.idPost = idPost;
        this.gameTitle = gameTitle;
        this.description = description;
        this.image = image;
        this.video = video;
        this.timestamp = timestamp;
        this.character = character;

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

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getDescriptionLowCase() {
        return descriptionLowCase;
    }

    public void setDescriptionLowCase(String descriptionLowCase) {
        this.descriptionLowCase = descriptionLowCase;
    }

    public String getCharacter() {
        return character;
    }

    public void setCharacter(String character) {
        this.character = character;
    }
}
