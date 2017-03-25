package com.example.luisle.equiz.Model;

import android.text.style.QuoteSpan;

import java.util.List;

/**
 * Created by LuisLe on 2/4/2017.
 */

public class Question {

    private String ID;
    private String title;
    private String questionType;
    private List<String> choiceList;
    private List<String> resultList;

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

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public List<String> getChoiceList() {
        return choiceList;
    }

    public void setChoiceList(List<String> choiceList) {
        this.choiceList = choiceList;
    }

    public List<String> getResultList() {
        return resultList;
    }

    public void setResultList(List<String> resultList) {
        this.resultList = resultList;
    }

    // endregion

    // region CONSTRUCTOR

    public Question() {
        // Firebase required
    }

    public Question(String ID, String title, String questionType, List<String> choiceList, List<String> resultList) {
        this.ID = ID;
        this.title = title;
        this.questionType = questionType;
        this.choiceList = choiceList;
        this.resultList = resultList;
    }

    // endregion

}
