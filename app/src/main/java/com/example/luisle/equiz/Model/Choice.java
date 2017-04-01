package com.example.luisle.equiz.Model;

/**
 * Created by LuisLe on 4/1/2017.
 */

public class Choice {

    private Integer ID;
    private String content;

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Choice(Integer ID, String content) {
        this.ID = ID;
        this.content = content;
    }
}
