package com.manage_system.ui.manage.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
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
import com.manage_system.ui.manage.activity.manager.ManagerCtReportMainActivity;
import com.manage_system.ui.manage.activity.manager.ManagerXtListActivity;
import com.manage_system.ui.personal.GuideStudentInfoActivity;
import com.manage_system.ui.personal.GuideTeacherInfoActivity;
import com.manage_system.utils.DateUtil;

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
        private Button look_title;
        public AuthorViewHolder(View itemView) {
            super(itemView);
            project_title = (TextView)itemView.findViewById(R.id.project_title);
            project_ct = (TextView)itemView.findViewById(R.id.project_ct);
            project_number = (TextView)itemView.findViewById(R.id.project_number);
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
