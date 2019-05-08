package com.manage_system.ui.manage.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.manage_system.R;
import com.manage_system.net.ApiConstants;
import com.manage_system.ui.personal.GuideTeacherInfoActivity;
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
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.manage_system.utils.FileUtils.getPath;
import static com.manage_system.utils.FileUtils.getRealPathFromURI;

public class StudentForeignTranslationActivity extends AppCompatActivity implements View.OnClickListener {

    ImageButton iv_back;
    @BindView(R.id.nav_title)
    TextView nav_title;
    @BindView(R.id.ft_time)
    TextView ft_time;
    @BindView(R.id.ft_state)
    TextView ft_state;
    @BindView(R.id.ft_foreign)
    EditText ft_foreign;
    @BindView(R.id.ft_original)
    EditText ft_original;
    @BindView(R.id.ft_annotation)
    EditText ft_annotation;
    @BindView(R.id.ft_forFile)
    EditText ft_forFile;
    @BindView(R.id.ft_oriFile)
    EditText ft_oriFile;
    @BindView(R.id.for_tra_edit)
    Button for_tra_edit;
    @BindView(R.id.for_tra_submit)
    Button for_tra_submit;
    @BindView(R.id.tm_check_main)
    LinearLayout tm_check_main;
    @BindView(R.id.ft_submit_for_annex)
    Button ft_submit_for_annex;
    @BindView(R.id.ft_submit_ori_annex)
    Button ft_submit_ori_annex;
    @BindView(R.id.ft_annotation_main)
    RelativeLayout ft_annotation_main;
    private String TAG = "外文译文和原文";
    private String original,foreign,uploadOrifile,uploadForfile,uploadfile;
    private String forFileId = "0";
    private String oriFileId = "0";
    private String path;
    private File file;
    private String fileName = null;
    private Context mContext;
    private int fileIdType=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ms_student_pd_fortra);
        ButterKnife.bind(this);
        initEditStatus();
        tm_check_main.setVisibility(View.GONE);
        for_tra_submit.setVisibility(View.GONE);
        ft_submit_for_annex.setVisibility(View.GONE);
        ft_submit_ori_annex.setVisibility(View.GONE);
        getId();
        initData();
    }

    /**启动这个Activity的Intent
     * @param context
     * @return
     */
    public static Intent createIntent(Context context) {
        return new Intent(context, StudentForeignTranslationActivity.class);
    }

    public void initEditStatus() {
        ft_time.setEnabled(false);
        ft_state.setEnabled(false);
        ft_foreign.setEnabled(false);
        ft_original.setEnabled(false);
        ft_annotation.setEnabled(false);
    }

    public void changeState() {
        Log.w(TAG,"可编辑");
        ft_foreign.setEnabled(true);
        ft_foreign.setFocusable(true);
        ft_original.setEnabled(true);
        ft_annotation_main.setVisibility(View.GONE);
        ft_submit_for_annex.setVisibility(View.VISIBLE);
        ft_submit_ori_annex.setVisibility(View.VISIBLE);
        for_tra_edit.setVisibility(View.GONE);
        for_tra_submit.setVisibility(View.VISIBLE);
    }

    /**
     * 获取id
     */
    private void getId()
    {
        iv_back = (ImageButton)findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);
    }

    @OnClick({R.id.for_tra_edit,R.id.ft_submit_for_annex,R.id.ft_submit_ori_annex,R.id.for_tra_submit,R.id.ft_forFile,R.id.ft_oriFile})
    public void onClick(View v) {//直接调用不会显示v被点击效果
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.for_tra_edit:
                changeState();
                nav_title.setText("修改外文译文和原文");
                break;
            case R.id.ft_submit_for_annex:
                fileIdType = 1;
                showFileChooser();
                break;
            case R.id.ft_submit_ori_annex:
                fileIdType = 2;
                showFileChooser();
                break;
            case R.id.for_tra_submit:
                submitData();
                break;
            case R.id.ft_forFile:
                downLoad("下载文件","确认下载原文译文附件？",forFileId);
                break;
            case R.id.ft_oriFile:
                downLoad("下载文件","确认下载原文附件？",oriFileId);
                break;
            default:
                break;
        }
    }

    public void initData() {
        OkManager manager = OkManager.getInstance();
        Map<String, String> map = new HashMap<>();
        manager.post(ApiConstants.studentApi + "/showForeignOriginal", map,new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "onFailure: ",e);
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseBody = response.body().string();
                final JSONObject obj = JSON.parseObject(responseBody);
                Log.e(TAG,obj.toString());
                final String msg = obj.getString("msg");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(obj.get("statusCode").equals(100)){
                            JSONObject object = obj.getJSONObject("data");
                            Log.w(TAG,obj.getJSONObject("data").toString());
                            ft_time.setText(DateUtil.getDateFormat(object.getString("submitDate")));
                            String status = object.getString("cStatus");
                            String cStatus = null;
                            Log.w(TAG,status+"喔喔");
                            if(status.equals("0")){
                                cStatus = "审核不通过";
                            }else if(status.equals("1")){
                                cStatus = "审核通过";
                                for_tra_submit.setVisibility(View.GONE);
                            }else if(status.equals("2")|| status.equals("3")){
                                cStatus = "审核中";
                            }
                            ft_state.setText(cStatus);
                            ft_foreign.setText(object.getString("foreign"));
                            ft_original.setText(object.getString("original"));
                            ft_annotation.setText(object.getString("annotation"));

                            oriFileId = object.getString("oriFileId");
                            forFileId = object.getString("forFileId");
                            if(object.get("forFileId").equals(0)){
                                ft_forFile.setText("暂无附件");
                                ft_forFile.setEnabled(false);
                            }else{
                                if(!object.getJSONObject("forFile").getString("fileName").isEmpty()){
                                    fileName = object.getJSONObject("forFile").getString("fileName");
                                    ft_forFile.setText(Html.fromHtml("<u>"+object.getJSONObject("forFile").getString("fileName")+"</u>"));
                                }else{
                                    fileName = object.getString("title");
                                    ft_forFile.setText(Html.fromHtml("<u>"+"外文译文.附件"+"</u>"));
                                }
                            }

                            if(object.get("oriFileId").equals(0)){
                                ft_oriFile.setText("暂无附件");
                                ft_oriFile.setEnabled(false);
                            }else{
                                if(!object.getJSONObject("oriFile").getString("fileName").isEmpty()){
                                    fileName = object.getJSONObject("oriFile").getString("fileName");
                                    ft_oriFile.setText(Html.fromHtml("<u>"+object.getJSONObject("oriFile").getString("fileName")+"</u>"));
                                }else{
                                    fileName = object.getString("title");
                                    ft_oriFile.setText(Html.fromHtml("<u>"+"原文.附件"+"</u>"));
                                }
                            }

                        }else if(obj.get("statusCode").equals(101)){
                            Toast.makeText(StudentForeignTranslationActivity.this, msg, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(StudentForeignTranslationActivity.this,StudentForeignTranslationEditActivity.class);
                            startActivity(intent);
                        }else{
                            Toast.makeText(StudentForeignTranslationActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }

    public void submitData() {
        original=ft_foreign.getText().toString().trim();
        foreign=ft_original.getText().toString().trim();
        uploadOrifile=ft_forFile.getText().toString().trim();
        uploadForfile=ft_oriFile.getText().toString().trim();
        OkManager manager = OkManager.getInstance();
        RequestBody requestBody;
        if(file == null){
            requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("foreign", foreign)
                    .addFormDataPart("original", original) // 提交普通字段
                    .addFormDataPart("forFileId", forFileId) // 提交普通字段
                    .addFormDataPart("oriFileId", oriFileId)
                    .addFormDataPart("uploadForfile", uploadForfile)
                    .addFormDataPart("uploadOrifile", uploadOrifile)
                    .build();
        }else{
            requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("foreign", foreign)
                    .addFormDataPart("original", original) // 提交普通字段
                    .addFormDataPart("forFileId", forFileId) // 提交普通字段
                    .addFormDataPart("oriFileId", oriFileId)
                    .addFormDataPart("uploadForfile", uploadForfile, RequestBody.create(MediaType.parse("*/*"), file))
                    .addFormDataPart("uploadOrifile", uploadOrifile, RequestBody.create(MediaType.parse("*/*"), file))
                    .build();
        }

        manager.postFile(ApiConstants.studentApi + "/commitForeignOriginal", requestBody,new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "onFailure: ",e);
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseBody = response.body().string();
                Log.e(TAG,responseBody);
                final JSONObject obj = JSON.parseObject(responseBody);
                Log.e(TAG,obj.toString());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(obj.get("statusCode").equals(100)){
                            Toast.makeText(StudentForeignTranslationActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(StudentForeignTranslationActivity.this,StudentForeignTranslationActivity.class);
                            startActivity(intent);
                        }else {
                            Toast.makeText(StudentForeignTranslationActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }

    //打开文件选择器
    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.w(TAG,"返回的数据："+data);
        if (resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            if ("file".equalsIgnoreCase(uri.getScheme())){//使用第三方应用打开
                path = uri.getPath();
                file = new File(path);
                uploadfile = file.getName();
                if(fileIdType == 1){
                    ft_forFile.setText(uploadfile);
                }
                if(fileIdType == 2){
                    ft_oriFile.setText(uploadfile);
                }
                Toast.makeText(this,path+"11111",Toast.LENGTH_SHORT).show();
                return;
            }
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {//4.4以后
                path = getPath(this, uri);
                Log.w(TAG,path);
                file = new File(path);
                uploadfile = file.getName();
                if(fileIdType == 1){
                    ft_forFile.setText(uploadfile);
                }
                if(fileIdType == 2){
                    ft_oriFile.setText(uploadfile);
                }
                Toast.makeText(this,path,Toast.LENGTH_SHORT).show();
            } else {//4.4以下下系统调用方法
                path = getRealPathFromURI(this,uri);
                Log.w(TAG,path);
                Toast.makeText(StudentForeignTranslationActivity.this, path+"222222", Toast.LENGTH_SHORT).show();
            }
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
                final ProgressDialog dialog= new ProgressDialog(StudentForeignTranslationActivity.this);
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
                        Toast.makeText(StudentForeignTranslationActivity.this, "下载成功", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(StudentForeignTranslationActivity.this, "下载失败", Toast.LENGTH_SHORT).show();
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