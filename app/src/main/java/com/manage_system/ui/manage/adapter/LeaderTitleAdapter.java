package com.manage_system.ui.manage.adapter;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.manage_system.R;
import com.manage_system.ui.manage.activity.manager.ManagerCtReportMainActivity;
import com.manage_system.ui.manage.activity.manager.ManagerTotalReportMainActivity;
import com.manage_system.ui.manage.activity.manager.ManagerXtReportMainActivity;
import com.manage_system.ui.manage.activity.student.StudentChooseDoneTitleMainActivity;
import com.manage_system.ui.manage.activity.student.StudentChooseTitleMainActivity;
import com.manage_system.ui.manage.activity.teacher.TeacherCheckTitleMainActivity;
import com.manage_system.ui.manage.activity.teacher.TeacherOutTitleMainActivity;
import com.manage_system.ui.manage.activity.teacher.TeacherOutTitleUploadActivity;

public class LeaderTitleAdapter extends RecyclerView.Adapter<LeaderTitleAdapter.AuthorViewHolder> {

    @Override
    public AuthorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View childView = inflater.inflate(R.layout.ms_manager_title, parent, false);

        AuthorViewHolder viewHolder = new AuthorViewHolder(childView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(AuthorViewHolder holder, int position) {

        holder.m_ct_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(v.getContext(),TeacherOutTitleMainActivity.class);
                v.getContext().startActivity(intent);
            }
        });

        holder.m_xt_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(v.getContext(),TeacherOutTitleUploadActivity.class);
                intent.putExtra("method", "upload");
                v.getContext().startActivity(intent);
            }
        });

        // 出题情况统计报表
        holder.m_ct_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(v.getContext(),ManagerCtReportMainActivity.class);
                v.getContext().startActivity(intent);
            }
        });

        // 选题情况统计报表
        holder.m_xt_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(v.getContext(),ManagerXtReportMainActivity.class);
                v.getContext().startActivity(intent);
            }
        });

        // 题目情况统计表
        holder.m_tm_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(v.getContext(),ManagerTotalReportMainActivity.class);
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
        private LinearLayout m_ct_check;
        private LinearLayout m_xt_check;
        private LinearLayout m_ct_report;
        private LinearLayout m_xt_report;
        private LinearLayout m_tm_report;
        public AuthorViewHolder(View itemView) {
            super(itemView);
            m_ct_check = (LinearLayout) itemView.findViewById(R.id.m_ct_check);
            m_xt_check = (LinearLayout) itemView.findViewById(R.id.m_xt_check);
            m_ct_report = (LinearLayout) itemView.findViewById(R.id.m_ct_report);
            m_xt_report = (LinearLayout) itemView.findViewById(R.id.m_xt_report);
            m_tm_report = (LinearLayout) itemView.findViewById(R.id.m_tm_report);
        }
    }
}
