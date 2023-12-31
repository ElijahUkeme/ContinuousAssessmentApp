package com.elijahukeme.assessmentapp.views;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.elijahukeme.assessmentapp.R;
import com.elijahukeme.assessmentapp.interfaces.ShowErrorMessage;
import com.elijahukeme.assessmentapp.model.QuizListModel;
import com.elijahukeme.assessmentapp.viewmodels.QuizListViewModel;
import com.squareup.picasso.Picasso;

import java.util.List;


public class DetailsFragment extends Fragment implements ShowErrorMessage {
    private TextView totalQuestion,quizTitle,totalTime;
    private Button startQuizBtn;
    private QuizListViewModel viewModel;
    private NavController navController;
    private ImageView imageView;
    private int position;
    private String quizId,regNumber;
    private int totalQuestionCount=0;

    public DetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        viewModel = new ViewModelProvider(this,ViewModelProvider.AndroidViewModelFactory.getInstance(
                getActivity().getApplication()
        )).get(QuizListViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        totalQuestion = view.findViewById(R.id.tv_total_question);
        totalTime = view.findViewById(R.id.tv_total_time);
        startQuizBtn = view.findViewById(R.id.start_quiz_button);
        quizTitle = view.findViewById(R.id.quiz_title_detail);
        imageView = view.findViewById(R.id.quiz_image_detail);

        navController = Navigation.findNavController(view);
        position = DetailsFragmentArgs.fromBundle(getArguments()).getPosition();
        regNumber = DetailsFragmentArgs.fromBundle(getArguments()).getRegNumber();
        viewModel.getQuizListLiveData().observe(getViewLifecycleOwner(), new Observer<List<QuizListModel>>() {
            @Override
            public void onChanged(List<QuizListModel> quizListModels) {
                QuizListModel quiz = quizListModels.get(position);
                quizTitle.setText(quiz.getTitle());
                quizId = quiz.getQuizId();
                Picasso.get().load(quiz.getImage()).placeholder(R.drawable.profile).into(imageView);
            }
        });

        startQuizBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (quizId ==null){
                        showMessage("Error","Question for this Course not ready please try again",getActivity());

                    }else {
                        DetailsFragmentDirections.ActionDetailsFragmentToQuizFragment action =
                                DetailsFragmentDirections.actionDetailsFragmentToQuizFragment();
                        action.setQuizId(quizId);
                        action.setRegNumber(regNumber);
                        navController.navigate(action);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });

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