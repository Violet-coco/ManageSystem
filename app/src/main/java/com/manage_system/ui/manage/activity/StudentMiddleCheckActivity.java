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

public class StudentMiddleCheckActivity extends AppCompatActivity implements View.OnClickListener {

    ImageButton iv_back;
    Button middle_check_edit;
    @BindView(R.id.nav_title)
    TextView nav_title;
    @BindView(R.id.mc_time)
    TextView mc_time;
    @BindView(R.id.mc_state)
    TextView mc_state;
    @BindView(R.id.mc_intro)
    EditText mc_intro;
    @BindView(R.id.mc_annotation)
    EditText mc_annotation;
    @BindView(R.id.mc_annex)
    EditText mc_annex;
    @BindView(R.id.tm_middle_check)
    LinearLayout tm_middle_check;
    @BindView(R.id.mc_submit_annex)
    Button mc_submit_annex;
    @BindView(R.id.middle_check_submit)
    Button middle_check_submit;
    @BindView(R.id.mc_annotation_main)
    RelativeLayout mc_annotation_main;
    private String TAG = "中期检查";
    private String intro,uploadfile;
    private String fileId = "0";
    private String path;
    private File file;
    private String fileName = null;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ms_student_pd_middlecheck);
        ButterKnife.bind(this);
        mc_submit_annex.setVisibility(View.GONE);
        tm_middle_check.setVisibility(View.GONE);
        middle_check_submit.setVisibility(View.GONE);
        getId();
        initEditStatus();
        initData();
    }

    /**启动这个Activity的Intent
     * @param context
     * @return
     */
    public static Intent createIntent(Context context) {
        return new Intent(context, StudentMiddleCheckActivity.class);
    }

    /**
     * 获取id
     */
    private void getId()
    {
        iv_back = (ImageButton)findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);
        middle_check_edit = (Button)findViewById(R.id.middle_check_edit);
        middle_check_edit.setOnClickListener(this);
    }

    @OnClick({R.id.middle_check_submit,R.id.mc_submit_annex,R.id.mc_annex})
    public void onClick(View v) {//直接调用不会显示v被点击效果
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.middle_check_edit:
                changeState();
                break;
            case R.id.middle_check_submit:
                nav_title.setText("修改中期检查");
                submitData();
                break;
            case R.id.mc_submit_annex:
                showFileChooser();
                break;
            case R.id.mc_annex:
                downLoad("下载文件","确认下载附件？",fileId);
                break;
            default:
                break;
        }
    }

    public void initEditStatus() {
        mc_time.setEnabled(false);
        mc_state.setEnabled(false);
        mc_intro.setEnabled(false);
        mc_annotation.setEnabled(false);
        mc_annex.setEnabled(false);
    }

    public void changeState() {
        Log.w(TAG,"可编辑");
        mc_intro.setEnabled(true);
        mc_intro.setFocusable(true);
        mc_annotation_main.setVisibility(View.GONE);
        mc_submit_annex.setVisibility(View.VISIBLE);
        middle_check_edit.setVisibility(View.GONE);
        middle_check_submit.setVisibility(View.VISIBLE);
    }

    public void initData() {
        OkManager manager = OkManager.getInstance();
        Map<String, String> map = new HashMap<String, String>();
        manager.post(ApiConstants.studentApi + "/showMidInspection", map,new okhttp3.Callback() {
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
                            mc_time.setText(DateUtil.getDateFormat(object.getString("submitDate")));
                            String status = object.getString("cStatus");
                            String cStatus = null;
                            Log.w(TAG,status+"喔喔");
                            if(status.equals("0")){
                                cStatus = "审核不通过";
                            }else if(status.equals("1")){
                                cStatus = "审核通过";
                                middle_check_submit.setVisibility(View.GONE);
                            }else if(status.equals("2")|| status.equals("3")){
                                cStatus = "审核中";
                            }
                            mc_state.setText(cStatus);
                            mc_intro.setText(object.getString("intro"));
                            mc_annotation.setText(object.getString("annotation"));
                            fileId = object.getString("fileId");
                            if(object.containsKey("file")){
                                fileName = object.getJSONObject("file").getString("fileName");
                                mc_annex.setText(Html.fromHtml("<u>"+object.getJSONObject("file").getString("fileName")+"</u>"));
                            }else{
                                fileName = object.getString("title");
                                mc_annex.setEnabled(false);
                                mc_annex.setText("暂无附件");
                            }

                        }else if(obj.get("statusCode").equals(101)){
                            Toast.makeText(StudentMiddleCheckActivity.this, msg, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(StudentMiddleCheckActivity.this,StudentMiddleCheckEditActivity.class);
                            startActivity(intent);
                        }else{
                            Toast.makeText(StudentMiddleCheckActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }

    public void submitData() {
        intro=mc_intro.getText().toString().trim();
        uploadfile=mc_annex.getText().toString().trim();
        OkManager manager = OkManager.getInstance();
        RequestBody requestBody;
        if(file == null){
            requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("intro", intro) // 提交普通字段
                    .addFormDataPart("fileId", fileId) // 提交普通字段
                    .addFormDataPart("uploadfile", uploadfile)
                    .build();
        }else{
            requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("intro", intro) // 提交普通字段
                    .addFormDataPart("fileId", fileId) // 提交普通字段
                    .addFormDataPart("uploadfile", uploadfile, RequestBody.create(MediaType.parse("*/*"), file))
                    .build();
        }

        manager.postFile(ApiConstants.studentApi + "/commitMidInspection", requestBody,new okhttp3.Callback() {
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
                            Toast.makeText(StudentMiddleCheckActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(StudentMiddleCheckActivity.this,StudentMiddleCheckActivity.class);
                            startActivity(intent);
                        }else {
                            Toast.makeText(StudentMiddleCheckActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
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
                mc_annex.setText(uploadfile);
                Log.w(TAG,"getName==="+uploadfile);
                Toast.makeText(this,path+"11111",Toast.LENGTH_SHORT).show();
                return;
            }
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {//4.4以后
                path = getPath(this, uri);
                Log.w(TAG,path);
                file = new File(path);
                uploadfile = file.getName();
                mc_annex.setText(uploadfile);
                Log.w(TAG,"getName==="+uploadfile);
                Toast.makeText(this,path,Toast.LENGTH_SHORT).show();
            } else {//4.4以下下系统调用方法
                path = getRealPathFromURI(this,uri);
                Log.w(TAG,path);
                Toast.makeText(StudentMiddleCheckActivity.this, path+"222222", Toast.LENGTH_SHORT).show();
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
                final ProgressDialog dialog= new ProgressDialog(StudentMiddleCheckActivity.this);
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
                        Toast.makeText(StudentMiddleCheckActivity.this, "下载成功", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(StudentMiddleCheckActivity.this, "下载失败", Toast.LENGTH_SHORT).show();
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