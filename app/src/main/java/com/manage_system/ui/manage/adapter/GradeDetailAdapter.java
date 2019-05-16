package com.manage_system.ui.manage.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.manage_system.R;
import com.manage_system.ui.manage.activity.manager.ManagerXtListActivity;
import com.manage_system.ui.personal.GuideStudentInfoActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GradeDetailAdapter extends RecyclerView.Adapter<GradeDetailAdapter.AuthorViewHolder> {
    private Context context;
    public List<Map<String,Object>> list=new ArrayList<>();
    private OnItemClickListener mOnItemClickListener;

    private String TAG = "MyAdapter";
    private String string;

    public GradeDetailAdapter(Context context, List<Map<String,Object>> list, String string) {
        this.context = context;
        this.list = list;
        this.string = string;
        Log.w(TAG,list.toString());
    }

    @Override
    public AuthorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View childView = inflater.inflate(R.layout.ms_tm_grade_list_detail, parent, false);
        AuthorViewHolder viewHolder = new AuthorViewHolder(childView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(AuthorViewHolder holder, final int position) {
        Log.e(TAG,"是否有接受参数");
        Log.w(TAG,string);
        String line = String.valueOf(position +1);
        holder.stu_line.setText(line);
        holder.stu_name_id.setText(Html.fromHtml("<u>"+list.get(position).get("name").toString()+"（"+list.get(position).get("identifier").toString()+"）"+"</u>"));
        holder.stu_pName.setText(Html.fromHtml("<u>"+list.get(position).get("pName").toString()+"</u>"));
        holder.stu_grade.setText("成绩："+list.get(position).get("scoreGt").toString()+" "+list.get(position).get("scoreMt").toString()+" "+list.get(position).get("scoreDef").toString()+ " "+list.get(position).get("scoreTotal").toString()+" "+list.get(position).get("grade").toString());

        holder.stu_name_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(v.getContext(),GuideStudentInfoActivity.class);
                intent.putExtra("stu_info","from_grade_detail");
                intent.putExtra("stu_data",list.get(position).get("stu_info").toString());
                v.getContext().startActivity(intent);
            }
        });

        holder.stu_pName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(v.getContext(),ManagerXtListActivity.class);
                intent.putExtra("total_info","from_grade_detail");
                intent.putExtra("pName_data",list.get(position).get("stu_info").toString());
                v.getContext().startActivity(intent);
            }
        });

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
        private TextView stu_line;
        private TextView stu_name_id;
        private TextView stu_grade;
        private TextView stu_pName;

        public AuthorViewHolder(View itemView) {
            super(itemView);
            stu_line = (TextView)itemView.findViewById(R.id.stu_line);
            stu_name_id = (TextView)itemView.findViewById(R.id.stu_name_id);
            stu_grade = (TextView)itemView.findViewById(R.id.stu_grade);
            stu_pName = (TextView)itemView.findViewById(R.id.stu_pName);

        }
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener){
        this.mOnItemClickListener = mOnItemClickListener;
    }


    public interface OnItemClickListener{
        void onItemClick(int position);
    }

}
