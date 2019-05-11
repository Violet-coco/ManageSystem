package com.manage_system.ui.manage.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.manage_system.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GradeAdapter extends RecyclerView.Adapter<GradeAdapter.AuthorViewHolder> {
    private Context context;
    public List<Map<String,Object>> list=new ArrayList<>();
    private OnItemClickListener mOnItemClickListener;

    private String TAG = "MyAdapter";
    private String string;

    public GradeAdapter(Context context, List<Map<String,Object>> list, String string) {
        this.context = context;
        this.list = list;
        this.string = string;
        Log.w(TAG,list.toString());
    }

    @Override
    public AuthorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View childView = inflater.inflate(R.layout.ms_tm_grade_list, parent, false);
        AuthorViewHolder viewHolder = new AuthorViewHolder(childView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(AuthorViewHolder holder, final int position) {
        Log.e(TAG,"是否有接受参数");
        Log.w(TAG,string);
        String line = String.valueOf(position +1);
        holder.stu_line.setText(line);
        holder.stu_name_id.setText(list.get(position).get("name").toString()+"（"+list.get(position).get("identifier").toString()+"）");
        holder.stu_grade.setText("成绩："+list.get(position).get("grade").toString());
        holder.stu_level.setText("等级："+list.get(position).get("level").toString());

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
        private TextView stu_level;

        public AuthorViewHolder(View itemView) {
            super(itemView);
            stu_line = (TextView)itemView.findViewById(R.id.stu_line);
            stu_name_id = (TextView)itemView.findViewById(R.id.stu_name_id);
            stu_grade = (TextView)itemView.findViewById(R.id.stu_grade);
            stu_level = (TextView)itemView.findViewById(R.id.stu_level);

        }
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener){
        this.mOnItemClickListener = mOnItemClickListener;
    }


    public interface OnItemClickListener{
        void onItemClick(int position);
    }

}
