package com.manage_system.ui.manage.activity.teacher;

import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.manage_system.R;
import com.manage_system.net.ApiConstants;
import com.manage_system.ui.manage.Manage;
import com.manage_system.ui.manage.activity.student.StudentChooseDoneTitleMainActivity;
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

public class TeacherOutTitleDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private String id = null;
    private String task_fileId = null;
    private Context mContext;
    private String fileName = null;

    @BindView(R.id.ot_title)
    EditText ot_title;
    @BindView(R.id.ot_intro)
    EditText ot_intro;
    @BindView(R.id.ot_time)
    EditText ot_time;
    @BindView(R.id.ot_source)
    EditText ot_source;
    @BindView(R.id.ot_type)
    EditText ot_type;
    @BindView(R.id.ot_able)
    EditText ot_able;
    @BindView(R.id.ot_state)
    EditText ot_state;
    @BindView(R.id.ot_suggest)
    EditText ot_suggest;
    @BindView(R.id.ot_range)
    EditText ot_range;
    @BindView(R.id.ot_taskBook)
    TextView ot_taskBook;
    @BindView(R.id.tm_pass)
    LinearLayout tm_pass;
    private static String TAG = "选题详细界面";
    private String ways;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ms_tm_subject_detail);
        ButterKnife.bind(this);
        tm_pass.setVisibility(View.GONE);
        Intent intent = getIntent();
        if(intent.getStringExtra("from_way").equals("ot_out")){
            ways = "teacher_outTitle_detail";
        }else if(intent.getStringExtra("from_way").equals("ot_check")){
            ways = "teacher_checkTitle_detail";
        }
        initData(ways);
    }

    /**启动这个Activity的Intent
     * @param context
     * @return
     */
    public static Intent createIntent(Context context) {
        return new Intent(context, TeacherOutTitleDetailActivity.class);
    }

    @OnClick({R.id.iv_back,R.id.ot_taskBook})
    public void onClick(View v) {//直接调用不会显示v被点击效果
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.ot_taskBook:
                downLoad("下载文件","确认下载任务书？",task_fileId);
                break;
            default:
                break;
        }
    }

    public void initData(String str) {
        SharedPreferences sp=getSharedPreferences("processData", MODE_PRIVATE);
        JSONObject obj = JSON.parseObject(sp.getString(str , ""));
        Log.e(TAG,obj.toString());
        JSONObject project = obj.getJSONObject("project");
        ot_title.setText(project.getString("title"));
        ot_suggest.setText(obj.getString("cSuggest"));
        ot_type.setText(project.getString("genre"));
        ot_source.setText(project.getString("source"));
        ot_range.setText(project.getString("range"));
        ot_able.setText(project.getString("rest")+"/"+project.getString("number"));
        ot_intro.setText(project.getString("briefIntro"));
        ot_time.setText(DateUtil.getDateFormat(obj.getString("setDate")));
        if(project.containsKey("taskBook")){
            if(project.getJSONObject("taskBook").containsKey("file")){
                task_fileId = project.getJSONObject("taskBook").getString("fileId");
                ot_taskBook.setTextColor(Color.BLUE);
                ot_taskBook.setText(Html.fromHtml("<u>"+project.getJSONObject("taskBook").getJSONObject("file").getString("fileName")+"</u>"));
            }else{
                ot_taskBook.setEnabled(false);
                ot_taskBook.setText("暂无任务书");
                ot_taskBook.setTextColor(this.getResources().getColor(R.color.white_alpha));
            }

        }else{
            ot_taskBook.setEnabled(false);
            ot_taskBook.setText("暂无任务书");
            ot_taskBook.setTextColor(this.getResources().getColor(R.color.white_alpha));
        }
        String cStatus = null;
        if(obj.getString("cStatus").equals("0")){
            cStatus = "审核不通过";
        }else if(obj.getString("cStatus").equals("1")){
            cStatus = "审核通过";
        }else if(obj.getString("cStatus").equals("2")|| obj.getString("cStatus").equals("3")){
            cStatus = "审核中";
        }
        ot_state.setText(cStatus);
    }

    public void downLoad(String title,String message,final String fileId){
        final Dialog dialog = new Dialog(TeacherOutTitleDetailActivity.this, R.style.MyDialog);
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
                final ProgressDialog dialog= new ProgressDialog(TeacherOutTitleDetailActivity.this);
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
        DownloadUtil.get().download(TeacherOutTitleDetailActivity.this, ApiConstants.commonApi + "/downloadFile"+"?fileId="+fileId,saveurl,fileName,  new DownloadUtil.OnDownloadListener() {
            @Override
            public void onDownloadSuccess() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getNotificationManager().notify(1,getNotification("文件下载成功，点击进行查看",-1));
                        Toast.makeText(TeacherOutTitleDetailActivity.this, "下载成功", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(TeacherOutTitleDetailActivity.this, "下载失败", Toast.LENGTH_SHORT).show();
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
        NotificationCompat.Builder builder = new NotificationCompat.Builder(TeacherOutTitleDetailActivity.this);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle(title);
        if (progress > 0) {
            builder.setContentText(progress+"%");
            builder.setProgress(100,progress,false);
        }
        return builder.build();
    }
}