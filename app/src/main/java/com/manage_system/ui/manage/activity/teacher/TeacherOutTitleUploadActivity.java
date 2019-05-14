package com.manage_system.ui.manage.activity.teacher;

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
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.manage_system.R;
import com.manage_system.net.ApiConstants;
import com.manage_system.ui.manage.activity.student.StudentForeignTranslationEditActivity;
import com.manage_system.ui.manage.activity.student.StudentGraduationThesisEditActivity;
import com.manage_system.ui.manage.activity.student.StudentGraduationThesisMainActivity;
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

public class TeacherOutTitleUploadActivity extends AppCompatActivity implements View.OnClickListener {

    private String id = null;
    private String task_fileId,fileId;
    private Context mContext;
    private String fileName = null;

    @BindView(R.id.ot_title)
    EditText ot_title;
    @BindView(R.id.ot_intro)
    EditText ot_intro;
    @BindView(R.id.ot_source)
    Spinner ot_source;
    @BindView(R.id.ot_type)
    Spinner ot_type;
    @BindView(R.id.ot_able)
    Spinner ot_able;
    @BindView(R.id.ot_range)
    Spinner ot_range;
    @BindView(R.id.ot_taskBook)
    TextView ot_taskBook;
    @BindView(R.id.ot_referData)
    EditText ot_referData;
    @BindView(R.id.ot_task)
    EditText ot_task;
    @BindView(R.id.ot_annex)
    TextView ot_annex;
    @BindView(R.id.up_main)
    LinearLayout up_main;
    private static String TAG = "上传选题界面";
    private File file,file1;
    private String path;
    private int uploadType = 0;
    private int fileIdType = 0;
    @Nullable
    private String briefIntro,uploadFile,uploadTaskBookFile;
    private String title,rangeType,genre,source,number,task,referData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ms_tm_upload_topic);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        String method = intent.getStringExtra("method");
        if(method.equals("upload")){
            Log.w(TAG,"上传题目");
        }else{
            uploadType = 1;
            if(method.equals("edit_task")){
                up_main.setVisibility(View.GONE);
            }
            initData();
        }

    }
        /**启动这个Activity的Intent
         * @param context
         * @return
         */
    public static Intent createIntent(Context context) {
        return new Intent(context, TeacherOutTitleUploadActivity.class);
    }

    @OnClick({R.id.iv_back,R.id.ot_taskBook,R.id.ot_item_submit,R.id.ot_taskBook_submit,R.id.ot_annex_submit})
    public void onClick(View v) {//直接调用不会显示v被点击效果
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.ot_taskBook:
                downLoad("下载文件","确认下载任务书？",task_fileId);
                break;
            case R.id.ot_item_submit:
                submitData();
                break;
            case R.id.ot_annex_submit:
                fileIdType = 1;
                showFileChooser();
                break;
            case R.id.ot_taskBook_submit:
                fileIdType = 2;
                showFileChooser();
                break;
            default:
                break;
        }
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

    public void initData() {
        SharedPreferences sp=getSharedPreferences("processData", MODE_PRIVATE);
        JSONObject obj = JSON.parseObject(sp.getString("teacher_outTitle_detail" , ""));
        Log.e(TAG,obj.toString());
        JSONObject project = obj.getJSONObject("project");
        id = project.getString("id");
        ot_title.setText(project.getString("title"));
        setSpinnerItemSelectedByValue(ot_source,project.getString("source"));
        setSpinnerItemSelectedByValue(ot_type,project.getString("genre"));
        setSpinnerItemSelectedByValue(ot_range,project.getString("range"));
        setSpinnerItemSelectedByValue(ot_able,project.getString("rest"));
        ot_intro.setText(project.getString("briefIntro"));
        fileId = project.getString("fileId");
        ot_task.setText(project.getJSONObject("taskBook").getString("task"));
        ot_referData.setText(project.getJSONObject("taskBook").getString("referData"));
        if(project.containsKey("file")){
            ot_annex.setText(Html.fromHtml("<u>"+project.getJSONObject("file").getString("fileName")+"</u>"));
        }else{
            ot_annex.setEnabled(false);
            ot_annex.setText("暂无附件");
            ot_taskBook.setTextColor(Color.parseColor("#666666"));
        }
        if(project.containsKey("taskBook")){
            ot_taskBook.setEnabled(false);
            ot_taskBook.setText("暂无任务书");
            ot_taskBook.setTextColor(Color.parseColor("#666666"));
            task_fileId = project.getJSONObject("taskBook").getString("fileId");
            if(project.getJSONObject("taskBook").containsKey("file")){
                ot_taskBook.setEnabled(true);
                ot_taskBook.setTextColor(Color.BLUE);
                ot_taskBook.setText(Html.fromHtml("<u>"+project.getJSONObject("taskBook").getJSONObject("file").getString("fileName")+"</u>"));
            }

        }else{
            ot_taskBook.setEnabled(false);
            ot_taskBook.setText("暂无任务书");
            ot_taskBook.setTextColor(Color.parseColor("#666666"));
        }
    }

    public void submitData() {
        title=ot_title.getText().toString().trim();
        if(ot_range.getSelectedItem().toString().equals("本系")){
            rangeType="0";
        }else{
            rangeType="1";
        }

        genre=ot_type.getSelectedItem().toString();
        source=ot_source.getSelectedItem().toString();
        number=ot_able.getSelectedItem().toString();

        Log.w(TAG,ot_range.getSelectedItem().toString());
        Log.w(TAG,ot_type.getSelectedItem().toString());
        Log.w(TAG,ot_source.getSelectedItem().toString());
        Log.w(TAG,ot_able.getSelectedItem().toString());

        briefIntro=ot_intro.getText().toString().trim();
        //  任务和要求
        task=ot_task.getText().toString().trim();
        // 参考资料
        referData=ot_referData.getText().toString().trim();
        uploadFile=ot_annex.getText().toString().trim();
        uploadTaskBookFile=ot_taskBook.getText().toString().trim();
        Log.e(TAG,uploadFile);
        Log.e(TAG,uploadTaskBookFile);
        OkManager manager = OkManager.getInstance();
        // 接口名称
        String api;
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM)
                .addFormDataPart("title", title)
                .addFormDataPart("rangeType", rangeType)
                .addFormDataPart("genre", genre)
                .addFormDataPart("source", source)
                .addFormDataPart("briefIntro", briefIntro)
                .addFormDataPart("number", number)
                .addFormDataPart("task", task)
                .addFormDataPart("referData", referData);
        if(uploadType == 1){
            builder.addFormDataPart("id", id)
                    .addFormDataPart("fileId", fileId);
//                任务书id
//                .addFormDataPart("task_fileId", task_fileId)
            api = "/modifyProject";
        }else{
            api = "/issueProject";
        }
        if(file == null || file1 == null){
            if(file == null&& file1 !=null){
                builder.addFormDataPart("uploadFile", uploadFile)
                        .addFormDataPart("uploadTaskBookFile", uploadTaskBookFile,RequestBody.create(MediaType.parse("*/*"),file1))
                        .build();
            }else if(file1 == null&&file!=null){
                builder.addFormDataPart("uploadFile", uploadFile,RequestBody.create(MediaType.parse("*/*"), file))
                        .addFormDataPart("uploadTaskBookFile", uploadTaskBookFile)
                        .build();
            }else{
                builder.addFormDataPart("uploadFile", uploadFile)
                        .addFormDataPart("uploadTaskBookFile", uploadTaskBookFile)
                        .build();
            }
        }else{
            builder.addFormDataPart("uploadFile", uploadFile,RequestBody.create(MediaType.parse("*/*"), file))
                    .addFormDataPart("uploadTaskBookFile", uploadTaskBookFile,RequestBody.create(MediaType.parse("*/*"), file1))
                    .build();
        }
        RequestBody requestBody = builder.build();

        manager.postFile(ApiConstants.teacherApi + api, requestBody,new okhttp3.Callback() {
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
                            Toast.makeText(TeacherOutTitleUploadActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(TeacherOutTitleUploadActivity.this,TeacherOutTitleMainActivity.class);
                            startActivity(intent);
                            finish();
                        }else {
                            Toast.makeText(TeacherOutTitleUploadActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
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
                if(fileIdType == 1){
                    file = new File(path);
                    ot_annex.setText(file.getName());
                }
                if(fileIdType == 2){
                    file1 = new File(path);
                    ot_taskBook.setText(file1.getName());
                }
                Toast.makeText(this,path+"11111",Toast.LENGTH_SHORT).show();
                return;
            }
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {//4.4以后
                path = getPath(this, uri);
                Log.w(TAG,path);
                if(fileIdType == 1){
                    file = new File(path);
                    ot_annex.setText(file.getName());
                }
                if(fileIdType == 2){
                    file1 = new File(path);
                    ot_taskBook.setText(file1.getName());
                }
                Toast.makeText(this,path,Toast.LENGTH_SHORT).show();
            } else {//4.4以下下系统调用方法
                path = getRealPathFromURI(this,uri);
                Log.w(TAG,path);
                Toast.makeText(TeacherOutTitleUploadActivity.this, path+"222222", Toast.LENGTH_SHORT).show();
            }
        }

    }

    public void downLoad(String title,String message,final String fileId){
        final Dialog dialog = new Dialog(TeacherOutTitleUploadActivity.this, R.style.MyDialog);
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
                final ProgressDialog dialog= new ProgressDialog(TeacherOutTitleUploadActivity.this);
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
        DownloadUtil.get().download(TeacherOutTitleUploadActivity.this, ApiConstants.commonApi + "/downloadFile"+"?fileId="+fileId,saveurl,fileName,  new DownloadUtil.OnDownloadListener() {
            @Override
            public void onDownloadSuccess() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getNotificationManager().notify(1,getNotification("文件下载成功，点击进行查看",-1));
                        Toast.makeText(TeacherOutTitleUploadActivity.this, "下载成功", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(TeacherOutTitleUploadActivity.this, "下载失败", Toast.LENGTH_SHORT).show();
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
        NotificationCompat.Builder builder = new NotificationCompat.Builder(TeacherOutTitleUploadActivity.this);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle(title);
        if (progress > 0) {
            builder.setContentText(progress+"%");
            builder.setProgress(100,progress,false);
        }
        return builder.build();
    }
}