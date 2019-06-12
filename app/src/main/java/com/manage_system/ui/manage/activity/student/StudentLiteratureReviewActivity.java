package com.manage_system.ui.manage.activity.student;

import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.manage_system.LoginActivity;
import com.manage_system.R;
import com.manage_system.net.ApiConstants;
import com.manage_system.ui.manage.activity.teacher.TeacherCheckDataMainActivity;
import com.manage_system.utils.DateUtil;
import com.manage_system.utils.DownloadUtil;
import com.manage_system.utils.OkManager;
import com.manage_system.utils.OpenFileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindInt;
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

public class StudentLiteratureReviewActivity extends AppCompatActivity implements View.OnClickListener {

    ImageButton iv_back;
    @BindView(R.id.nav_title)
    TextView nav_title;
    @BindView(R.id.lr_time)
    TextView lr_time;
    @BindView(R.id.lr_state)
    TextView lr_state;
    @BindView(R.id.lr_intro)
    EditText lr_intro;
    @BindView(R.id.lr_annotation)
    EditText lr_annotation;
    @BindView(R.id.lr_annex)
    TextView lr_annex;
    @BindView(R.id.lite_review_edit)
    Button lite_review_edit;
    @BindView(R.id.lite_review_submit)
    Button lite_review_submit;
    @BindView(R.id.lite_review_check)
    LinearLayout lite_review_check;
    @BindView(R.id.lite_review_annex)
    Button lite_review_annex;
    @BindView(R.id.lr_annotation_main)
    RelativeLayout lr_annotation_main;
    @BindView(R.id.lite_review_sure)
    Button lite_review_sure;
    @BindView(R.id.lr_suggest)
    EditText lr_suggest;
    @BindView(R.id.lr_result)
    Spinner lr_result;
    private String TAG = "文献综述";
    private String intro,uploadfile;
    private String fileId = "0";
    private String path;
    private File file;
    private String fileName = null;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ms_student_pd_litreview);
        ButterKnife.bind(this);
        getId();
        lite_review_annex.setVisibility(View.GONE);
        lite_review_submit.setVisibility(View.GONE);
        initEditStatus();

        SharedPreferences sp=getSharedPreferences("loginInfo", MODE_PRIVATE);
        if(sp.getString("authority","").equals("1")){
            lite_review_check.setVisibility(View.GONE);
            lite_review_sure.setVisibility(View.GONE);
            initData();
        }else{
            lite_review_edit.setVisibility(View.GONE);
            initCheckData();
        }
    }

    /**启动这个Activity的Intent
     * @param context
     * @return
     */
    public static Intent createIntent(Context context) {
        return new Intent(context, StudentLiteratureReviewActivity.class);
    }

    public void initEditStatus() {
        lr_intro.setEnabled(false);
        lr_annotation.setEnabled(false);
        lr_annex.setEnabled(false);
    }

    public void changeState() {
        Log.w(TAG,"可编辑");
        lr_intro.setEnabled(true);
        lr_intro.setFocusable(true);
        lr_annotation_main.setVisibility(View.GONE);
        lite_review_annex.setVisibility(View.VISIBLE);
        lite_review_edit.setVisibility(View.GONE);
        lite_review_submit.setVisibility(View.VISIBLE);
    }


    /**
     * 获取id
     */
    private void getId()
    {
        iv_back = (ImageButton)findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);
    }

    @OnClick({R.id.lite_review_edit,R.id.lite_review_submit,R.id.lite_review_annex,R.id.lr_annex,R.id.lite_review_sure})
    public void onClick(View v) {//直接调用不会显示v被点击效果
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.lite_review_edit:
                nav_title.setText("修改文献综述");
                changeState();
                break;
            case R.id.lite_review_submit:
                submitData();
                break;
            case R.id.lite_review_sure:
                submitCheckData();
                break;
            case R.id.lite_review_annex:
                showFileChooser();
                break;
            case R.id.lr_annex:
                downLoad("下载文件","确认下载附件？",fileId);
                break;
            default:
                break;
        }
    }

    public void initData() {
        OkManager manager = OkManager.getInstance();
        Map<String, String> map = new HashMap<String, String>();
        OkManager.post(ApiConstants.studentApi + "/showLiteratureReview", map,new okhttp3.Callback() {
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
                            lr_time.setText(DateUtil.getDateFormat(object.getString("submitDate")));
                            String status = object.getString("cStatus");
                            String cStatus = null;
                            Log.w(TAG,status+"喔喔");
                            if(status.equals("0")){
                                cStatus = "审核不通过";
                            }else if(status.equals("1")){
                                cStatus = "审核通过";
                                lite_review_submit.setVisibility(View.GONE);
                                lite_review_edit.setVisibility(View.GONE);
                            }else if(status.equals("2")|| status.equals("3")){
                                cStatus = "审核中";
                            }
                            lr_state.setText(cStatus);
                            lr_intro.setText(object.getString("intro"));
                            lr_annotation.setText(object.getString("annotation"));
                            fileId = object.getString("fileId");
                            if(object.containsKey("file")){
                                lr_annex.setEnabled(true);
                                fileName = object.getJSONObject("file").getString("fileName");
                                lr_annex.setText(Html.fromHtml("<u>"+object.getJSONObject("file").getString("fileName")+"</u>"));
                            }else{
                                lr_annex.setText("暂无附件");
                                lr_annex.setEnabled(false);
                                lr_annex.setTextColor(Color.parseColor("#666666"));
                            }

                        }else if(obj.get("statusCode").equals(101)){
                            Toast.makeText(StudentLiteratureReviewActivity.this, msg, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(StudentLiteratureReviewActivity.this,StudentLiteratureReviewEditActivity.class);
                            startActivity(intent);
                            finish();
                        }else if(obj.get("statusCode").equals(102)){
                            Toast.makeText(StudentLiteratureReviewActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(StudentLiteratureReviewActivity.this,LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }else{
                            Toast.makeText(StudentLiteratureReviewActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }

    public void initCheckData() {
        Intent intent = getIntent();
        OkManager manager = OkManager.getInstance();
        Map<String, String> map = new HashMap<String, String>();
        Log.e(TAG,intent.getStringExtra("stu_id"));
        Log.e(TAG,intent.getStringExtra("doc_type"));
        map.put("sid",intent.getStringExtra("stu_id"));
        map.put("docType",intent.getStringExtra("doc_type"));
        OkManager.post(ApiConstants.teacherApi + "/showStudentDocument", map,new okhttp3.Callback() {
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
                            lr_time.setText(DateUtil.getDateFormat(object.getString("submitDate")));
                            String status = object.getString("cStatus");
                            String cStatus = null;
                            Log.w(TAG,status+"喔喔");
                            if(status.equals("0")){
                                cStatus = "审核不通过";
                                lr_annotation.setEnabled(true);
                            }else if(status.equals("1")){
                                cStatus = "审核通过";
                                lite_review_submit.setVisibility(View.GONE);
                                lite_review_sure.setVisibility(View.GONE);
                                lr_suggest.setEnabled(false);
                                setSpinnerItemSelectedByValue(lr_result,cStatus);
                                lr_result.setEnabled(false);
                            }else if(status.equals("2")|| status.equals("3")){
                                cStatus = "审核中";
                                lr_annotation.setEnabled(true);
                            }
                            lr_state.setText(cStatus);
                            lr_intro.setText(object.getString("intro"));
                            lr_annotation.setText(object.getString("annotation"));
                            lr_suggest.setText(object.getString("cSuggest"));
                            fileId = object.getString("fileId");
                            if(object.get("fileId").equals(0)){
                                lr_annex.setText("暂无附件");
                                lr_annex.setEnabled(false);
                            }else{
                                if(!object.getJSONObject("file").getString("fileName").isEmpty()){
                                    fileName = object.getJSONObject("file").getString("fileName");
                                    lr_annex.setText(Html.fromHtml("<u>"+object.getJSONObject("file").getString("fileName")+"</u>"));
                                }else{
                                    fileName = object.getString("title");
                                    lr_annex.setText(Html.fromHtml("<u>"+"中期检查.附件"+"</u>"));
                                }
                            }

                        }else if(obj.get("statusCode").equals(102)){
                            Toast.makeText(StudentLiteratureReviewActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(StudentLiteratureReviewActivity.this,LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }else{
                            Toast.makeText(StudentLiteratureReviewActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }

    public void submitData() {
        intro=lr_intro.getText().toString().trim();
        uploadfile=lr_annex.getText().toString().trim();
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
        OkManager.postFile(ApiConstants.studentApi + "/commitMidInspection", requestBody,new okhttp3.Callback() {
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
                            Toast.makeText(StudentLiteratureReviewActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(StudentLiteratureReviewActivity.this,StudentLiteratureReviewActivity.class);
                            startActivity(intent);
                        }else if(obj.get("statusCode").equals(102)){
                            Toast.makeText(StudentLiteratureReviewActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(StudentLiteratureReviewActivity.this,LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }else {
                            Toast.makeText(StudentLiteratureReviewActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }

    public void submitCheckData() {
        Intent intent = getIntent();
        String status;
        if(lr_result.getSelectedItem().toString().equals("审核通过")){
            status="1";
        }else{
            status="0";
        }
        OkManager manager = OkManager.getInstance();
        Map<String, String> map = new HashMap<String, String>();
        map.put("sid",intent.getStringExtra("stu_id"));
        map.put("docType",intent.getStringExtra("doc_type"));
        map.put("annotation",lr_annotation.getText().toString().trim());
        map.put("status",status);
        map.put("suggest",lr_suggest.getText().toString().trim());
        OkManager.post(ApiConstants.teacherApi + "/verifyDocument", map,new okhttp3.Callback() {
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
                            Toast.makeText(StudentLiteratureReviewActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(StudentLiteratureReviewActivity.this,TeacherCheckDataMainActivity.class);
                            startActivity(intent);
                        }else if(obj.get("statusCode").equals(102)){
                            Toast.makeText(StudentLiteratureReviewActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(StudentLiteratureReviewActivity.this,LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }else {
                            Toast.makeText(StudentLiteratureReviewActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }

    public void setSpinnerItemSelectedByValue(Spinner spinner,String value) {
        SpinnerAdapter apsAdapter = spinner.getAdapter(); //得到SpinnerAdapter对象
        int k = apsAdapter.getCount();
        for (int i = 0; i < k; i++) {
            if (value.equals(apsAdapter.getItem(i).toString())) {
//                spinner.setSelection(i,true);// 默认选中项
                spinner.setSelection(i);// 默认选中项
                break;
            }
        }
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
                lr_annex.setText(uploadfile);
                Log.w(TAG,"getName==="+uploadfile);
                Toast.makeText(this,path+"11111",Toast.LENGTH_SHORT).show();
                return;
            }
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {//4.4以后
                path = getPath(this, uri);
                Log.w(TAG,path);
                file = new File(path);
                uploadfile = file.getName();
                lr_annex.setText(uploadfile);
                Log.w(TAG,"getName==="+uploadfile);
                Toast.makeText(this,path,Toast.LENGTH_SHORT).show();
            } else {//4.4以下下系统调用方法
                path = getRealPathFromURI(this,uri);
                Log.w(TAG,path);
                Toast.makeText(StudentLiteratureReviewActivity.this, path+"222222", Toast.LENGTH_SHORT).show();
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
                final ProgressDialog dialog= new ProgressDialog(StudentLiteratureReviewActivity.this);
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
                        Toast.makeText(StudentLiteratureReviewActivity.this, "下载成功", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(StudentLiteratureReviewActivity.this, "下载失败", Toast.LENGTH_SHORT).show();
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