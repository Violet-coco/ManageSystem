package com.manage_system.ui.manage.adapter;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.manage_system.R;
import com.manage_system.ui.manage.activity.StudentChooseDoneTitleActivity;
import com.manage_system.ui.manage.activity.StudentChooseTitleActivity;
import com.manage_system.ui.manage.activity.StudentChooseTitleMainActivity;

public class ChooseTitleAdapter extends RecyclerView.Adapter<ChooseTitleAdapter.AuthorViewHolder> {

    @Override
    public AuthorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View childView = inflater.inflate(R.layout.ms_student_choose_title, parent, false);
        AuthorViewHolder viewHolder = new AuthorViewHolder(childView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(AuthorViewHolder holder, int position) {

        holder.student_choose_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(v.getContext(),StudentChooseTitleMainActivity.class);
                v.getContext().startActivity(intent);
            }
        });

        holder.student_choose_done_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(v.getContext(),StudentChooseDoneTitleActivity.class);
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    private static String TAG = "CHOOSE";
    class AuthorViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout student_choose_title;
        private LinearLayout student_choose_done_title;
        public AuthorViewHolder(View itemView) {
            super(itemView);
            student_choose_title = (LinearLayout) itemView.findViewById(R.id.student_choose_title);
            student_choose_done_title = (LinearLayout) itemView.findViewById(R.id.student_choose_done_title);
        }
    }
}
