package com.manage_system.ui.index.adapter;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.manage_system.R;
import com.manage_system.ui.index.activity.SchoolNewsActivity;
import com.manage_system.utils.ImageLoaderUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SchoolNewsAdapter extends RecyclerView.Adapter<SchoolNewsAdapter.AuthorViewHolder> {
    private OnItemClickListener mOnItemClickListener;
    public List<Map<String,Object>> list=new ArrayList<>();
    private static String TAG = "SchoolNewsAdapter";

    public SchoolNewsAdapter(List<Map<String,Object>> list) {
//        this.context = context;
        this.list = list;
//        this.string = string;
        Log.w(TAG,list.toString());
    }

    @Override
    public AuthorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View childView = inflater.inflate(R.layout.ms_news_item, parent, false);
        AuthorViewHolder viewHolder = new AuthorViewHolder(childView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final AuthorViewHolder holder, final int position) {
        holder.tv_title.setText(list.get(position).get("title").toString());
        holder.tv_time.setText(list.get(position).get("time").toString());
        holder.tv_name.setText(list.get(position).get("sendName").toString());

        Log.e(TAG,"*********");
        Log.w(TAG,list.get(position).get("image").toString());
        if(list.get(position).get("image").toString().equals("暂无图片")){
            holder.tv_image.setImageDrawable(holder.itemView.getResources().getDrawable(R.drawable.no_picture));
        }else{
            Glide.with(holder.itemView).load(list.get(position).get("image").toString()).into(holder.tv_image);
        }

        holder.tv_image.setScaleType(ImageView.ScaleType.FIT_XY);//使图片充满控件大小

//        String url = "https://pic.cnblogs.com/avatar/1142647/20170416093225.png";


        holder.main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(v.getContext(),SchoolNewsActivity.class);
                Log.w(TAG,list.get(position).get("web_data").toString());
                intent.putExtra("news_data",list.get(position).get("news_data").toString());
                intent.putExtra("web_content",list.get(position).get("web_data").toString());
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class AuthorViewHolder extends RecyclerView.ViewHolder {
        private ImageView tv_image;
        private TextView tv_title;
        private TextView tv_time;
        private TextView tv_name;
        private LinearLayout main;

        public AuthorViewHolder(View itemView) {
            super(itemView);
            tv_image = (ImageView)itemView.findViewById(R.id.tv_image);
            tv_title = (TextView)itemView.findViewById(R.id.tv_title);
            tv_time = (TextView)itemView.findViewById(R.id.tv_time);
            tv_name = (TextView)itemView.findViewById(R.id.tv_name);
            main = (LinearLayout)itemView.findViewById(R.id.main);
        }
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener){
        this.mOnItemClickListener = mOnItemClickListener;
    }


    public interface OnItemClickListener{
        void onItemClick(int position);
    }
}
