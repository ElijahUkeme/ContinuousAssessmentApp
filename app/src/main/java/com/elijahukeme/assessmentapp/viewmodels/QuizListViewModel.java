package com.elijahukeme.assessmentapp.viewmodels;

import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.elijahukeme.assessmentapp.interfaces.QuizTaskCompletionMessage;
import com.elijahukeme.assessmentapp.model.QuizListModel;
import com.elijahukeme.assessmentapp.repositories.QuizRepository;

import java.util.List;

public class QuizListViewModel extends ViewModel implements QuizTaskCompletionMessage {

    private MutableLiveData<List<QuizListModel>> quizListLiveData = new MutableLiveData<>();

    public MutableLiveData<List<QuizListModel>> getQuizListLiveData() {
        return quizListLiveData;
    }

    private QuizRepository repository = new QuizRepository(this);

    public QuizListViewModel(){
        repository.getQuizData();
    }
    @Override
    public void quizDataLoaded(List<QuizListModel> quizListModels) {
        quizListLiveData.setValue(quizListModels);
    }

    @Override
    public void onError(Exception e) {
        Log.d("Main",e.getMessage());
    }
}
