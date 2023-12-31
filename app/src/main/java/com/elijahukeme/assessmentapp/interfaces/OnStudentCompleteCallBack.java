package com.elijahukeme.assessmentapp.interfaces;

import com.elijahukeme.assessmentapp.model.StudentModel;

public interface OnStudentCompleteCallBack {
    void onLoginComplete(StudentModel studentModel);
    void onError(String errorMessage);
}
