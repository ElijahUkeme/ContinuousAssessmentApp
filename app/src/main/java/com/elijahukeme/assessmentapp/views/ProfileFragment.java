package com.elijahukeme.assessmentapp.views;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.elijahukeme.assessmentapp.R;
import com.elijahukeme.assessmentapp.interfaces.ShowErrorMessage;
import com.elijahukeme.assessmentapp.viewmodels.AuthenticationViewModel;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment implements ShowErrorMessage {
    private String image,name,testStatus,gender,regNumber;
    private int score;
    private TextView tvName,tvStatus,tvGender,tvScore,tvRegNumber;
    private CircleImageView imageView;
    private NavController navController;
    private AuthenticationViewModel viewModel;
    private Toolbar toolbar;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        viewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory
                .getInstance(getActivity().getApplication())).get(AuthenticationViewModel.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);
        toolbar =  view.findViewById(R.id.toolBar_profile);
        toolbar.setTitle("");
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        MenuHost menuHost = requireActivity();
        menuHost.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.student_menu,menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId()==R.id.test_menu_item){
                    if (!checkForAlreadyTakenTest()) {
                        proceedToTakeTheTest();
                    }
                    return true;
                }else if (menuItem.getItemId()==R.id.about){
                    aboutUsDialog();
                    return true;
                }else {
                    return false;
                }

            }
        },getViewLifecycleOwner(), Lifecycle.State.RESUMED);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvStatus = view.findViewById(R.id.test_status_profile);
        tvGender = view.findViewById(R.id.gender_profile);
        imageView = view.findViewById(R.id.image_profile);
        tvName = view.findViewById(R.id.name_profile);
        tvScore = view.findViewById(R.id.test_score_profile);
        tvRegNumber = view.findViewById(R.id.registration_number_profile);
        navController = Navigation.findNavController(view);
        score = ProfileFragmentArgs.fromBundle(getArguments()).getScore();
        name = ProfileFragmentArgs.fromBundle(getArguments()).getName();
        testStatus = ProfileFragmentArgs.fromBundle(getArguments()).getStatus();
        gender = ProfileFragmentArgs.fromBundle(getArguments()).getGender();
        image = ProfileFragmentArgs.fromBundle(getArguments()).getImage();
        regNumber = ProfileFragmentArgs.fromBundle(getArguments()).getRegNumber();
        displayUserDetails();
    }

    private void displayUserDetails(){
        tvScore.setText(String.valueOf(score));
        tvStatus.setText(testStatus);
        tvName.setText(name);
        tvGender.setText(gender);
        tvRegNumber.setText(regNumber);
        Picasso.get().load(image).into(imageView);
    }

    private void proceedToTakeTheTest(){
        ProfileFragmentDirections.ActionProfileFragmentToListFragment action =
                ProfileFragmentDirections.actionProfileFragmentToListFragment();
        action.setRegNumber(regNumber);
        navController.navigate(action);
    }

    public void aboutUsDialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle("CONTINUOUS ASSESSMENT APP");
        alertDialogBuilder.setMessage("This App was developed for the Student of Computer Science, Glory Land Polytechnic Ankpa for their continuous assessment work.\nDeveloper and Designer: Elijah Ukeme\nAddress: Glory Land Polytechnic Ankpa, Kogi State, Nigeria\nE-mail:ukemedmet@gmail.com");
        alertDialogBuilder.setPositiveButton("CONTACT US", (dialogInterface, i) -> {
        });
        alertDialogBuilder.setNegativeButton("CLOSE", (dialogInterface, i) -> {

        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private boolean checkForAlreadyTakenTest(){
        if (testStatus.equalsIgnoreCase("Taken")) {
            showMessage("Error","You have Already Taken The Test",getActivity());
            return true;
        }else {
            return false;
        }
    }

    @Override
    public void showMessage(String title, String message, Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }
}