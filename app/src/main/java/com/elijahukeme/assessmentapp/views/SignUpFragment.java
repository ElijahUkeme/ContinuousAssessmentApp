package com.elijahukeme.assessmentapp.views;

import static android.app.Activity.RESULT_OK;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.elijahukeme.assessmentapp.R;
import com.elijahukeme.assessmentapp.model.StudentModel;
import com.elijahukeme.assessmentapp.viewmodels.AuthenticationViewModel;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;



public class SignUpFragment extends Fragment {
    private AuthenticationViewModel authenticationViewModel;
    private NavController navController;
    private TextView pickImageTv;
    private CircleImageView profileImage;
    private EditText name, regNumber;
    private RadioGroup radioGroup;
    private RadioButton male, female;
    private Button registerBtn;
    private boolean cancel = false;
    private ProgressDialog loadingDialog;
    private String selectedGender = "";
    private static final int PIC_IMAGE_REQUEST=1;
    private Uri imageUri;
    private String studentName,studentRegNumber;

    public SignUpFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        authenticationViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory
                .getInstance(getActivity().getApplication())).get(AuthenticationViewModel.class);
        loadingDialog = new ProgressDialog(getContext());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_up, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        pickImageTv = view.findViewById(R.id.textview_profile_image_picker);
        profileImage = view.findViewById(R.id.profile_image);
        name = view.findViewById(R.id.editText_name);
        regNumber = view.findViewById(R.id.editText_regNumber);
        registerBtn = view.findViewById(R.id.register_button);
        radioGroup = view.findViewById(R.id.gender);
        male = view.findViewById(R.id.radio_male);
        female = view.findViewById(R.id.radio_female);

        pickImageTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickProfileImage();
            }
        });
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateInput();
            }
        });
    }

    private void validateInput() {
        boolean genderChecked = false;
         studentName = name.getText().toString();
         studentRegNumber = regNumber.getText().toString();
        if (female.isChecked()) {
            genderChecked = true;
            selectedGender = female.getText().toString();
        } else if (male.isChecked()) {
            genderChecked = true;
            selectedGender = male.getText().toString();
        } else {
            genderChecked = false;
        }
        if (studentName.isEmpty()) {
            name.setError("Please Enter your name");
            cancel = true;
            name.requestFocus();
        } else if (studentRegNumber.isEmpty()) {
            regNumber.setError("Please Enter your Registration Number");
            cancel = true;
            regNumber.requestFocus();
        } else if (studentRegNumber.contains("/")) {
            regNumber.setError("Reg Number must not contains slash, use underscore");
            cancel = true;
            regNumber.requestFocus();
        } else if (imageUri == null) {
            Toast.makeText(getActivity(), "Please Pick a Profile Image", Toast.LENGTH_SHORT).show();
        } else if (!genderChecked) {
            Toast.makeText(getActivity(), "Please select your gender", Toast.LENGTH_SHORT).show();
        } else {
            collateRegistrationData();
        }
    }
    private void pickProfileImage(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent,PIC_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode ==PIC_IMAGE_REQUEST && data !=null){
            imageUri = data.getData();
            profileImage.setImageURI(imageUri);
        }
    }

    private void collateRegistrationData(){
        StudentModel studentModel = new StudentModel();
        studentModel.setGender(selectedGender);
        studentModel.setName(studentName);
        studentModel.setRegNumber(studentRegNumber);
        studentModel.setTestScore(0);
        studentModel.setTestStatus("Not Taken");
        authenticationViewModel.handleStudentRegistration(studentModel,imageUri,loadingDialog,getActivity());
        authenticationViewModel.getMutableLiveData().observe(getViewLifecycleOwner(), new Observer<FirebaseDatabase>() {
            @Override
            public void onChanged(FirebaseDatabase firebaseDatabase) {
                firebaseDatabase.getReference().child("Students").child(studentRegNumber);
                navController.navigate(R.id.action_signUpFragment_to_signInFragment);
            }
        });
    }
}
