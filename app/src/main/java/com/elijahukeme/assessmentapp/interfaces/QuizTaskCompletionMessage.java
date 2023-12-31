package com.elijahukeme.assessmentapp.interfaces;

import com.elijahukeme.assessmentapp.model.QuizListModel;

import java.util.List;

public interface QuizTaskCompletionMessage {

    void quizDataLoaded(List<QuizListModel> quizListModels);
    void onError(Exception e);
}
