package com.example.luisle.equiz.Model;

/**
 * Created by LuisLe on 2/4/2017.
 */

public class Exam {

    private String ID;
    private String title;
    private Integer numberOfQuestion;
    private Integer duration;
    private String dateCreated;
    private String level;

    // region GET SET

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getNumberOfQuestion() {
        return numberOfQuestion;
    }

    public void setNumberOfQuestion(Integer numberOfQuestion) {
        this.numberOfQuestion = numberOfQuestion;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    // endregion

    // region CONTRUCTOR

    public Exam() {
        // Firebase required
    }

//    public Exam(String ID, String title, Integer numberOfQuestion, Integer duration, String dateCreated) {
//        this.ID = ID;
//        this.title = title;
//        this.numberOfQuestion = numberOfQuestion;
//        this.duration = duration;
//        this.dateCreated = dateCreated;
//    }

    public Exam(String ID, String title, Integer duration) {
        this.ID = ID;
        this.title = title;
        this.duration = duration;
    }

    // endregion
}
