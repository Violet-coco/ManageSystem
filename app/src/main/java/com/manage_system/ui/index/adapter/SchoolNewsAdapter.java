package com.manage_system.ui.index.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.manage_system.R;
import com.manage_system.ui.index.activity.IndexNewsMainActivity;
import com.manage_system.ui.manage.adapter.MyAdapter;

import java.util.List;
import java.util.Map;

public class SchoolNewsAdapter extends RecyclerView.Adapter<SchoolNewsAdapter.AuthorViewHolder> {
    private SchoolNewsAdapter.OnItemClickListener mOnItemClickListener;

    public SchoolNewsAdapter(Context context, List<Map<String,Object>> list, String string) {
//        this.context = context;
//        this.list = list;
//        this.string = string;
//        Log.w(TAG,list.toString());
    }

    @Override
    public AuthorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View childView = inflater.inflate(R.layout.ms_browse_item, parent, false);
        AuthorViewHolder viewHolder = new AuthorViewHolder(childView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(AuthorViewHolder holder, int position) {

        holder.project_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(v.getContext(),IndexNewsMainActivity.class);
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
//        return list.size();
    }

    private static String TAG = "CHOOSE";
    class AuthorViewHolder extends RecyclerView.ViewHolder {
        private TextView project_title;

        public AuthorViewHolder(View itemView) {
            super(itemView);
            project_title = (TextView)itemView.findViewById(R.id.project_title);
        }
    }

    public void setOnItemClickListener(SchoolNewsAdapter.OnItemClickListener mOnItemClickListener){
        this.mOnItemClickListener = mOnItemClickListener;
    }


    public interface OnItemClickListener{
        void onItemClick(int position);
    }
}
