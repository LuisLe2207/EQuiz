package com.example.luisle.equiz.Model;

/**
 * Created by LuisLe on 4/5/2017.
 */

public class Comment {

    private String ID;
    private String authorName;
    private String dateCreated;
    private String content;
    private Integer rate;


    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
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

    public Integer getRate() {
        return rate;
    }

    public void setRate(Integer rate) {
        this.rate = rate;
    }


    public Comment() {
        // Firebase required
    }

    public Comment(String ID, String authorName, String dateCreated, String content, Integer rate) {
        this.ID = ID;
        this.authorName = authorName;
        this.dateCreated = dateCreated;
        this.content = content;
        this.rate = rate;
    }
}
