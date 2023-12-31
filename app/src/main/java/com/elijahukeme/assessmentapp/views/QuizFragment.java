package com.elijahukeme.assessmentapp.views;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.elijahukeme.assessmentapp.R;
import com.elijahukeme.assessmentapp.adapter.QuizListAdapter;
import com.elijahukeme.assessmentapp.interfaces.QuizItemClickListener;
import com.elijahukeme.assessmentapp.model.QuestionModel;
import com.elijahukeme.assessmentapp.model.QuizListModel;
import com.elijahukeme.assessmentapp.viewmodels.AuthenticationViewModel;
import com.elijahukeme.assessmentapp.viewmodels.QuestionViewModel;
import com.elijahukeme.assessmentapp.viewmodels.QuizListViewModel;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class QuizFragment extends Fragment{

    Button buttonNext;
    TextView tv, timing;
    RadioButton rb1, rb2, rb3, rb4;
    RadioGroup rg;
    int count = 1;
    int correctAnswer = 0;
    String rightAnswer=null ;
    String selectedOption = null;
    private QuestionViewModel viewModel;
    NavController navController;
    private String quizId,regNumber;
    public boolean sureOfAnswer=false;
    private int generatedIndex;
    List<Integer> list = new ArrayList<>();
    private AuthenticationViewModel authenticationViewModel;


    public QuizFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        viewModel = new ViewModelProvider(this,ViewModelProvider.AndroidViewModelFactory.getInstance(
                getActivity().getApplication()
        )).get(QuestionViewModel.class);
        authenticationViewModel = new ViewModelProvider(this,ViewModelProvider.AndroidViewModelFactory.getInstance(
                getActivity().getApplication()
        )).get(AuthenticationViewModel.class);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_quiz, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        buttonNext = view.findViewById(R.id.button_next);
        regNumber = QuizFragmentArgs.fromBundle(getArguments()).getRegNumber();
        tv = view.findViewById(R.id.textview_question);
        timing = view.findViewById(R.id.textview_time);
        rb1 = view.findViewById(R.id.radioButton);
        rb2 = view.findViewById(R.id.radioButton2);
        rb3 = view.findViewById(R.id.radioButton3);
        rb4 = view.findViewById(R.id.radioButton4);
        rg = view.findViewById(R.id.radioGroup);
        navController = Navigation.findNavController(view);
        quizId = QuizFragmentArgs.fromBundle(getArguments()).getQuizId();
        viewModel.setQuizId(quizId);
        try {
                loadQuestions(0);
        }catch (Exception e){
            e.printStackTrace();
        }

        timeCount();

        for (int i=1;i<=15;i++){
            list.add(i);
        }
        //loadData();

        rb1.setOnClickListener(first_radio_listener);
        rb2.setOnClickListener(first_radio_listener);
        rb3.setOnClickListener(first_radio_listener);
        rb4.setOnClickListener(first_radio_listener);

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                        checkAnswer();
                        sureOfAnswer = false;
                        selectedOption = null;
                }catch (Exception e){
                    e.printStackTrace();
                };
            }
        });
    }

    private void checkForCompletion(){
        if (count==10){
            buttonNext.setText("Submit");
            submitTest();
        }else {
            loadDataAgain();
        }
    }
    private void loadDataAgain(){
        sureOfAnswer = false;
        selectedOption = "";
        rg.clearCheck();
        count +=1;
        Collections.shuffle(list);
        generatedIndex =list.get(0);
        loadQuestions(generatedIndex);
        list.remove(list.get(0));
    }

    private void loadQuestions(int index){

        viewModel.getMutableLiveData().observe(getViewLifecycleOwner(), new Observer<List<QuestionModel>>() {
            @Override
            public void onChanged(List<QuestionModel> questionModels) {
                rightAnswer = questionModels.get(index).getAnswer();
                tv.setText(count+"."+questionModels.get(index).getQuestion());
                rb1.setText(questionModels.get(index).getOption1());
                rb2.setText(questionModels.get(index).getOption2());
                rb3.setText(questionModels.get(index).getOption3());
                rb4.setText(questionModels.get(index).getOption4());

            }
        });
    }
    private void checkAnswer(){
        try {
            if (selectedOption.equalsIgnoreCase(rightAnswer)){
                correctAnswer +=2;
            }else {
                correctAnswer +=0;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        checkForCompletion();
    }
    private void submitTest(){
        authenticationViewModel.updateStudentWithScore(regNumber,correctAnswer);

                QuizFragmentDirections.ActionQuizFragmentToResultFragment action =
                        QuizFragmentDirections.actionQuizFragmentToResultFragment();
                action.setScore(correctAnswer);
                navController.navigate(action);
            }
    private void timeCount(){

        new CountDownTimer(600000, 1000) {
            public void onTick(long millisUntilFinished) {
                long hour = (millisUntilFinished / 3600000) % 24;
                long min = (millisUntilFinished / 60000) % 60;
                long sec = (millisUntilFinished / 1000) % 60;
                String str = String.format("%d:%02d", min, sec);
                timing.setText("Your time remains: "+str);
            }
            // When the task is over it will print 00:00:00 there
            public void onFinish() {
                timing.setText("00:00:00");
                submitTest();
            }
        }.start();
    }

    View.OnClickListener first_radio_listener = new View.OnClickListener(){
        public void onClick(View v) {
            int id = v.getId();
            if (id==R.id.radioButton){
                sureOfAnswer = true;
                selectedOption = rb1.getText().toString();

            }else if (id==R.id.radioButton2){
                sureOfAnswer = true;
                selectedOption = rb2.getText().toString();

            }else if (id==R.id.radioButton3){
                sureOfAnswer = true;
                selectedOption = rb3.getText().toString();

            }else if (id==R.id.radioButton4){
                sureOfAnswer = true;
                selectedOption = rb4.getText().toString();

            }else {
                sureOfAnswer = false;
                selectedOption = "Nothing";
            }
        }
    };
}