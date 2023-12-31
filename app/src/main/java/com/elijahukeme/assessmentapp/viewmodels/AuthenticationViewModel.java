package com.elijahukeme.assessmentapp.viewmodels;

import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.net.Uri;
import android.util.Log;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.elijahukeme.assessmentapp.interfaces.OnStudentCompleteCallBack;
import com.elijahukeme.assessmentapp.model.StudentModel;
import com.elijahukeme.assessmentapp.repositories.AuthenticationRepository;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;

public class AuthenticationViewModel extends AndroidViewModel implements OnStudentCompleteCallBack {

    private MutableLiveData<FirebaseDatabase> mutableLiveData;
    private FirebaseDatabase databaseReference;
    private StorageReference storageReference;

    public MutableLiveData<FirebaseDatabase> getMutableLiveData() {
        return mutableLiveData;
    }

    public FirebaseDatabase getDatabaseReference() {
        return databaseReference;
    }

    public StorageReference getStorageReference() {
        return storageReference;
    }
    private MutableLiveData<StudentModel> studentModelMutableLiveData;
    public MutableLiveData<StudentModel> getStudentModelMutableLiveData(){
        return studentModelMutableLiveData;
    }

    private AuthenticationRepository authenticationRepository;
    public AuthenticationViewModel(@NonNull Application application) {
        super(application);
        authenticationRepository = new AuthenticationRepository(application,this);
        studentModelMutableLiveData = new MutableLiveData<>();
        mutableLiveData = authenticationRepository.getMutableLiveData();
        databaseReference = authenticationRepository.getFirebaseDatabase();
        storageReference = authenticationRepository.getProfileImageStorageRef();
    }
    public void addRegNumber(String regNumber, ProgressBar progressBar, Activity activity){
        authenticationRepository.registerRegistrationNumber(regNumber,progressBar,activity);
    }
    public void handleStudentRegistration(StudentModel studentModel, Uri imageUri, ProgressDialog progressDialog,Activity activity){
        authenticationRepository.processStudentRegistration(studentModel,imageUri,progressDialog,activity);
    }
    public void signInStudent(String regNumber, ProgressDialog progressDialog,Activity activity){
         authenticationRepository.login(regNumber,progressDialog,activity);

    }
    public void updateStudentWithScore(String regNumber,int score){
        authenticationRepository.updateStudentWithTestInfo(regNumber,score);
    }
    public boolean checkForAlreadyTakenTest(String regNumber, Activity activity){
        return authenticationRepository.studentAlreadyTakenTest(regNumber,activity);
    }

    @Override
    public void onLoginComplete(StudentModel studentModel) {
        studentModelMutableLiveData.setValue(studentModel);
    }

    @Override
    public void onError(String errorMessage) {
        Log.d("Main",errorMessage);
    }
}
