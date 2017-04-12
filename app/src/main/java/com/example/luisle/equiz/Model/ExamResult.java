package com.example.luisle.equiz.Model;

import java.util.List;

/**
 * Created by LuisLe on 4/11/2017.
 */

public class ExamResult {

    private String ID;
    private String examTitle;
    private Long completeTime;
    private Integer correctAnswer;
    private List<QuestionResult> questionResults;


    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getExamTitle() {
        return examTitle;
    }

    public void setExamTitle(String examTitle) {
        this.examTitle = examTitle;
    }

    public Long getCompleteTime() {
        return completeTime;
    }

    public void setCompleteTime(Long completeTime) {
        this.completeTime = completeTime;
    }

    public Integer getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(Integer correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public List<QuestionResult> getQuestionResults() {
        return questionResults;
    }

    public void setQuestionResults(List<QuestionResult> questionResults) {
        this.questionResults = questionResults;
    }

    public ExamResult() {

    }

    public ExamResult(String ID, String examTitle, Long completeTime, Integer correctAnswer, List<QuestionResult> questionResults) {
        this.ID = ID;
        this.examTitle = examTitle;
        this.completeTime = completeTime;
        this.correctAnswer = correctAnswer;
        this.questionResults = questionResults;
    }
}
