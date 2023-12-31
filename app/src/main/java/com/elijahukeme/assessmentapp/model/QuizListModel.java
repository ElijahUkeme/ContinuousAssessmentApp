package com.elijahukeme.assessmentapp.model;

import com.google.firebase.firestore.DocumentId;

public class QuizListModel {

    @DocumentId
    private String quizId;
    private String image;
    private String title;

    public QuizListModel() {
    }

    public QuizListModel(String quizId, String image, String title) {
        this.quizId = quizId;
        this.image = image;
        this.title = title;
    }

    public String getQuizId() {
        return quizId;
    }

    public void setQuizId(String quizId) {
        this.quizId = quizId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
