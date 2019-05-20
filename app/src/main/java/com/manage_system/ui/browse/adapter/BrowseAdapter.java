package com.manage_system.ui.browse.adapter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.manage_system.R;
import com.manage_system.net.ApiConstants;
import com.manage_system.ui.browse.activity.BrowseActivity;
import com.manage_system.ui.manage.adapter.LeaderAdapter;
import com.manage_system.utils.OkManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

public class BrowseAdapter extends RecyclerView.Adapter<BrowseAdapter.AuthorViewHolder> {

    public List<Map<String,Object>> list=new ArrayList<>();
    private OnItemClickListener mOnItemClickListener;

    private String TAG = "BrowseAdapter";

    public BrowseAdapter(List<Map<String,Object>> list) {
        this.list = list;
        Log.w(TAG,"$$$$$$$$$$$$");
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
        Log.w(TAG,"**************");
        Log.w(TAG,list.toString());
        holder.project_last_main.setVisibility(View.VISIBLE);
        holder.project_title.setText(list.get(position).get("pTitle").toString());
        holder.project_ct.setText("优秀生："+list.get(position).get("sName").toString()+"（"+list.get(position).get("sid").toString()+"）");
        holder.project_number.setText("专业："+list.get(position).get("sMaj").toString());
        holder.project_item.setText("届："+list.get(position).get("grade").toString()+"届");
        holder.look_title.setText("查看");

        holder.look_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(v.getContext(),BrowseActivity.class);
                intent.putExtra("excellents_data",list.get(position).get("excellents_data").toString());
                v.getContext().startActivity(intent);
            }
        });
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

        private TextView project_ct_item;
        private TextView project_number_item;
        private LinearLayout project_last_main;
        public AuthorViewHolder(View itemView) {
            super(itemView);
            project_title = (TextView)itemView.findViewById(R.id.project_title);
            project_ct = (TextView)itemView.findViewById(R.id.project_ct);
            project_number = (TextView)itemView.findViewById(R.id.project_number);
            project_item = (TextView)itemView.findViewById(R.id.project_item);
            look_title = (Button)itemView.findViewById(R.id.look_title);

            project_ct_item = (TextView)itemView.findViewById(R.id.project_ct_item);
            project_number_item = (TextView)itemView.findViewById(R.id.project_number_item);
            project_last_main = (LinearLayout)itemView.findViewById(R.id.project_last_main);
        }
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener){
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }
}
