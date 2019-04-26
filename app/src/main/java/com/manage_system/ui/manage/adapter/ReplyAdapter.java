package com.manage_system.ui.manage.adapter;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.manage_system.R;
import com.manage_system.ui.manage.activity.StudentReplyGradeActivity;
import com.manage_system.ui.manage.activity.StudentReplyPlanActivity;
import com.manage_system.ui.manage.activity.StudentReplyRecordActivity;

public class ReplyAdapter extends RecyclerView.Adapter<ReplyAdapter.AuthorViewHolder> {

    @Override
    public AuthorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View childView = inflater.inflate(R.layout.ms_student_reply, parent, false);
        AuthorViewHolder viewHolder = new AuthorViewHolder(childView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(AuthorViewHolder holder, int position) {
        holder.student_find_reply_plan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(v.getContext(),StudentReplyPlanActivity.class);
                v.getContext().startActivity(intent);
            }
        });

        holder.student_find_reply_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(v.getContext(),StudentReplyRecordActivity.class);
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
    }

    @Override
    public int getItemCount() {
        return 1;
    }


    class AuthorViewHolder extends RecyclerView.ViewHolder {

        LinearLayout student_find_reply_plan;
        LinearLayout student_find_reply_record;
        LinearLayout student_find_reply_grade;

        public AuthorViewHolder(View itemView) {
            super(itemView);

            student_find_reply_plan = (LinearLayout) itemView.findViewById(R.id.student_find_reply_plan);
            student_find_reply_record = (LinearLayout) itemView.findViewById(R.id.student_find_reply_record);
            student_find_reply_grade = (LinearLayout) itemView.findViewById(R.id.student_find_reply_grade);

        }
    }
}
