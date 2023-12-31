package com.elijahukeme.assessmentapp.views;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.elijahukeme.assessmentapp.R;
import com.elijahukeme.assessmentapp.model.StudentModel;
import com.elijahukeme.assessmentapp.viewmodels.AuthenticationViewModel;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class SignInFragment extends Fragment {

    private AuthenticationViewModel authenticationViewModel;
    private NavController navController;
    private TextView adminLink, registrationLink;
    private Button signInBtn;
    private EditText editTextRegNumber;
    private ProgressDialog progressDialog;
    private boolean cancel = false;
    private int count = 0;
    private String regNumber;

    public SignInFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        progressDialog = new ProgressDialog(getContext());
        authenticationViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory
                .getInstance(getActivity().getApplication())).get(AuthenticationViewModel.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_in, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        adminLink = view.findViewById(R.id.admin_home_link);
        registrationLink = view.findViewById(R.id.to_register_page);
        editTextRegNumber = view.findViewById(R.id.login_regNumber);
        signInBtn = view.findViewById(R.id.login_button);

        registrationLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toRegistrationPage();
            }
        });
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateInput();
            }
        });
        adminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count +=1;
                if (count >=5){
                    toAdminAddRegNumberPage();
                }
            }
        });
    }

    private void validateInput() {
        regNumber = editTextRegNumber.getText().toString();
        if (regNumber.isEmpty()) {
            editTextRegNumber.setError("Please Enter your Registration Number");
            cancel = true;
            editTextRegNumber.requestFocus();
        } else if (regNumber.contains("/")) {
            editTextRegNumber.setError("Reg Number must not contain slash, use underscore");
            cancel = true;
            editTextRegNumber.requestFocus();
        } else {
            try {

              authenticationViewModel.signInStudent(regNumber,progressDialog,getActivity());
              authenticationViewModel.getStudentModelMutableLiveData().observe(getViewLifecycleOwner(), new Observer<StudentModel>() {
                  @Override
                  public void onChanged(StudentModel studentModel) {

                      SignInFragmentDirections.ActionSignInFragmentToProfileFragment action =
                              SignInFragmentDirections.actionSignInFragmentToProfileFragment();
                      action.setGender(studentModel.getGender());
                      action.setImage(studentModel.getProfileImage());
                      action.setName(studentModel.getName());
                      action.setScore(studentModel.getTestScore());
                      action.setStatus(studentModel.getTestStatus());
                      action.setRegNumber(studentModel.getRegNumber());
                      navController.navigate(action);
                  }
              });

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    private void toRegistrationPage(){
        navController.navigate(R.id.action_signInFragment_to_signUpFragment);
    }
    private void toAdminAddRegNumberPage(){
        navController.navigate(R.id.action_signInFragment_to_addRegNumberFragment);
    }
}