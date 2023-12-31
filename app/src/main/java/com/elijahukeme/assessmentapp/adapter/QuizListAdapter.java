package com.elijahukeme.assessmentapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.elijahukeme.assessmentapp.R;
import com.elijahukeme.assessmentapp.interfaces.QuizItemClickListener;
import com.elijahukeme.assessmentapp.model.QuizListModel;
import com.squareup.picasso.Picasso;

import java.util.List;

public class QuizListAdapter extends RecyclerView.Adapter<QuizListAdapter.QuizListViewHolder> {
    private List<QuizListModel> quizListModels;
    private QuizItemClickListener quizItemClickListener;

    public void setQuizListModels(List<QuizListModel> quizListModels) {
        this.quizListModels = quizListModels;
    }
    public QuizListAdapter(QuizItemClickListener quizItemClickListener){
        this.quizItemClickListener = quizItemClickListener;
    }

    @NonNull
    @Override
    public QuizListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.quiz_item,parent,false);
        return new QuizListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizListViewHolder holder, int position) {
        QuizListModel model = quizListModels.get(position);
        holder.quizTitle.setText(model.getTitle());
        Picasso.get().load(model.getImage()).into(holder.quizImage);

    }

    @Override
    public int getItemCount() {
        if (quizListModels ==null){
            System.out.println("The quiz item is null");
            return 0;
        }

        return quizListModels.size();
    }

    public class QuizListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView quizImage;
        private TextView quizTitle;
        private RelativeLayout relativeLayout;

        public QuizListViewHolder(@NonNull View itemView) {
            super(itemView);
            quizImage = itemView.findViewById(R.id.quizListImage);
            quizTitle = itemView.findViewById(R.id.quizTitleList);
            relativeLayout = itemView.findViewById(R.id.relative_layout);
            relativeLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            quizItemClickListener.onItemClick(getAdapterPosition());
        }
    }
}
