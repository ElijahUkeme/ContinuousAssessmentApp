package com.elijahukeme.assessmentapp.repositories;

import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;

import com.elijahukeme.assessmentapp.interfaces.ImageUploadCallBack;
import com.elijahukeme.assessmentapp.interfaces.OnStudentCompleteCallBack;
import com.elijahukeme.assessmentapp.interfaces.ShowErrorMessage;
import com.elijahukeme.assessmentapp.model.RegistrationNumberModel;
import com.elijahukeme.assessmentapp.model.StudentModel;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.util.HashMap;

public class AuthenticationRepository implements ShowErrorMessage{
    private Application application;
    private MutableLiveData<FirebaseDatabase> mutableLiveData;
    private FirebaseDatabase firebaseDatabase;
    private StorageReference profileImageStorageRef;
    private String myUri ;

    public MutableLiveData<FirebaseDatabase> getMutableLiveData() {
        return mutableLiveData;
    }

    public FirebaseDatabase getFirebaseDatabase() {
        return firebaseDatabase;
    }

    public StorageReference getProfileImageStorageRef() {
        return profileImageStorageRef;
    }
    private RegistrationNumberModel registrationNumberModel;
    boolean hasTaken = false;
    StudentModel studentModelReturned;
    private OnStudentCompleteCallBack onStudentCompleteCallBack;
    public StudentModel getStudentModelReturned(){
        return studentModelReturned;
    }

    public AuthenticationRepository(Application application,OnStudentCompleteCallBack onStudentCompleteCallBack){
        this.application = application;
        this.onStudentCompleteCallBack = onStudentCompleteCallBack;
        mutableLiveData = new MutableLiveData<>();
        firebaseDatabase = FirebaseDatabase.getInstance();
        profileImageStorageRef = FirebaseStorage.getInstance().getReference("Profile Images");
        myUri = "";
    }

    public void registerRegistrationNumber(String regNumber, ProgressBar progressBar,Activity activity){
        progressBar.setVisibility(View.VISIBLE);
        firebaseDatabase.getReference().child("Registration Number")
                .child(regNumber).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            progressBar.setVisibility(View.GONE);
                            showMessage("Error","You have already added this Registration Number",activity);
                        }else {
                            HashMap<String,Object> hashMap = new HashMap<>();
                            hashMap.put("regNumber",regNumber);
                            firebaseDatabase.getReference().child("Registration Number")
                                    .child(regNumber).updateChildren(hashMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                progressBar.setVisibility(View.GONE);
                                                Toast.makeText(application, "Registration Number Added Successfully",Toast.LENGTH_SHORT).show();
                                            }else{
                                                progressBar.setVisibility(View.GONE);
                                                Toast.makeText(application, "Error Occurred", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(application, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    public void processStudentRegistration(StudentModel studentModel,Uri imageUri, ProgressDialog progressDialog,Activity activity){
        progressDialog.setTitle("Registration Processing....");
        progressDialog.setMessage("Please wait while we are checking your credentials");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        firebaseDatabase.getReference().child("Registration Number")
                .child(studentModel.getRegNumber())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            firebaseDatabase.getReference().child("Students").child(studentModel.getRegNumber())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()){
                                                progressDialog.dismiss();
                                                showMessage("Error","Student with this Registration Number has Already Registered",activity);
                                            }else {
                                                uploadProfileImage(studentModel.getRegNumber(), imageUri, progressDialog, new ImageUploadCallBack() {
                                                    @Override
                                                    public void onImageUploadSuccess(String imageUrl) {
                                                        studentModel.setProfileImage(imageUrl);
                                                        registerStudent(studentModel,progressDialog);
                                                    }

                                                    @Override
                                                    public void onImageUploadFailure(String errorMessage) {
                                                        progressDialog.dismiss();
                                                        showMessage("Error",errorMessage,activity);
                                                    }
                                                });
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                        }else {
                            progressDialog.dismiss();
                            showMessage("Error","You are not qualified to Register for this course",activity);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(application, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });

    }
    private void uploadProfileImage(String regNumber, Uri imageUri, ProgressDialog progressDialog, ImageUploadCallBack callBack){
        if (imageUri !=null){
            final StorageReference fileRef = profileImageStorageRef
                    .child(regNumber+".jpg");
            StorageTask uploadTask = fileRef.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()){
                        progressDialog.dismiss();
                        throw task.getException();
                    }
                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        if (downloadUri !=null){
                            myUri = downloadUri.toString();
                            callBack.onImageUploadSuccess(myUri);
                        }else {
                            callBack.onImageUploadFailure("Fail to Get Image Url");
                        }
                    }else {
                        callBack.onImageUploadFailure("Image Upload Fail");
                    }
                }
            });
        }
    }
    private void registerStudent(StudentModel studentModel,ProgressDialog progressDialog){
        HashMap hashMap = new HashMap<>();
        hashMap.put("name",studentModel.getName());
        hashMap.put("regNumber",studentModel.getRegNumber());
        hashMap.put("gender",studentModel.getGender());
        hashMap.put("testScore",studentModel.getTestScore());
        hashMap.put("profileImage",myUri);
        hashMap.put("testStatus",studentModel.getTestStatus());

        firebaseDatabase.getReference().child("Students")
                .child(studentModel.getRegNumber())
                .updateChildren(hashMap)
                .addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()){
                            progressDialog.dismiss();
                            Toast.makeText(application, "Registration Successful", Toast.LENGTH_SHORT).show();
                        }else {
                            progressDialog.dismiss();
                            Toast.makeText(application, "Error Occurred, Please Try Again", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    public void login(String regNumber, ProgressDialog progressDialog, Activity activity){
        progressDialog.setTitle("Login Processing....");
        progressDialog.setMessage("Please wait while we verify your Registration Number");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        firebaseDatabase.getReference().child("Students")
                .child(regNumber)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            progressDialog.dismiss();
                            System.out.println("Student Exist");
                            StudentModel snapshotValue = snapshot.getValue(StudentModel.class);
                            onStudentCompleteCallBack.onLoginComplete(snapshotValue);

                        }else {
                            progressDialog.dismiss();
                            onStudentCompleteCallBack.onError("Error Occurred");
                            showMessage("Error","Incorrect Registration Number",activity);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressDialog.dismiss();
                    }
                });
    }

    public void updateStudentWithTestInfo(String regNumber,int testScore){
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("testStatus","Taken");
        hashMap.put("testScore",testScore);
        firebaseDatabase.getReference().child("Students")
                .child(regNumber)
                .updateChildren(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(application.getApplicationContext(),"Your Test Score has been updated Successfully",Toast.LENGTH_LONG).show();
                        }else {
                            Toast.makeText(application.getApplicationContext(),"Error occurred updating your test score",Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
    public boolean studentAlreadyTakenTest(String regNumber,Activity activity){

        firebaseDatabase.getReference().child("Students")
                .child(regNumber)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            StudentModel studentModel1 = snapshot.getValue(StudentModel.class);
                            if (studentModel1.getTestStatus().equalsIgnoreCase("Taken")){
                                hasTaken = true;
                                showMessage("Error","You have already taken the test",activity);
                            }else {
                                hasTaken = false;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        Log.d("Main","The value of has taken is "+hasTaken);
        return hasTaken;

    }


    @Override
    public void showMessage(String title, String message,Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }
}
