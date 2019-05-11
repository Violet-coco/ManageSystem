package com.manage_system.ui.manage.activity.teacher;

import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
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
import com.manage_system.utils.DateUtil;
import com.manage_system.utils.DownloadUtil;
import com.manage_system.utils.OpenFileUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TeacherThesisCommentActivity extends AppCompatActivity implements View.OnClickListener {

    private String id = null;
    private String task_fileId = null;
    private Context mContext;
    private String fileName = null;

    @BindView(R.id.stu_name_id)
    EditText stu_name_id;
    @BindView(R.id.pName)
    EditText pName;
    @BindView(R.id.scoreInnovate)
    EditText scoreInnovate;
    @BindView(R.id.scoreInstruction)
    EditText scoreInstruction;
    @BindView(R.id.scoreLiterature)
    EditText scoreLiterature;
    @BindView(R.id.scoreTotal)
    EditText scoreTotal;
    @BindView(R.id.scoreWorkload)
    EditText scoreWorkload;
    @BindView(R.id.reviewDate)
    EditText reviewDate;
    @BindView(R.id.comment)
    EditText comment;
    @BindView(R.id.annex)
    TextView annex;
    private static String TAG = "选题界面";
    private String fileId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ms_tm_gt_thesis_item);
        ButterKnife.bind(this);

        Intent intent = getIntent();

        SharedPreferences sp=getSharedPreferences("processData", MODE_PRIVATE);
        Log.w(TAG,sp.getString("teacher_thesis_comment" , ""));
        JSONObject obj = JSON.parseObject(sp.getString("teacher_thesis_comment" , ""));
        JSONArray array = new JSONArray(obj.getJSONArray("data"));
        JSONObject object = array.getJSONObject(Integer.parseInt(intent.getStringExtra("position")));

        stu_name_id.setText(object.getString("name")+"（"+object.getString("identifier")+"）");
        pName.setText(object.getString("pName"));
        scoreLiterature.setText(object.getJSONObject("reviewTeaGroup").getString("scoreLiterature"));
        scoreInstruction.setText(object.getJSONObject("reviewTeaGroup").getString("scoreInstruction"));
        scoreInnovate.setText(object.getJSONObject("reviewTeaGroup").getString("scoreInnovate"));;
        scoreWorkload.setText(object.getJSONObject("reviewTeaGroup").getString("scoreWorkload"));
        scoreTotal.setText(object.getJSONObject("reviewTeaGroup").getString("scoreTotal"));
        comment.setText(object.getJSONObject("reviewTeaGroup").getString("comment"));
        reviewDate.setText(DateUtil.getDateFormat(object.getJSONObject("reviewTeaGroup").getString("reviewDate")));

        if(object.containsKey("graduationProject")){
            fileId = object.getJSONObject("graduationProject").getString("docFileId");
            annex.setText(object.getJSONObject("graduationProject").getJSONObject("docFile").getString("fileName"));
        }else {
            fileId = "0";
            annex.setText("暂未上传附件");
        }
    }

    /**启动这个Activity的Intent
     * @param context
     * @return
     */
    public static Intent createIntent(Context context) {
        return new Intent(context, TeacherThesisCommentActivity.class);
    }

    @OnClick({R.id.iv_back,R.id.annex})
    public void onClick(View v) {//直接调用不会显示v被点击效果
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.annex:
                Log.e(TAG,fileId);
                downLoad("下载文件","确认下载附件？",fileId);
                break;
            default:
                break;
        }
    }

    public void downLoad(String title,String message,final String fileId){
        final Dialog dialog = new Dialog(this, R.style.MyDialog);
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
                final ProgressDialog dialog= new ProgressDialog(TeacherThesisCommentActivity.this);
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
        DownloadUtil.get().download(this, ApiConstants.commonApi + "/downloadFile"+"?fileId="+fileId,saveurl,fileName,  new DownloadUtil.OnDownloadListener() {
            @Override
            public void onDownloadSuccess() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getNotificationManager().notify(1,getNotification("文件下载成功，点击进行查看",-1));
                        Toast.makeText(TeacherThesisCommentActivity.this, "下载成功", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(TeacherThesisCommentActivity.this, "下载失败", Toast.LENGTH_SHORT).show();
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
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle(title);
        if (progress > 0) {
            builder.setContentText(progress+"%");
            builder.setProgress(100,progress,false);
        }
        return builder.build();
    }


}