package com.example.luisle.equiz.Model;

import java.util.List;

/**
 * Created by LuisLe on 2/4/2017.
 */

public class Result {

    private String questionID;
    private List<String> answerList;
    private Boolean boolResult;

    // region GET and SET

    public String getQuestionID() {
        return questionID;
    }

    public void setQuestionID(String questionID) {
        this.questionID = questionID;
    }

    public List<String> getAnswerList() {
        return answerList;
    }

    public void setAnswerList(List<String> answerList) {
        this.answerList = answerList;
    }

    public Boolean getBoolResult() {
        return boolResult;
    }

    public void setBoolResult(Boolean boolResult) {
        this.boolResult = boolResult;
    }

    // endregion

    // region CONSTRUCTOR

    public Result() {
        
    }

    public Result(String questionID, List<String> answerList, Boolean boolResult) {
        this.questionID = questionID;
        this.answerList = answerList;
        this.boolResult = boolResult;
    }

    // endregion
}
