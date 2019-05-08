package com.manage_system.ui.manage.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.manage_system.R;
import com.manage_system.ui.manage.activity.StudentChooseDoneTitleActivity;
import com.manage_system.ui.manage.activity.StudentChooseTitleMainActivity;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
            holder.project_state.setVisibility(View.GONE);
        }else if(string.equals("1002")){
            holder.project_title.setText(list.get(position).get("title").toString());
            holder.project_teacher.setText("指导老师："+list.get(position).get("name").toString());
            holder.project_number.setText("审核状态："+list.get(position).get("cStatus").toString());
            holder.project_state.setVisibility(View.GONE);
        }else if(string.equals("1003")){
            holder.project_title.setText(list.get(position).get("title").toString());
            holder.project_teacher.setText("提交时间："+list.get(position).get("submitDate").toString());
            holder.project_number.setText("记录编号："+list.get(position).get("record_times").toString());
            holder.project_state.setText("审核状态："+list.get(position).get("cStatus").toString());
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
        public AuthorViewHolder(View itemView) {
            super(itemView);
            project_title = (TextView)itemView.findViewById(R.id.project_title);
            project_teacher = (TextView)itemView.findViewById(R.id.project_teacher);
            project_number = (TextView)itemView.findViewById(R.id.project_number);
            project_state = (TextView)itemView.findViewById(R.id.project_state);
        }
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener){
        this.mOnItemClickListener = mOnItemClickListener;
    }


    public interface OnItemClickListener{
        void onItemClick(int position);
    }

}
