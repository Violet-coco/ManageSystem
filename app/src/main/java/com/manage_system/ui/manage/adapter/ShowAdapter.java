package com.manage_system.ui.manage.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonArray;
import com.manage_system.R;
import com.manage_system.net.ApiConstants;
import com.manage_system.ui.manage.activity.student.StudentForeignTranslationActivity;
import com.manage_system.ui.manage.activity.student.StudentGraduationThesisActivity;
import com.manage_system.ui.manage.activity.student.StudentGraduationThesisMainActivity;
import com.manage_system.ui.manage.activity.student.StudentGuideReportMainActivity;
import com.manage_system.ui.manage.activity.student.StudentLiteratureReviewActivity;
import com.manage_system.ui.manage.activity.student.StudentMiddleCheckActivity;
import com.manage_system.ui.manage.activity.student.StudentOpenReportActivity;
import com.manage_system.ui.manage.activity.teacher.TeacherScoreMainActivity;
import com.manage_system.ui.personal.GuideStudentInfoActivity;
import com.manage_system.ui.personal.GuideTeacherInfoActivity;
import com.manage_system.utils.DateUtil;
import com.manage_system.utils.OkManager;

import org.json.JSONArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

public class ShowAdapter extends RecyclerView.Adapter<ShowAdapter.AuthorViewHolder> {
    private Context context;
    public List<Map<String,Object>> list=new ArrayList<>();
    private OnItemClickListener mOnItemClickListener;

    private String TAG = "MyAdapter";
    private String string;

    public ShowAdapter(Context context, List<Map<String,Object>> list, String string) {
        this.context = context;
        this.list = list;
        this.string = string;
        Log.w(TAG,list.toString());
    }

    @Override
    public AuthorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View childView = inflater.inflate(R.layout.ms_tm_reply_plan_list, parent, false);
        AuthorViewHolder viewHolder = new AuthorViewHolder(childView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(AuthorViewHolder holder, final int position) {
        Log.e(TAG,"是否有接受参数");
        Log.w(TAG,string);
        if(string.equals("2007")){
            holder.stu_name_id.setText(list.get(position).get("name").toString()+"（"+list.get(position).get("identifier").toString()+"）");
            holder.stu_date.setText("答辩时间："+list.get(position).get("date").toString()+"  第"+list.get(position).get("defWeek").toString()+"周 星期"+list.get(position).get("defDay").toString());
            holder.stu_class.setText("答辩教室："+list.get(position).get("defClass").toString());
            holder.stu_leader.setText("答辩组长："+list.get(position).get("leader").toString());
            holder.stu_reply_teacher.setText("答辩教师："+list.get(position).get("reply_teacher").toString());
            holder.stu_comment_teacher.setText("评审教师："+list.get(position).get("comment_teacher").toString());
            holder.stu_group.setText("分　　组："+"第"+list.get(position).get("group").toString()+"组");
        }else if(string.equals("2008")){
            holder.stu_name_id.setText(list.get(position).get("name").toString()+"（"+list.get(position).get("identifier").toString()+"）");
            holder.stu_date.setText("开题报告："+list.get(position).get("progressList1").toString());
            holder.stu_class.setText("中期检查："+list.get(position).get("progressList2").toString());
            holder.stu_leader.setText("指导记录："+list.get(position).get("progressList3").toString());
            holder.stu_reply_teacher.setText("外文译文和原文："+list.get(position).get("progressList4").toString());
            holder.stu_comment_teacher.setText("文献综述："+list.get(position).get("progressList5").toString());
            holder.stu_group.setText("毕业设计和论文："+list.get(position).get("progressList6").toString());

            holder.stu_date.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(v.getContext(),StudentOpenReportActivity.class);
                    intent.putExtra("doc_type","1");
                    intent.putExtra("stu_id",list.get(position).get("identifier").toString());
                    v.getContext().startActivity(intent);
                }
            });

            holder.stu_class.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(v.getContext(),StudentMiddleCheckActivity.class);
                    intent.putExtra("doc_type","2");
                    intent.putExtra("stu_id",list.get(position).get("identifier").toString());
                    v.getContext().startActivity(intent);
                }
            });

            holder.stu_reply_teacher.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(v.getContext(),StudentForeignTranslationActivity.class);
                    intent.putExtra("doc_type","3");
                    intent.putExtra("stu_id",list.get(position).get("identifier").toString());
                    v.getContext().startActivity(intent);
                }
            });

            holder.stu_comment_teacher.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(v.getContext(),StudentLiteratureReviewActivity.class);
                    intent.putExtra("doc_type","4");
                    intent.putExtra("stu_id",list.get(position).get("identifier").toString());
                    v.getContext().startActivity(intent);
                }
            });

            holder.stu_leader.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(v.getContext(),StudentGuideReportMainActivity.class);
                    intent.putExtra("stu_id",list.get(position).get("identifier").toString());
                    intent.putExtra("pName",list.get(position).get("pName").toString());
                    v.getContext().startActivity(intent);
                }
            });

            holder.stu_group.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(v.getContext(),StudentGraduationThesisMainActivity.class);
                    intent.putExtra("stu_id",list.get(position).get("identifier").toString());
                    intent.putExtra("pName",list.get(position).get("pName").toString());
                    v.getContext().startActivity(intent);
                }
            });
        }

        // 点击事件一般都写在绑定数据这里，当然写到上边的创建布局时候也是可以的
        if (mOnItemClickListener != null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 这里利用回调来给RecyclerView设置点击事件
                    mOnItemClickListener.onItemClick(position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class AuthorViewHolder extends RecyclerView.ViewHolder {
        private TextView stu_name_id;
        private TextView stu_date;
        private TextView stu_class;
        private TextView stu_leader;
        private TextView stu_reply_teacher;
        private TextView stu_comment_teacher;
        private TextView stu_group;
        public AuthorViewHolder(View itemView) {
            super(itemView);
            stu_name_id = (TextView)itemView.findViewById(R.id.stu_name_id);
            stu_date = (TextView)itemView.findViewById(R.id.stu_date);
            stu_class = (TextView)itemView.findViewById(R.id.stu_class);
            stu_leader = (TextView)itemView.findViewById(R.id.stu_leader);
            stu_reply_teacher = (TextView)itemView.findViewById(R.id.stu_reply_teacher);
            stu_comment_teacher = (TextView)itemView.findViewById(R.id.stu_comment_teacher);
            stu_group = (TextView)itemView.findViewById(R.id.stu_group);
        }
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener){
        this.mOnItemClickListener = mOnItemClickListener;
    }


    public interface OnItemClickListener{
        void onItemClick(int position);
    }

}
