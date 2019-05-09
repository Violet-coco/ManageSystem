package com.manage_system.ui.manage.adapter;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.manage_system.R;
import com.manage_system.ui.manage.activity.student.StudentForeignTranslationActivity;
import com.manage_system.ui.manage.activity.student.StudentGraduationThesisMainActivity;
import com.manage_system.ui.manage.activity.student.StudentGuideReportMainActivity;
import com.manage_system.ui.manage.activity.student.StudentLiteratureReviewActivity;
import com.manage_system.ui.manage.activity.student.StudentMiddleCheckActivity;
import com.manage_system.ui.manage.activity.student.StudentOpenReportActivity;
import com.manage_system.utils.OkManager;

public class ProcessDocumentAdapter extends RecyclerView.Adapter<ProcessDocumentAdapter.AuthorViewHolder> {

    private OkManager manager;
    //登录验证请求
    private String path="http://www.yuanbw.cn:20086/gpms/stu/showOpeningReport";
    private String TAG = "开题报告";

    @Override
    public AuthorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View childView = inflater.inflate(R.layout.ms_student_pd, parent, false);
        AuthorViewHolder viewHolder = new AuthorViewHolder(childView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(AuthorViewHolder holder, int position) {
        holder.student_submit_open_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(v.getContext(),StudentOpenReportActivity.class);
                v.getContext().startActivity(intent);
            }
        });

        holder.student_submit_middle_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(v.getContext(),StudentMiddleCheckActivity.class);
                v.getContext().startActivity(intent);
            }
        });
        holder.student_submit_guide_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(v.getContext(),StudentGuideReportMainActivity.class);
                v.getContext().startActivity(intent);
            }
        });

        holder.student_submit_foreign_translation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(v.getContext(),StudentForeignTranslationActivity.class);
                v.getContext().startActivity(intent);
            }
        });
        holder.student_submit_literature_review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(v.getContext(),StudentLiteratureReviewActivity.class);
                v.getContext().startActivity(intent);
            }
        });

        holder.student_submit_graduation_thesis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(v.getContext(),StudentGraduationThesisMainActivity.class);
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return 1;
    }


    class AuthorViewHolder extends RecyclerView.ViewHolder {
        LinearLayout student_submit_open_report;
        LinearLayout student_submit_middle_check;
        LinearLayout student_submit_guide_record;
        LinearLayout student_submit_foreign_translation;
        LinearLayout student_submit_literature_review;
        LinearLayout student_submit_graduation_thesis;

        public AuthorViewHolder(View itemView) {
            super(itemView);
            student_submit_open_report = (LinearLayout) itemView.findViewById(R.id.student_submit_open_report);
            student_submit_middle_check = (LinearLayout) itemView.findViewById(R.id.student_submit_middle_check);
            student_submit_guide_record = (LinearLayout) itemView.findViewById(R.id.student_submit_guide_record);
            student_submit_foreign_translation = (LinearLayout) itemView.findViewById(R.id.student_submit_foreign_translation);
            student_submit_literature_review = (LinearLayout) itemView.findViewById(R.id.student_submit_literature_review);
            student_submit_graduation_thesis = (LinearLayout) itemView.findViewById(R.id.student_submit_graduation_thesis);
        }
    }
}
