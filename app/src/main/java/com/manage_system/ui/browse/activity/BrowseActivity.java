package com.manage_system.ui.browse.activity;

import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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

public class BrowseActivity extends AppCompatActivity implements View.OnClickListener {

    private String id = null;
    private Context mContext;
    private String fileName = null;
    @BindView(R.id.exe_grade)
    EditText exe_grade;
    @BindView(R.id.exe_major)
    EditText exe_major;
    @BindView(R.id.exe_name)
    EditText exe_name;
    @BindView(R.id.exe_topic)
    EditText exe_topic;
    @BindView(R.id.exe_title_annex)
    TextView exe_title_annex;
    @BindView(R.id.exe_word_annex)
    TextView exe_word_annex;

    private static String TAG = "BrowseActivity";
    private String docFileId,pFileId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ms_browse_detail);
        ButterKnife.bind(this);
        initData();
    }

    /**启动这个Activity的Intent
     * @param context
     * @return
     */
    public static Intent createIntent(Context context) {
        return new Intent(context, BrowseActivity.class);
    }

    @OnClick({R.id.iv_back,R.id.exe_title_annex,R.id.exe_word_annex})
    public void onClick(View v) {//直接调用不会显示v被点击效果
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.exe_title_annex:
                downLoad("下载附件","确定下载该题目附件？",pFileId);
                break;
            case R.id.exe_word_annex:
                downLoad("下载附件","确定下载该文档附件？",docFileId);
                break;
            default:
                break;
        }
    }
    public void initData(){
        Intent intent = getIntent();
        JSONObject obj = JSON.parseObject(intent.getStringExtra("excellents_data"));
        exe_topic.setText(obj.getString("pTitle"));
        exe_grade.setText(obj.getString("grade")+"届");
        exe_major.setText(obj.getString("sMaj"));
        exe_name.setText(obj.getString("sName")+"（"+obj.getString("sid")+"）");
        if(obj.containsKey("pFile")){
            pFileId = obj.getString("pFileId");
            exe_title_annex.setText(Html.fromHtml("<u>"+obj.getJSONObject("pFile").getString("fileName")+"</u>"));
        }else{
            exe_title_annex.setEnabled(false);
            exe_title_annex.setText("暂无附件");
            exe_title_annex.setTextColor(Color.parseColor("#666666"));
        }

        if(obj.containsKey("docFile")){
            docFileId = obj.getString("docFileId");
            exe_word_annex.setText(Html.fromHtml("<u>"+obj.getJSONObject("docFile").getString("fileName")+"</u>"));
        }else{
            exe_word_annex.setEnabled(false);
            exe_word_annex.setText("暂无附件");
            exe_word_annex.setTextColor(Color.parseColor("#666666"));
        }


    }

    public void downLoad(String title,String message,final String fileId){
        final Dialog dialog = new Dialog(BrowseActivity.this, R.style.MyDialog);
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
                final ProgressDialog dialog= new ProgressDialog(BrowseActivity.this);
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
        DownloadUtil.get().download(BrowseActivity.this, ApiConstants.commonApi + "/downloadFile"+"?fileId="+fileId,saveurl,fileName,  new DownloadUtil.OnDownloadListener() {
            @Override
            public void onDownloadSuccess() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getNotificationManager().notify(1,getNotification("文件下载成功，点击进行查看",-1));
                        Toast.makeText(BrowseActivity.this, "下载成功", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(BrowseActivity.this, "下载失败", Toast.LENGTH_SHORT).show();
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
        NotificationCompat.Builder builder = new NotificationCompat.Builder(BrowseActivity.this);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle(title);
        if (progress > 0) {
            builder.setContentText(progress+"%");
            builder.setProgress(100,progress,false);
        }
        return builder.build();
    }
}