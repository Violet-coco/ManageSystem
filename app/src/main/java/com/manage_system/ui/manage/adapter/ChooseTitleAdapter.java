package com.manage_system.ui.manage.adapter;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.manage_system.R;
import com.manage_system.ui.manage.activity.student.StudentChooseDoneTitleMainActivity;
import com.manage_system.ui.manage.activity.student.StudentChooseTitleMainActivity;
import com.manage_system.ui.manage.activity.teacher.TeacherCheckTitleMainActivity;
import com.manage_system.ui.manage.activity.teacher.TeacherOutTitleMainActivity;
import com.manage_system.ui.manage.activity.teacher.TeacherOutTitleUploadActivity;

public class ChooseTitleAdapter extends RecyclerView.Adapter<ChooseTitleAdapter.AuthorViewHolder> {
    private String authority ;

    public ChooseTitleAdapter(String string) {
        authority = string;
        Log.w(TAG,"传过来的"+string);
    }

    @Override
    public AuthorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View childView = inflater.inflate(R.layout.ms_student_choose_title, parent, false);
        if(authority.equals("1")){
            childView = inflater.inflate(R.layout.ms_student_choose_title, parent, false);
        }else if(authority.equals("2")){
            childView = inflater.inflate(R.layout.ms_teacher_out_title, parent, false);
        }

        AuthorViewHolder viewHolder = new AuthorViewHolder(childView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(AuthorViewHolder holder, int position) {

        if(authority.equals("1")){
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
                    Intent intent=new Intent(v.getContext(),StudentChooseDoneTitleMainActivity.class);
                    v.getContext().startActivity(intent);
                }
            });

        }else if(authority.equals("2")){
            holder.student_choose_title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(v.getContext(),TeacherOutTitleMainActivity.class);
                    v.getContext().startActivity(intent);
                }
            });

            holder.student_choose_done_title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(v.getContext(),TeacherOutTitleUploadActivity.class);
                    intent.putExtra("method", "upload");
                    v.getContext().startActivity(intent);
                }
            });

            holder.student_choose_done_check.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(v.getContext(),TeacherCheckTitleMainActivity.class);
                    v.getContext().startActivity(intent);
                }
            });
        }

    }


    @Override
    public int getItemCount() {
        return 1;
    }

    private static String TAG = "CHOOSE";
    class AuthorViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout student_choose_title;
        private LinearLayout student_choose_done_title;
        private LinearLayout student_choose_done_check;
        public AuthorViewHolder(View itemView) {
            super(itemView);
            student_choose_title = (LinearLayout) itemView.findViewById(R.id.student_choose_title);
            student_choose_done_title = (LinearLayout) itemView.findViewById(R.id.student_choose_done_title);
            student_choose_done_check = (LinearLayout) itemView.findViewById(R.id.student_choose_done_check);
        }
    }
}
