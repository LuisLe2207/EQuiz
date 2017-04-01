package com.example.luisle.equiz.Model;

import java.util.List;

/**
 * Created by LuisLe on 2/4/2017.
 */

public class Question {

    private String ID;
    private String title;
    private String questionType;
    private List<Choice> choiceList;
    private List<Integer> answerList;

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

    public List<Choice> getChoiceList() {
        return choiceList;
    }

    public void setChoiceList(List<Choice> choiceList) {
        this.choiceList = choiceList;
    }

    public List<Integer> getAnswerList() {
        return answerList;
    }

    public void setAnswerList(List<Integer> resultList) {
        this.answerList = resultList;
    }

    // endregion

    // region CONSTRUCTOR

    public Question() {
        // Firebase required
    }

    public Question(String ID, String title, String questionType, List<Choice> choiceList, List<Integer> answerList) {
        this.ID = ID;
        this.title = title;
        this.questionType = questionType;
        this.choiceList = choiceList;
        this.answerList = answerList;
    }

    // endregion

}
