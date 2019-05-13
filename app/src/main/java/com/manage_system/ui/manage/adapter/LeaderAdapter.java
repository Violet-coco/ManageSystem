package com.manage_system.ui.manage.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.manage_system.R;
import com.manage_system.ui.manage.activity.manager.ManagerCtListMainActivity;
import com.manage_system.ui.manage.activity.manager.ManagerTotalListMainActivity;
import com.manage_system.ui.manage.activity.manager.ManagerXtListActivity;
import com.manage_system.ui.personal.GuideStudentInfoActivity;
import com.manage_system.ui.personal.GuideTeacherInfoActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LeaderAdapter extends RecyclerView.Adapter<LeaderAdapter.AuthorViewHolder> {
    private Context context;
    public List<Map<String,Object>> list=new ArrayList<>();
    private OnItemClickListener mOnItemClickListener;

    private String TAG = "MyAdapter";
    private String string;

    public LeaderAdapter(Context context, List<Map<String,Object>> list, String string) {
        this.context = context;
        this.list = list;
        this.string = string;
        Log.w(TAG,list.toString());
    }

    @Override
    public AuthorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View childView = inflater.inflate(R.layout.ms_manager_item, parent, false);
        AuthorViewHolder viewHolder = new AuthorViewHolder(childView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(AuthorViewHolder holder, final int position) {

        if(string.equals("3001")){
            holder.project_title.setText(list.get(position).get("name").toString()+"（"+list.get(position).get("identifier").toString()+"）");
            holder.project_ct.setText("是否出题："+list.get(position).get("is_ct").toString());
            holder.project_number.setText("出题数目："+list.get(position).get("number").toString());

            if(list.get(position).get("number").toString().equals("0")){
                holder.look_title.setEnabled(false);
                holder.look_title.setText("查看选题");
                holder.look_title.setBackgroundColor(Color.parseColor("#dddddd"));
                holder.look_title.setTextColor(Color.GRAY);
            }

            holder.project_title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(v.getContext(),GuideTeacherInfoActivity.class);
                    intent.putExtra("tea_info","from_m_reply");
                    intent.putExtra("teacher_info",list.get(position).get("m_teacher").toString());
                    v.getContext().startActivity(intent);
                }
            });

            holder.look_title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(v.getContext(),ManagerCtListMainActivity.class);
                    intent.putExtra("teacher_info",list.get(position).get("m_teacher").toString());
                    v.getContext().startActivity(intent);
                }
            });
        }else if(string.equals("3002")){
            holder.project_title.setText(list.get(position).get("pName").toString());
            holder.project_ct.setVisibility(View.GONE);
            holder.project_number.setVisibility(View.GONE);
            holder.look_title.setVisibility(View.GONE);
        }else if(string.equals("3003")){
            holder.project_title.setText(Html.fromHtml("<u>"+list.get(position).get("name").toString()+"（"+list.get(position).get("identifier").toString()+"）"+"</u>"));
            holder.project_ct.setText("是否选题："+list.get(position).get("is_ct").toString());
            holder.project_number.setText("课题题目："+Html.fromHtml("<u>"+list.get(position).get("pName").toString()+"</u>"));

            if(list.get(position).get("is_ct").toString().equals("否")){
                holder.look_title.setEnabled(false);
                holder.look_title.setText("暂无课题");
                holder.look_title.setBackgroundColor(Color.parseColor("#dddddd"));
                holder.look_title.setTextColor(Color.GRAY);
            }

            holder.look_title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(v.getContext(),ManagerXtListActivity.class);
                    intent.putExtra("stu_info_ct",list.get(position).get("m_student").toString());
                    v.getContext().startActivity(intent);
                }
            });

            holder.project_title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(v.getContext(),GuideStudentInfoActivity.class);
                    intent.putExtra("stu_info","from_m_reply");
                    intent.putExtra("student_info",list.get(position).get("m_student").toString());
                    v.getContext().startActivity(intent);
                }
            });
        }else if(string.equals("3004")){
            holder.project_title.setText(Html.fromHtml("<u>"+list.get(position).get("pName").toString()+"</u>"));
            holder.project_ct.setText("选题学生："+Html.fromHtml("<u>"+list.get(position).get("c_stu").toString()+"</u>"));
            holder.project_number.setText("出题教师："+Html.fromHtml("<u>"+list.get(position).get("c_teacher_name").toString()+"（"+list.get(position).get("c_teacher_id").toString()+"）"+"</u>"));
            holder.project_item.setText("可选人数/容纳人数："+list.get(position).get("rest").toString()+"/"+list.get(position).get("number").toString());
            holder.project_item.setVisibility(View.VISIBLE);
            if(list.get(position).get("c_stu").toString().equals("暂无学生选题")){
                holder.look_title.setEnabled(false);
                holder.look_title.setText("暂无信息");
                holder.look_title.setBackgroundColor(Color.parseColor("#dddddd"));
                holder.look_title.setTextColor(Color.GRAY);
            }else{
                holder.look_title.setText("查看学生");
                holder.look_title.setBackgroundColor(Color.parseColor("#F55A5D"));
                holder.look_title.setTextColor(Color.WHITE);
            }

            holder.project_title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(v.getContext(),ManagerXtListActivity.class);
                    intent.putExtra("total_info","from_m_total");
                    intent.putExtra("total_project",list.get(position).get("total_project").toString());
                    v.getContext().startActivity(intent);
                }
            });

            holder.project_number.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(v.getContext(),GuideTeacherInfoActivity.class);
                    intent.putExtra("tea_info","from_m_total");
                    intent.putExtra("teacher_info",list.get(position).get("m_total").toString());
                    v.getContext().startActivity(intent);
                }
            });

            holder.look_title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(v.getContext(),ManagerTotalListMainActivity.class);
                    intent.putExtra("total_list","from_m_total");
                    intent.putExtra("total_stu_info",list.get(position).get("total_project").toString());
                    v.getContext().startActivity(intent);
                }
            });
        }else if(string.equals("3006")){
            holder.project_title.setText(Html.fromHtml("<u>"+list.get(position).get("name").toString()+"（"+list.get(position).get("identifier").toString()+"）"+"</u>"));
            holder.project_ct.setVisibility(View.GONE);
            holder.project_number.setVisibility(View.GONE);
            holder.look_title.setVisibility(View.GONE);
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
        private TextView project_ct;
        private TextView project_number;
        private TextView project_item;
        private Button look_title;
        public AuthorViewHolder(View itemView) {
            super(itemView);
            project_title = (TextView)itemView.findViewById(R.id.project_title);
            project_ct = (TextView)itemView.findViewById(R.id.project_ct);
            project_number = (TextView)itemView.findViewById(R.id.project_number);
            project_item = (TextView)itemView.findViewById(R.id.project_item);
            look_title = (Button)itemView.findViewById(R.id.look_title);
        }
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener){
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

}
