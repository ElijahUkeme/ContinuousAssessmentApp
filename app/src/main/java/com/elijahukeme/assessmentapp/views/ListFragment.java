package com.elijahukeme.assessmentapp.views;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.elijahukeme.assessmentapp.R;
import com.elijahukeme.assessmentapp.adapter.QuizListAdapter;
import com.elijahukeme.assessmentapp.interfaces.QuizItemClickListener;
import com.elijahukeme.assessmentapp.model.QuizListModel;
import com.elijahukeme.assessmentapp.viewmodels.QuizListViewModel;

import java.util.List;


public class ListFragment extends Fragment implements QuizItemClickListener {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private NavController navController;
    private QuizListViewModel viewModel;
    private QuizListAdapter adapter;
    private String regNumber;



    public ListFragment() {
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
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        toolbar = view.findViewById(R.id.toolbar_quiz_list);
        toolbar.setTitle("");
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerview_quiz_list);
        progressBar = view.findViewById(R.id.progress_bar_quiz_list);
        //toolbar = view.findViewById(R.id.toolbar_quiz_list);
        navController = Navigation.findNavController(view);
        regNumber = ListFragmentArgs.fromBundle(getArguments()).getRegNumber();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new QuizListAdapter(this);
        recyclerView.setAdapter(adapter);

        viewModel.getQuizListLiveData().observe(getViewLifecycleOwner(), new Observer<List<QuizListModel>>() {
            @Override
            public void onChanged(List<QuizListModel> quizListModels) {
                progressBar.setVisibility(View.GONE);
                adapter.setQuizListModels(quizListModels);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        ListFragmentDirections.ActionListFragmentToDetailsFragment action =
                ListFragmentDirections.actionListFragmentToDetailsFragment();
        action.setPosition(position);
        action.setRegNumber(regNumber);
        navController.navigate(action);
    }
}