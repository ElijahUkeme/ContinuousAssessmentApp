package com.elijahukeme.assessmentapp.views;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.elijahukeme.assessmentapp.R;
import com.elijahukeme.assessmentapp.viewmodels.AuthenticationViewModel;
import com.google.firebase.database.FirebaseDatabase;

public class AddRegNumberFragment extends Fragment {

    private ProgressBar progressBar;
    private EditText editText;
    private Button button;
    private AuthenticationViewModel authenticationViewModel;


    public AddRegNumberFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        authenticationViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory
                .getInstance(getActivity().getApplication())).get(AuthenticationViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_reg_number, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        button = view.findViewById(R.id.add_regNumber_button);
        editText = view.findViewById(R.id.admin_add_regNumber);
        progressBar = view.findViewById(R.id.progressBar_admin_add_regNumber);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addRegNumber();
            }
        });
    }

    private void addRegNumber(){
        String regNumber = editText.getText().toString();
        authenticationViewModel.addRegNumber(regNumber,progressBar,getActivity());
        authenticationViewModel.getMutableLiveData().observe(getViewLifecycleOwner(), new Observer<FirebaseDatabase>() {
            @Override
            public void onChanged(FirebaseDatabase firebaseDatabase) {
                firebaseDatabase.getReference().child("Registration Number")
                        .child(regNumber);
            }
        });
    }
}