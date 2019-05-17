package com.manage_system.ui.manage.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.manage_system.R;
import com.manage_system.ui.manage.activity.manager.ManagerDataTotalMainActivity;
import com.manage_system.ui.manage.activity.manager.ManagerGradeListActivity;
import com.manage_system.ui.manage.activity.manager.ManagerPdListActivity;
import com.manage_system.ui.manage.activity.teacher.TeacherListMainActivity;
import com.manage_system.ui.personal.GuideStudentInfoActivity;
import com.manage_system.ui.personal.GuideTeacherInfoActivity;
import com.manage_system.utils.DateUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.AuthorViewHolder> {
    private Context context;
    public List<Map<String,Object>> list=new ArrayList<>();
    private OnItemClickListener mOnItemClickListener;

    private String TAG = "MyAdapter";
    private String string;

    public MyAdapter(Context context, List<Map<String,Object>> list,String string) {
        this.context = context;
        this.list = list;
        this.string = string;
        Log.w(TAG,list.toString());
    }

    @Override
    public AuthorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View childView = inflater.inflate(R.layout.ms_browse_item, parent, false);
        AuthorViewHolder viewHolder = new AuthorViewHolder(childView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(AuthorViewHolder holder, final int position) {
        Log.e(TAG,"是否有接受参数");
        Log.w(TAG,string);
        if(string.equals("1001")){
            holder.project_title.setText(list.get(position).get("title").toString());
            holder.project_teacher.setText("指导老师："+list.get(position).get("name").toString());
            holder.project_number.setText("可选人数："+list.get(position).get("rest").toString());
            holder.project_state_main.setVisibility(View.GONE);
            holder.project_date_main.setVisibility(View.GONE);
            holder.project_teachers_main.setVisibility(View.GONE);
        }else if(string.equals("1002")){
            holder.project_title.setText(list.get(position).get("title").toString());
            holder.project_teacher.setText("指导老师："+list.get(position).get("name").toString());
            holder.project_number.setText("审核状态："+list.get(position).get("cStatus").toString());
            holder.project_state_main.setVisibility(View.GONE);
            holder.project_date_main.setVisibility(View.GONE);
            holder.project_teachers_main.setVisibility(View.GONE);
        }else if(string.equals("1003")){
            holder.project_title.setText(list.get(position).get("title").toString());
            holder.project_teacher.setText("提交时间："+list.get(position).get("submitDate").toString());
            holder.project_number.setText("记录编号："+list.get(position).get("record_times").toString());
            holder.project_state.setText("审核状态："+list.get(position).get("cStatus").toString());
            holder.project_date_main.setVisibility(View.GONE);
            holder.project_teachers_main.setVisibility(View.GONE);
        }else if(string.equals("2001")){
            holder.project_title.setText(list.get(position).get("title").toString());
            holder.project_teacher.setText("可选人数/容纳人数："+list.get(position).get("rest").toString()+"/"+list.get(position).get("number").toString());
            holder.project_number.setText("审核状态："+list.get(position).get("cStatus").toString());
            holder.project_state.setText("任务书："+list.get(position).get("taskBook").toString());
            holder.project_date_main.setVisibility(View.GONE);
            holder.project_teachers_main.setVisibility(View.GONE);
        }else if(string.equals("2002")){
            holder.project_title.setText(list.get(position).get("title").toString());
            holder.project_teacher.setText("学生（学号）："+list.get(position).get("name").toString()+"（"+list.get(position).get("identifier").toString()+"）");
            holder.project_number.setText("审核状态："+list.get(position).get("cStatus").toString());
            holder.project_state_main.setVisibility(View.GONE);
            holder.project_date_main.setVisibility(View.GONE);
            holder.project_teachers_main.setVisibility(View.GONE);
        }else if(string.equals("2003")){
            holder.project_title.setVisibility(View.GONE);
            holder.project_date.setText("答辩时间："+DateUtil.getDateFormat(list.get(position).get("defDate").toString())+" "+"第"+list.get(position).get("defWeek").toString()+"周 星期"+list.get(position).get("defDay").toString());
            holder.project_teacher.setText("答辩教室："+list.get(position).get("defClass").toString());
            holder.project_number.setText("答辩组长：");
            holder.project_number_item.setText(list.get(position).get("leader_name").toString());
            holder.project_state.setText("分组："+"第"+list.get(position).get("groupId").toString()+"组");
            holder.project_teachers.setText("答辩教师：");
            holder.project_teachers_item.setText(list.get(position).get("reply_teachers").toString());

            holder.project_number_item.setTextColor(Color.BLUE);
            holder.project_teachers_item.setTextColor(Color.BLUE);

            holder.look_info.setVisibility(View.VISIBLE);
            holder.look_info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(v.getContext(),TeacherListMainActivity.class);
                    Log.w(TAG,list.get(position).get("teachersInfo").toString());
                    intent.putExtra("teachersInfo",list.get(position).get("teachersInfo").toString());
                    v.getContext().startActivity(intent);
                }
            });

        }else if(string.equals("2004")){
            holder.project_title.setText(list.get(position).get("pName").toString());
            holder.project_date.setText("答辩学生：");
            holder.project_date_item.setText(Html.fromHtml("<u>"+list.get(position).get("name").toString()+"（"+list.get(position).get("identifier").toString()+"）"+"</u>"));
            holder.project_teacher.setText("答辩教师："+list.get(position).get("gt_name").toString()+"（"+list.get(position).get("gt_identifier").toString()+"）");
            holder.project_number_main.setVisibility(View.GONE);
            holder.project_state_main.setVisibility(View.GONE);
            holder.project_teachers_main.setVisibility(View.GONE);

            holder.project_date_item.setTextColor(Color.BLUE);

            holder.project_date_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(v.getContext(),GuideStudentInfoActivity.class);
                    intent.putExtra("stu_info","from_reply");
                    intent.putExtra("stu_id",list.get(position).get("identifier").toString());
                    v.getContext().startActivity(intent);
                }
            });
        }else if(string.equals("2005")){
            holder.project_title.setText(list.get(position).get("pName").toString());
            holder.project_date.setText("答辩学生："+list.get(position).get("name").toString()+"（"+list.get(position).get("identifier").toString()+"）");
            holder.project_teacher.setText("答辩教师："+list.get(position).get("gt_name").toString()+"（"+list.get(position).get("gt_identifier").toString()+"）");
            holder.project_number.setText("分数："+list.get(position).get("scoreTotal").toString());
            holder.project_state_main.setVisibility(View.GONE);
            holder.project_teachers_main.setVisibility(View.GONE);
        }else if(string.equals("2006")){
            holder.project_title.setText(list.get(position).get("pName").toString());
            holder.project_date.setText("答辩学生："+list.get(position).get("name").toString()+"（"+list.get(position).get("identifier").toString()+"）");
            holder.project_teacher.setText(list.get(position).get("def_name1").toString() );
            holder.project_teacher_item.setText( list.get(position).get("scoreTotal1").toString());

            holder.project_number.setText(list.get(position).get("def_name2").toString() );
            holder.project_number_item.setText(list.get(position).get("scoreTotal2").toString());

            holder.project_state.setText(list.get(position).get("def_name3").toString());
            holder.project_state_item.setText(list.get(position).get("scoreTotal3").toString());

            holder.project_teachers.setText(list.get(position).get("def_name4").toString());
            holder.project_teachers_item.setText(list.get(position).get("scoreTotal4").toString());

        }else if(string.equals("2009")){
            holder.project_title.setText(list.get(position).get("pName").toString());
            holder.project_date.setText("答辩学生："+list.get(position).get("name").toString()+"（"+list.get(position).get("identifier").toString()+"）");
            holder.project_teacher.setText("评审分数："+list.get(position).get("score").toString());
            holder.project_number.setText("是否同意答辩："+list.get(position).get("attendDefence").toString());
            holder.project_state_main.setVisibility(View.GONE);
            holder.project_teachers_main.setVisibility(View.GONE);
        }else if(string.equals("2011")){
            holder.project_title.setText(list.get(position).get("pName").toString());
            holder.project_teacher.setText("答辩学生："+list.get(position).get("name").toString()+"（"+list.get(position).get("identifier").toString()+"）");
            holder.project_number.setText("分　　数："+list.get(position).get("scoreTotal").toString());
            holder.project_state_main.setVisibility(View.GONE);
            holder.project_date_main.setVisibility(View.GONE);
            holder.project_teachers_main.setVisibility(View.GONE);
        }else if(string.equals("3109")){
            holder.project_title.setVisibility(View.GONE);
            holder.project_date.setText("学生姓名：");
            holder.project_date_item.setTextColor(Color.BLUE);
            holder.project_date_item.setText(Html.fromHtml("<u>"+list.get(position).get("name").toString()+"（"+list.get(position).get("identifier").toString()+"）"+"</u>"));
            holder.project_number.setText("指导老师："+list.get(position).get("gt_name").toString()+"（"+list.get(position).get("gt_id").toString()+"）");
            holder.project_teacher.setText("课题题目："+list.get(position).get("pName").toString());
            holder.project_state.setText("过程文档：");
            holder.project_state_item.setTextColor(Color.BLUE);
            holder.project_state_item.setText(Html.fromHtml("<u>"+"过程文档提交情况"+"</u>"));
            holder.project_teachers.setText("成　　绩：");
            holder.project_teachers_item.setTextColor(Color.BLUE);
            holder.project_teachers_item.setText(Html.fromHtml("<u>"+"成绩情况"+"</u>"));

            holder.project_date_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(v.getContext(),GuideStudentInfoActivity.class);
                    intent.putExtra("stu_info","from_data_total");
                    intent.putExtra("stu_data",list.get(position).get("stu_info").toString());
                    v.getContext().startActivity(intent);
                }
            });

            holder.project_state_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(v.getContext(),ManagerPdListActivity.class);
                    intent.putExtra("stu_data",list.get(position).get("stu_info").toString());
                    v.getContext().startActivity(intent);
                }
            });

            holder.project_teachers_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(v.getContext(),ManagerGradeListActivity.class);
                    intent.putExtra("stu_data",list.get(position).get("stu_info").toString());
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
        private TextView project_title;
        private TextView project_teacher;
        private TextView project_number;
        private TextView project_state;
        private TextView project_date;
        private TextView project_teachers;
        private Button look_info;

        private TextView project_teacher_item;
        private TextView project_number_item;
        private TextView project_state_item;
        private TextView project_date_item;
        private TextView project_teachers_item;

        private LinearLayout project_teacher_main;
        private LinearLayout project_number_main;
        private LinearLayout project_state_main;
        private LinearLayout project_date_main;
        private LinearLayout project_teachers_main;
        public AuthorViewHolder(View itemView) {
            super(itemView);
            project_title = (TextView)itemView.findViewById(R.id.project_title);
            project_teacher = (TextView)itemView.findViewById(R.id.project_teacher);
            project_number = (TextView)itemView.findViewById(R.id.project_number);
            project_state = (TextView)itemView.findViewById(R.id.project_state);
            project_date = (TextView)itemView.findViewById(R.id.project_date);
            project_teachers = (TextView)itemView.findViewById(R.id.project_teachers);
            look_info = (Button)itemView.findViewById(R.id.look_info);

            project_teacher_item = (TextView)itemView.findViewById(R.id.project_teacher_item);
            project_number_item = (TextView)itemView.findViewById(R.id.project_number_item);
            project_state_item = (TextView)itemView.findViewById(R.id.project_state_item);
            project_date_item = (TextView)itemView.findViewById(R.id.project_date_item);
            project_teachers_item = (TextView)itemView.findViewById(R.id.project_teachers_item);

            project_teacher_main= (LinearLayout) itemView.findViewById(R.id.project_teacher_main);
            project_number_main = (LinearLayout)itemView.findViewById(R.id.project_number_main);
            project_state_main = (LinearLayout)itemView.findViewById(R.id.project_state_main);
            project_date_main= (LinearLayout)itemView.findViewById(R.id.project_date_main);
            project_teachers_main = (LinearLayout)itemView.findViewById(R.id.project_teachers_main);
        }
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener){
        this.mOnItemClickListener = mOnItemClickListener;
    }


    public interface OnItemClickListener{
        void onItemClick(int position);
    }

}
