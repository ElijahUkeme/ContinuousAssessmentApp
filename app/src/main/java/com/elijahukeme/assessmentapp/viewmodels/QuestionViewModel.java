package com.elijahukeme.assessmentapp.viewmodels;

import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.elijahukeme.assessmentapp.interfaces.OnQuestionLoad;
import com.elijahukeme.assessmentapp.model.QuestionModel;
import com.elijahukeme.assessmentapp.repositories.QuestionRepository;

import java.util.List;

public class QuestionViewModel extends ViewModel implements OnQuestionLoad {

    private MutableLiveData<List<QuestionModel>> mutableLiveData;
    private QuestionRepository questionRepository;

    public MutableLiveData<List<QuestionModel>> getMutableLiveData() {
        return mutableLiveData;
    }

    public QuestionViewModel(){
        mutableLiveData = new MutableLiveData<>();
        questionRepository = new QuestionRepository(this);
    }
    public void setQuizId(String quizId){
        questionRepository.setQuizId(quizId);
        questionRepository.getQuestions();
    }

    @Override
    public void onLoad(List<QuestionModel> questionModelList) {
        mutableLiveData.setValue(questionModelList);
    }

    @Override
    public void onError(Exception e) {
        Log.d("Error",e.getMessage());
    }
}
