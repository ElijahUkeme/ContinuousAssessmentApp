package com.elijahukeme.assessmentapp.interfaces;

public interface ImageUploadCallBack {

    void onImageUploadSuccess(String imageUrl);
    void onImageUploadFailure(String errorMessage);
}
