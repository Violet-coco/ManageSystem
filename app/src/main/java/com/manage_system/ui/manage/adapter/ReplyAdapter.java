package com.manage_system.ui.manage.adapter;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.manage_system.R;
import com.manage_system.ui.manage.activity.student.StudentReplyGradeActivity;
import com.manage_system.ui.manage.activity.student.StudentReplyPlanActivity;
import com.manage_system.ui.manage.activity.teacher.TeacherCheckDataMainActivity;
import com.manage_system.ui.manage.activity.teacher.TeacherGradeMainActivity;
import com.manage_system.ui.manage.activity.teacher.TeacherGtCommentMainActivity;
import com.manage_system.ui.manage.activity.teacher.TeacherReplyPlanMainActivity;

import static android.content.ContentValues.TAG;

public class ReplyAdapter extends RecyclerView.Adapter<ReplyAdapter.AuthorViewHolder> {
    private String authority;

    public ReplyAdapter(String string) {
        authority = string;
        Log.w(TAG,"传过来的"+string);
    }

    @Override
    public AuthorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View childView = inflater.inflate(R.layout.ms_student_reply, parent, false);
        if(authority.equals("1")){
            childView = inflater.inflate(R.layout.ms_student_reply, parent, false);
        }else if(authority.equals("2")){
            childView = inflater.inflate(R.layout.ms_teacher_manager_student, parent, false);
        }
        AuthorViewHolder viewHolder = new AuthorViewHolder(childView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(AuthorViewHolder holder, int position) {
        if(authority.equals("1")){
            holder.student_find_reply_plan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(v.getContext(),StudentReplyPlanActivity.class);
                    v.getContext().startActivity(intent);
                }
            });

            holder.student_find_reply_grade.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(v.getContext(),StudentReplyGradeActivity.class);
                    v.getContext().startActivity(intent);
                }
            });
        }else if(authority.equals("2")){
            holder.sm_reply_plan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(v.getContext(),TeacherReplyPlanMainActivity.class);
                    v.getContext().startActivity(intent);
                }
            });

            holder.sm_check_data.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(v.getContext(),TeacherCheckDataMainActivity.class);
                    v.getContext().startActivity(intent);
                }
            });
            holder.sm_gt_comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(v.getContext(),TeacherGtCommentMainActivity.class);
                    v.getContext().startActivity(intent);
                }
            });

            holder.sm_grade_show.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(v.getContext(),TeacherGradeMainActivity.class);
                    v.getContext().startActivity(intent);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return 1;
    }


    class AuthorViewHolder extends RecyclerView.ViewHolder {

        LinearLayout student_find_reply_plan;
        LinearLayout student_find_reply_grade;
        LinearLayout sm_reply_plan;
        LinearLayout sm_check_data;
        LinearLayout sm_gt_comment;
        LinearLayout sm_grade_show;

        public AuthorViewHolder(View itemView) {
            super(itemView);

            if(authority.equals("1")){
                student_find_reply_plan = (LinearLayout) itemView.findViewById(R.id.student_find_reply_plan);
                student_find_reply_grade = (LinearLayout) itemView.findViewById(R.id.student_find_reply_grade);
            }else if(authority.equals("2")){
                sm_reply_plan = (LinearLayout) itemView.findViewById(R.id.sm_reply_plan);
                sm_check_data = (LinearLayout) itemView.findViewById(R.id.sm_check_data);
                sm_gt_comment = (LinearLayout) itemView.findViewById(R.id.sm_gt_comment);
                sm_grade_show = (LinearLayout) itemView.findViewById(R.id.sm_grade_show);
            }
        }
    }
}
