package com.example.luisle.equiz.Model;

/**
 * Created by LuisLe on 4/5/2017.
 */

public class Comment {

    private String ID;
    private String authorID;
    private String dateCreated;
    private String content;
    private Float rate;


    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getAuthorID() {
        return authorID;
    }

    public void setAuthorID(String authorID) {
        this.authorID = authorID;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Float getRate() {
        return rate;
    }

    public void setRate(Float rate) {
        this.rate = rate;
    }


    public Comment() {
        // Firebase required
    }

    public Comment(String ID, String authorID, String dateCreated, String content, Float rate) {
        this.ID = ID;
        this.authorID = authorID;
        this.dateCreated = dateCreated;
        this.content = content;
        this.rate = rate;
    }
}
