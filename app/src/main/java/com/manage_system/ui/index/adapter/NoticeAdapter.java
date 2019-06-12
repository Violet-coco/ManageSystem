package com.manage_system.ui.index.adapter;


import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.manage_system.R;
import com.manage_system.net.ApiConstants;
import com.manage_system.ui.index.activity.NoticeMainActivity;
import com.manage_system.utils.OkManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.AuthorViewHolder> {

    public List<Map<String,Object>> list=new ArrayList<>();
    private OnItemClickListener mOnItemClickListener;

    private String TAG = "NoticeAdapter";
    private String string;
    private Context context;

    public NoticeAdapter(Context context, List<Map<String,Object>> list, String string) {
        this.list = list;
        this.string = string;
        this.context = context;
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
    public void onBindViewHolder(final AuthorViewHolder holder, final int position) {
        if(string.equals("0003")){
            Log.w(TAG,"**************");
            holder.project_title.setText(list.get(position).get("title").toString());
            holder.project_ct.setText("接收人："+list.get(position).get("name").toString()+"（"+list.get(position).get("id").toString()+"）");
            holder.project_number.setText("发送时间："+list.get(position).get("sendDate").toString());
            holder.look_title.setText("查看详情");

            holder.look_title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e(TAG,"别点我");
                    showDialog(v,list.get(position).get("title").toString(),list.get(position).get("content").toString(),list.get(position).get("sendDate").toString());
                }
            });
        }else if(string.equals("0004")){
            Log.w(TAG,"**************");
            holder.project_title.setText(list.get(position).get("title").toString());
            if(list.get(position).get("name").toString().equals("")){
                holder.project_ct.setText("接收人：");
            }else{
                holder.project_ct.setText("接收人："+list.get(position).get("name").toString()+"（"+list.get(position).get("id").toString()+"）");
            }
            holder.project_number.setText("发送时间："+list.get(position).get("sendDate").toString());
            holder.look_title.setText("查看详情");

            holder.look_title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.w(TAG,list.get(position).get("nid").toString());
                    showDialog(v,list.get(position).get("title").toString(),list.get(position).get("content").toString(),list.get(position).get("sendDate").toString());
                    OkManager manager = OkManager.getInstance();
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("nid",list.get(position).get("nid").toString());
                    OkManager.post(ApiConstants.commonApi + "/setNoticeRead", map,new okhttp3.Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.e(TAG, "onFailure: ",e);
                        }
                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            final String responseBody = response.body().string();
                            Log.e(TAG,responseBody);
                            final JSONObject obj = JSON.parseObject(responseBody);
                            if(obj.get("statusCode").equals(100)){
                                Log.w(TAG,"哈哈哈");
                                holder.look_title.setText("已读");
                            }
                        }
                    });
                }
            });
        }else if(string.equals("0005")){
            Log.w(TAG,"**************");
            holder.project_title.setText(list.get(position).get("title").toString());
            holder.project_ct.setText("接收人："+list.get(position).get("sendName").toString());
            holder.project_number.setText("发送时间："+list.get(position).get("sendDate").toString());
            holder.look_title.setText("查看详情");

            holder.look_title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialog(v,list.get(position).get("title").toString(),list.get(position).get("content").toString(),list.get(position).get("sendDate").toString());
                }
            });
        }else if(string.equals("0006")){
            Log.w(TAG,"**************");
            holder.project_title.setText(list.get(position).get("title").toString());
            holder.project_ct.setText("接收人："+list.get(position).get("sendName").toString());
            holder.project_number.setText("发送时间："+list.get(position).get("sendDate").toString());
            holder.look_title.setText("查看详情");

            holder.look_title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialog(v,list.get(position).get("title").toString(),list.get(position).get("content").toString(),list.get(position).get("sendDate").toString());
                    OkManager manager = OkManager.getInstance();
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("aid",list.get(position).get("aid").toString());
                    OkManager.post(ApiConstants.commonApi + "/setAnnounceRead", map,new okhttp3.Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.e(TAG, "onFailure: ",e);
                        }
                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            final String responseBody = response.body().string();
                            Log.e(TAG,responseBody);
                            final JSONObject obj = JSON.parseObject(responseBody);
                            if(obj.get("statusCode").equals(100)){
                                Log.w(TAG,"哈哈哈");
                                holder.look_title.setText("已读");
                            }
                        }
                    });
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

    public void showDialog(View vt,String title,String content,String time){
        final Dialog dialog = new Dialog(vt.getContext(), R.style.MyDialog);
        //设置它的ContentView
        dialog.setContentView(R.layout.ms_notice_alert_dialog);
        ((TextView)dialog.findViewById(R.id.tvAlertDialogTitle)).setText(title);
//        ((TextView)dialog.findViewById(R.id.tvAlertDialogMessage)).setText(content);
        String URL = "<html><body>"+content+"</body></html>";
        /**
         * 将文本HTML显示在webview中
         */
        ((WebView)dialog.findViewById(R.id.webView1)).loadDataWithBaseURL(null,URL,"text/html","utf-8",null);
        ((TextView)dialog.findViewById(R.id.time)).setText(time);
        dialog.show();

        Button cancel = (Button) dialog.findViewById(R.id.btnAlertDialogNegative);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialog.dismiss();
            }
        });
    }


}
