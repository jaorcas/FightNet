package com.jaorcas.fightnet.models;

public class Comment {

    private String id;
    private String commentText;
    private String idUser;
    private String idPost;
    long timestamp;

    public Comment(){

    }

    public Comment(String id, String commentText, String idUser, String idPost, long timestamp) {
        this.id = id;
        this.commentText = commentText;
        this.idUser = idUser;
        this.idPost = idPost;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getIdPost() {
        return idPost;
    }

    public void setIdPost(String idPost) {
        this.idPost = idPost;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
