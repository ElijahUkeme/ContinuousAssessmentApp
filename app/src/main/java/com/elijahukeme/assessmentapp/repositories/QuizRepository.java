package com.elijahukeme.assessmentapp.repositories;

import android.util.Log;

import androidx.annotation.NonNull;

import com.elijahukeme.assessmentapp.interfaces.QuizTaskCompletionMessage;
import com.elijahukeme.assessmentapp.model.QuizListModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class QuizRepository {

    private FirebaseFirestore firebaseFirestore= FirebaseFirestore.getInstance();
    private CollectionReference reference = firebaseFirestore.collection("Quiz");
    private QuizTaskCompletionMessage quizTaskCompletionMessage;
    public QuizRepository(QuizTaskCompletionMessage quizTaskCompletionMessage){
        this.quizTaskCompletionMessage = quizTaskCompletionMessage;
    }


    public void getQuizData(){
        reference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    Log.d("Main",task.getResult().toString());
                    System.out.println("The result from the quiz data is "+task.getResult().toString());
                    quizTaskCompletionMessage.quizDataLoaded(task.getResult().toObjects(QuizListModel.class));
                }else {
                    Log.d("Main",task.getException().toString());
                    System.out.println("The error is "+task.getException());
                    quizTaskCompletionMessage.onError(task.getException());
                }
            }
        });
    }

}
