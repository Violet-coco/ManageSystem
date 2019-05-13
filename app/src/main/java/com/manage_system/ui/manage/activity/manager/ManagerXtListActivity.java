package com.manage_system.ui.manage.activity.manager;

import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.manage_system.R;
import com.manage_system.net.ApiConstants;
import com.manage_system.ui.manage.activity.teacher.TeacherOutTitleMainActivity;
import com.manage_system.utils.DateUtil;
import com.manage_system.utils.DownloadUtil;
import com.manage_system.utils.OkManager;
import com.manage_system.utils.OpenFileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

public class ManagerXtListActivity extends AppCompatActivity implements View.OnClickListener {

    private String id = null;
    private Context mContext;
    private String fileName = null;
    @BindView(R.id.ct_belong_major)
    EditText ct_belong_major;
    @BindView(R.id.ct_topic)
    EditText ct_topic;
    @BindView(R.id.ct_type)
    EditText ct_type;
    @BindView(R.id.ct_resource)
    EditText ct_resource;
    @BindView(R.id.ct_number)
    EditText ct_number;
    @BindView(R.id.ct_profession)
    EditText ct_profession;
    @BindView(R.id.ct_time)
    EditText ct_time;
    @BindView(R.id.ct_task)
    EditText ct_task;
    @BindView(R.id.ct_annex)
    EditText ct_annex;
    @BindView(R.id.ct_detail)
    EditText ct_detail;
    private static String TAG = "ManagerCtListActivity";
    private String fileId,task_fileId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ms_manager_ct_detail);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        if(intent.getStringExtra("total_info").equals("from_m_total")){
            initTotalData();
        }else{
            initData();
        }
    }

    /**启动这个Activity的Intent
     * @param context
     * @return
     */
    public static Intent createIntent(Context context) {
        return new Intent(context, ManagerXtListActivity.class);
    }

    @OnClick({R.id.iv_back,R.id.ct_annex,R.id.ct_task})
    public void onClick(View v) {//直接调用不会显示v被点击效果
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.ct_annex:
                downLoad("下载文件","确定下载附件？",fileId);
                break;
            case R.id.ct_task:
                downLoad("下载文件","确定下载附件？",task_fileId);
                break;
            default:
                break;
        }
    }

    public void initData(){
        Intent intent = getIntent();
        JSONObject object = JSON.parseObject(intent.getStringExtra("stu_info_ct"));

        Log.e(TAG,object.toString());
        ct_topic.setText(object.getJSONObject("checkedPro").getString("title"));
        ct_type.setText(object.getJSONObject("checkedPro").getString("genre"));
        ct_resource.setText(object.getJSONObject("checkedPro").getString("source"));
        ct_number.setText(object.getJSONObject("checkedPro").getString("rest")+"/"+object.getJSONObject("checkedPro").getString("number"));
        ct_belong_major.setText(object.getJSONObject("checkedPro").getString("major"));
        ct_profession.setText(object.getJSONObject("checkedPro").getString("range"));
//        ct_time.setText(DateUtil.getDateFormat(object.getJSONObject("checkedPro").getString("setDate")));
        if(object.getJSONObject("checkedPro").containsKey("taskBook")){
            task_fileId = object.getJSONObject("checkedPro").getJSONObject("taskBook").getString("fileId");
            ct_task.setText(Html.fromHtml("<u>"+object.getJSONObject("checkedPro").getJSONObject("taskBook").getString("task")+"</u>"));
        }else{
            ct_task.setEnabled(false);
            ct_task.setText("暂无任务书");
        }

        if(object.getJSONObject("checkedPro").containsKey("file")){
            fileId = object.getJSONObject("checkedPro").getString("fileId");
            ct_annex.setText(Html.fromHtml("<u>"+object.getJSONObject("checkedPro").getJSONObject("file").getString("fileName")+"</u>"));
        }else{
            ct_annex.setEnabled(false);
            ct_annex.setText("暂无附件");
        }
        ct_detail.setText(object.getJSONObject("checkedPro").getString("briefIntro"));
    }

    public void initTotalData(){
        Intent intent = getIntent();
        JSONObject object = JSON.parseObject(intent.getStringExtra("total_project"));

        Log.e(TAG,object.toString());
        ct_topic.setText(object.getString("title"));
        ct_type.setText(object.getString("genre"));
        ct_resource.setText(object.getString("source"));
        ct_number.setText(object.getString("rest")+"/"+object.getString("number"));
        ct_belong_major.setText(object.getString("major"));
        ct_profession.setText(object.getString("range"));
//        ct_time.setText(DateUtil.getDateFormat(object.getJSONObject("checkedPro").getString("setDate")));
        if(object.containsKey("taskBook")){
            task_fileId = object.getJSONObject("taskBook").getString("fileId");
            ct_task.setText(Html.fromHtml("<u>"+object.getJSONObject("taskBook").getString("task")+"</u>"));
        }else{
            ct_task.setEnabled(false);
            ct_task.setText("暂无任务书");
        }

        if(object.containsKey("file")){
            fileId = object.getString("fileId");
            ct_annex.setText(Html.fromHtml("<u>"+object.getJSONObject("file").getString("fileName")+"</u>"));
        }else{
            ct_annex.setEnabled(false);
            ct_annex.setText("暂无附件");
        }
        ct_detail.setText(object.getString("briefIntro"));
    }

    public void downLoad(String title,String message,final String fileId){
        final Dialog dialog = new Dialog(ManagerXtListActivity.this, R.style.MyDialog);
        //设置它的ContentView
        dialog.setContentView(R.layout.alert_dialog);
        ((TextView)dialog.findViewById(R.id.tvAlertDialogTitle)).setText(title);
        ((TextView)dialog.findViewById(R.id.tvAlertDialogMessage)).setText(message);
        dialog.show();

        Button confirm = (Button)dialog.findViewById(R.id.btnAlertDialogPositive);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Map<String, String> map = new HashMap<String, String>();
                map.put("fileId",fileId);
                Log.w(TAG,fileId+"嘻嘻");
                //文件在手机内存存储的路径
                final String saveurl="/download/";
                Log.w(TAG,"路径1："+saveurl);
                //配置progressDialog
                final ProgressDialog dialog= new ProgressDialog(ManagerXtListActivity.this);
                dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                dialog.setCanceledOnTouchOutside(false);
                dialog.setCancelable(true);
                dialog.setTitle("正在下载中");
                dialog.setMessage("请稍后...");
                dialog.setProgress(0);
                dialog.setMax(100);
                dialog.show();
                startDownload(dialog,fileId,saveurl);

            }
        });
        Button cancel = (Button) dialog.findViewById(R.id.btnAlertDialogNegative);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialog.dismiss();
            }
        });
    }

    public void startDownload(final ProgressDialog dialog,String fileId,String saveurl){
        //启动下载方法
        DownloadUtil.get().download(ManagerXtListActivity.this, ApiConstants.commonApi + "/downloadFile"+"?fileId="+fileId,saveurl,fileName,  new DownloadUtil.OnDownloadListener() {
            @Override
            public void onDownloadSuccess() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getNotificationManager().notify(1,getNotification("文件下载成功，点击进行查看",-1));
                        Toast.makeText(ManagerXtListActivity.this, "下载成功", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();

                        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

                            return;
                        }

                        File file = new File(Environment.getExternalStorageDirectory().getPath() + "/download/"+fileName);
                        Log.w(TAG,"路径2："+file);
                        try {
                            Log.w(TAG,"打开");
                            OpenFileUtils.openFile(mContext, file);
                        } catch (Exception e) {
                            Log.w(TAG,"无打开方式");
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void onDownloading(int progress) {

                dialog.setProgress(progress);
                getNotificationManager().notify(1,getNotification("下载中...",progress));
            }

            @Override
            public void onDownloadFailed() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getNotificationManager().notify(1,getNotification("下载失败",-1));
                        Toast.makeText(ManagerXtListActivity.this, "下载失败", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
            }
        });
    }

    private NotificationManager getNotificationManager(){
        return (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    // 显示对话框；
    private Notification getNotification(String title, int progress) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(ManagerXtListActivity.this);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle(title);
        if (progress > 0) {
            builder.setContentText(progress+"%");
            builder.setProgress(100,progress,false);
        }
        return builder.build();
    }
}