package com.elijahukeme.assessmentapp.interfaces;

import com.elijahukeme.assessmentapp.model.QuestionModel;

import java.util.List;

public interface OnQuestionLoad {

    void onLoad(List<QuestionModel> questionModelList);
    void onError(Exception e);
}
