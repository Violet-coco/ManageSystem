package com.manage_system.ui.manage.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.manage_system.R;
import com.manage_system.net.ApiConstants;
import com.manage_system.ui.manage.Manage;
import com.manage_system.ui.manage.activity.manager.ManagerCtCheckMainActivity;
import com.manage_system.ui.manage.activity.manager.ManagerCtListMainActivity;
import com.manage_system.ui.manage.activity.manager.ManagerShowCheckActivity;
import com.manage_system.ui.manage.activity.manager.ManagerTotalListMainActivity;
import com.manage_system.ui.manage.activity.manager.ManagerXtCheckMainActivity;
import com.manage_system.ui.manage.activity.manager.ManagerXtListActivity;
import com.manage_system.ui.manage.activity.student.StudentChooseDoneTitleMainActivity;
import com.manage_system.ui.manage.activity.student.StudentChooseTitleActivity;
import com.manage_system.ui.personal.GuideStudentInfoActivity;
import com.manage_system.ui.personal.GuideTeacherInfoActivity;
import com.manage_system.utils.OkManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

import static com.mob.MobSDK.getContext;

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
            holder.project_title.setText(Html.fromHtml("<u>"+list.get(position).get("name").toString()+"（"+list.get(position).get("identifier").toString()+"）"+"</u>"));
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
                    intent.putExtra("total_info","");
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
            holder.project_number.setText("出题教师：");
            holder.project_number_item.setTextColor(Color.BLUE);
            holder.project_number_item.setText(Html.fromHtml("<u>"+list.get(position).get("c_teacher_name").toString()+"（"+list.get(position).get("c_teacher_id").toString()+"）"+"</u>"));
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

            holder.project_number_item.setOnClickListener(new View.OnClickListener() {
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
        }else if(string.equals("3007")){
            holder.project_item.setVisibility(View.VISIBLE);
            holder.project_title.setText(list.get(position).get("pName").toString());
            holder.project_ct.setText("学生姓名："+list.get(position).get("name").toString());
            holder.project_number.setText("选题时间："+list.get(position).get("chooseDate").toString());
            holder.project_item.setText("审核状态："+list.get(position).get("status").toString());
            if (list.get(position).get("status").toString().equals("审核通过")) {
                holder.look_title.setText("已审核");
                holder.look_title.setEnabled(false);
                holder.look_title.setBackgroundColor(Color.parseColor("#dddddd"));
                holder.look_title.setTextColor(Color.GRAY);
            } else if(list.get(position).get("status").toString().equals("审核不通过")) {
                holder.look_title.setText("审核不通过");
                holder.look_title.setEnabled(false);
                holder.look_title.setBackgroundColor(Color.parseColor("#F5F5F5"));
                holder.look_title.setTextColor(Color.GRAY);
            }else{
                holder.look_title.setText("审核");
                holder.look_title.setBackgroundColor(Color.parseColor("#F55A5D"));
                holder.look_title.setTextColor(Color.WHITE);
            }

            holder.look_title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View vt) {
                    final String sid = list.get(position).get("sid").toString();
                    final String pid = list.get(position).get("pid").toString();
                    showXtDialog(vt,sid,pid);
                }
            });

        }else if(string.equals("3008")) {
            holder.project_last_main.setVisibility(View.VISIBLE);
            holder.project_title.setText(Html.fromHtml("<u>"+list.get(position).get("pName").toString()+"</u>"));
            holder.project_ct.setText("指导老师：");
            holder.project_ct_item.setTextColor(Color.BLUE);
            holder.project_ct_item.setText(Html.fromHtml("<u>"+list.get(position).get("name").toString()+"</u>"));
            holder.project_number.setText("申报时间：" + list.get(position).get("setDate").toString());
            holder.project_item.setText("审核状态：" + list.get(position).get("status").toString());
            if (list.get(position).get("status").toString().equals("审核通过")) {
                holder.look_title.setText("已审核");
                holder.look_title.setEnabled(false);
                holder.look_title.setBackgroundColor(Color.parseColor("#dddddd"));
                holder.look_title.setTextColor(Color.GRAY);
            } else if(list.get(position).get("status").toString().equals("审核不通过")) {
                holder.look_title.setText("审核不通过");
                holder.look_title.setEnabled(false);
                holder.look_title.setBackgroundColor(Color.parseColor("#F5F5F5"));
                holder.look_title.setTextColor(Color.GRAY);
            }else{
                holder.look_title.setText("审核");
                holder.look_title.setEnabled(true);
                holder.look_title.setBackgroundColor(Color.parseColor("#F55A5D"));
                holder.look_title.setTextColor(Color.WHITE);
            }

            holder.project_title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(v.getContext(),ManagerXtListActivity.class);
                    intent.putExtra("total_info","from_m_ct");
                    intent.putExtra("project_info",list.get(position).get("project_info").toString());
                    v.getContext().startActivity(intent);
                }
            });

            holder.project_ct_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(v.getContext(),GuideTeacherInfoActivity.class);
                    intent.putExtra("tea_info","from_m_ct");
                    intent.putExtra("teacher_info",list.get(position).get("teacher_info").toString());
                    v.getContext().startActivity(intent);
                }
            });

            holder.look_title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View vt) {
                    String tid = list.get(position).get("tid").toString();
                    String pid = list.get(position).get("pid").toString();
                    Log.w(TAG,"hhh");
                    showCtDialog(vt,tid,pid);
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

    public void showCtDialog(View vt,final String tid,final String pid){
        final Dialog dialog = new Dialog(vt.getContext(), R.style.MyDialog);
        //设置它的ContentView
        dialog.setContentView(R.layout.alert_dialog);
        ((TextView)dialog.findViewById(R.id.tvAlertDialogTitle)).setText("审核选题");
        ((TextView)dialog.findViewById(R.id.tvAlertDialogMessage)).setText("确认通过审核？");
        dialog.show();

        Button confirm = (Button)dialog.findViewById(R.id.btnAlertDialogPositive);
        confirm.setText("通过");
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                dialog.dismiss();
                OkManager manager = OkManager.getInstance();
                Map<String, String> map = new HashMap<String, String>();
                map.put("tid", tid);
                map.put("pid", pid);
                map.put("status", "2");
                manager.post(ApiConstants.teacherApi+"/verifyTeaProject", map,new okhttp3.Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e(TAG, "onFailure: ",e);
                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String responseBody = response.body().string();
                        final JSONObject obj = JSON.parseObject(responseBody);
                        Log.e(TAG,obj.toString());
                        if(obj.get("statusCode").equals(100)) {
                            Looper.prepare();
                            Toast.makeText(v.getContext(), obj.getString("msg"), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(v.getContext(),ManagerCtCheckMainActivity.class);
                            v.getContext().startActivity(intent);
                            Looper.loop();
                        }else{
                            Looper.prepare();
                            Toast.makeText(v.getContext(), obj.getString("msg"), Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }

                    }
                });
            }
        });
        Button cancel = (Button) dialog.findViewById(R.id.btnAlertDialogNegative);
        cancel.setText("不通过");
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                // TODO Auto-generated method stub
                dialog.dismiss();
                OkManager manager = OkManager.getInstance();
                Map<String, String> map = new HashMap<String, String>();
                map.put("tid", tid);
                map.put("pid", pid);
                map.put("status", "0");
                manager.post(ApiConstants.teacherApi+"/verifyTeaProject", map,new okhttp3.Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e(TAG, "onFailure: ",e);
                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String responseBody = response.body().string();
                        final JSONObject obj = JSON.parseObject(responseBody);
                        Log.e(TAG,obj.toString());
                        if(obj.get("statusCode").equals(100)) {
                            Looper.prepare();
                            Toast.makeText(v.getContext(), obj.getString("msg"), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(v.getContext(),ManagerCtCheckMainActivity.class);
                            v.getContext().startActivity(intent);
                            Looper.loop();
                        }else{
                            Looper.prepare();
                            Toast.makeText(v.getContext(), obj.getString("msg"), Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }

                    }
                });
            }
        });
    }

    public void showXtDialog(View vt,final String sid,final String pid){
        final Dialog dialog = new Dialog(vt.getContext(), R.style.MyDialog);
        //设置它的ContentView
        dialog.setContentView(R.layout.alert_dialog);
        ((TextView)dialog.findViewById(R.id.tvAlertDialogTitle)).setText("审核选题");
        ((TextView)dialog.findViewById(R.id.tvAlertDialogMessage)).setText("确认通过审核？");
        dialog.show();

        Button confirm = (Button)dialog.findViewById(R.id.btnAlertDialogPositive);
        confirm.setText("通过");
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                dialog.dismiss();
                OkManager manager = OkManager.getInstance();
                Map<String, String> map = new HashMap<String, String>();
                map.put("sid", sid);
                map.put("pid", pid);
                map.put("status", "1");
                manager.post(ApiConstants.teacherApi+"/verifyStuProject", map,new okhttp3.Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e(TAG, "onFailure: ",e);
                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String responseBody = response.body().string();
                        final JSONObject obj = JSON.parseObject(responseBody);
                        Log.e(TAG,obj.toString());
                        if(obj.get("statusCode").equals(100)) {
                            Looper.prepare();
                            Toast.makeText(v.getContext(), obj.getString("msg"), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(v.getContext(),ManagerCtCheckMainActivity.class);
                            v.getContext().startActivity(intent);
                            Looper.loop();
                        }else{
                            Looper.prepare();
                            Toast.makeText(v.getContext(), obj.getString("msg"), Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }

                    }
                });
            }
        });
        Button cancel = (Button) dialog.findViewById(R.id.btnAlertDialogNegative);
        cancel.setText("不通过");
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                // TODO Auto-generated method stub
                dialog.dismiss();
                OkManager manager = OkManager.getInstance();
                Map<String, String> map = new HashMap<String, String>();
                map.put("sid", sid);
                map.put("pid", pid);
                map.put("status", "0");
                manager.post(ApiConstants.teacherApi+"/verifyStuProject", map,new okhttp3.Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e(TAG, "onFailure: ",e);
                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String responseBody = response.body().string();
                        final JSONObject obj = JSON.parseObject(responseBody);
                        Log.e(TAG,obj.toString());
                        if(obj.get("statusCode").equals(100)) {
                            Looper.prepare();
                            Toast.makeText(v.getContext(), obj.getString("msg"), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(v.getContext(),ManagerCtCheckMainActivity.class);
                            v.getContext().startActivity(intent);
                            Looper.loop();
                        }else{
                            Looper.prepare();
                            Toast.makeText(v.getContext(), obj.getString("msg"), Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }

                    }
                });
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
