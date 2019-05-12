package com.manage_system.ui.manage.activity.student;

import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.manage_system.R;
import com.manage_system.net.ApiConstants;
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

public class StudentGraduationThesisActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.nav_title)
    TextView nav_title;
    @BindView(R.id.gt_state)
    TextView gt_state;
    @BindView(R.id.gt_suggest)
    TextView gt_suggest;
    @BindView(R.id.gt_keywords)
    EditText gt_keywords;
    @BindView(R.id.gt_innovatePoint)
    EditText gt_innovatePoint;
    @BindView(R.id.gt_cnSummary)
    EditText gt_cnSummary;
    @BindView(R.id.gt_enSummary)
    EditText gt_enSummary;
    @BindView(R.id.gt_annotation)
    EditText gt_annotation;
    @BindView(R.id.gt_other)
    EditText gt_other;
    @BindView(R.id.gt_word)
    EditText gt_word;
    @BindView(R.id.gt_annex)
    EditText gt_annex;
    @BindView(R.id.gt_word_submit)
    Button gt_word_submit;
    @BindView(R.id.gt_annex_submit)
    Button gt_annex_submit;
    @BindView(R.id.graduation_thesis_edit)
    Button graduation_thesis_edit;
    @BindView(R.id.graduation_thesis_submit)
    Button graduation_thesis_submit;
    @BindView(R.id.tm_check_main)
    LinearLayout tm_check_main;
    @BindView(R.id.gt_annotation_main)
    RelativeLayout gt_annotation_main;
    @BindView(R.id.graduation_thesis_sure)
    Button graduation_thesis_sure;
    @BindView(R.id.gt_check)
    EditText gt_check;
    @BindView(R.id.gt_result)
    Spinner gt_result;
    private String TAG = "毕业论文展示";
    private String id,fileName,path,uploadfile;
    private String fileId,docFileId;
    private int fileIdType = 0;
    private File file;
    private Context mContext;
    private String keywords,innovatePoint,cnSummary,enSummary,other,uploadDocFile,uploadAttFile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ms_student_pd_graduation_thesis);
        ButterKnife.bind(this);
        gt_word_submit.setVisibility(View.GONE);
        gt_annex_submit.setVisibility(View.GONE);
        graduation_thesis_submit.setVisibility(View.GONE);
        initEditStatus();

        SharedPreferences sp=getSharedPreferences("loginInfo", MODE_PRIVATE);
        if(sp.getString("authority","").equals("1")){
            tm_check_main.setVisibility(View.GONE);
            graduation_thesis_sure.setVisibility(View.GONE);
            initData();
        }else{
            graduation_thesis_edit.setVisibility(View.GONE);
            initCheckData();
        }
    }

    /**启动这个Activity的Intent
     * @param context
     * @return
     */
    public static Intent createIntent(Context context) {
        return new Intent(context, StudentGraduationThesisActivity.class);
    }

    public void initEditStatus() {
        gt_keywords.setEnabled(false);
        gt_innovatePoint.setEnabled(false);
        gt_cnSummary.setEnabled(false);
        gt_enSummary.setEnabled(false);
        gt_other.setEnabled(false);
    }

    public void changeState() {
        Log.w(TAG,"可编辑");
        gt_keywords.setEnabled(true);
        gt_keywords.setFocusable(true);
        gt_innovatePoint.setEnabled(true);
        gt_cnSummary.setEnabled(true);
        gt_enSummary.setEnabled(true);
        gt_other.setEnabled(true);
        gt_annotation_main.setVisibility(View.GONE);
        gt_word_submit.setVisibility(View.VISIBLE);
        gt_annex_submit.setVisibility(View.VISIBLE);
        graduation_thesis_edit.setVisibility(View.GONE);
        graduation_thesis_submit.setVisibility(View.VISIBLE);
    }

    @OnClick({R.id.iv_back,R.id.graduation_thesis_edit,R.id.graduation_thesis_submit,R.id.gt_word_submit,R.id.gt_annex_submit,R.id.gt_word,R.id.gt_annex,R.id.graduation_thesis_sure})
    public void onClick(View v) {//直接调用不会显示v被点击效果
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.graduation_thesis_edit:
                changeState();
                nav_title.setText("修改毕业论文");
                break;
            case R.id.graduation_thesis_submit:
                submitData();
                break;
            case R.id.graduation_thesis_sure:
                submitCheckData();
                break;
            case R.id.gt_word_submit:
                fileIdType = 1;
                showFileChooser();
                break;
            case R.id.gt_annex_submit:
                fileIdType = 2;
                showFileChooser();
                break;
            case R.id.gt_word:
                downLoad("下载文件","确认下载文档？",docFileId);
                break;
            case R.id.gt_annex:
                downLoad("下载文件","确认下载附件？",fileId);
                break;
            default:
                break;
        }
    }

    public void initData() {
        SharedPreferences sp=getSharedPreferences("processData", MODE_PRIVATE);
        Log.w(TAG,sp.getString("obj" , ""));
        JSONObject obj = JSON.parseObject(sp.getString("graduation_thesis" , ""));
        Log.w(TAG,obj.toString());
        id = obj.getString("id");
        String status = obj.getString("cStatus");
        String cStatus = null;
        Log.w(TAG,status+"喔喔");
        if(status.equals("0")){
            cStatus = "审核不通过";
            graduation_thesis_edit.setVisibility(View.GONE);
        }else if(status.equals("1")){
            cStatus = "审核通过";
            graduation_thesis_edit.setVisibility(View.GONE);
        }else if(status.equals("2")|| status.equals("3")){
            cStatus = "审核中";
        }
        gt_state.setText(cStatus);
        gt_suggest.setText(obj.getString("cSuggest"));
        gt_keywords.setText(obj.getString("keywords"));
        gt_innovatePoint.setText(obj.getString("innovatePoint"));
        gt_cnSummary.setText(obj.getString("cnSummary"));
        gt_enSummary.setText(obj.getString("enSummary"));
        gt_annotation.setText(obj.getString("annotation"));
        gt_other.setText(obj.getString("other"));
        fileId = obj.getString("fileId");
        docFileId = obj.getString("docFileId");
        if(obj.containsKey("docFile")){
            fileName = obj.getJSONObject("docFile").getString("fileName");
            gt_word.setText(Html.fromHtml("<u>"+obj.getJSONObject("file").getString("fileName")+"</u>"));
        }else{
            gt_word.setText("暂无附件");
            gt_word.setEnabled(false);
        }
        if(obj.containsKey("file")){
            fileName = obj.getJSONObject("file").getString("fileName");
            gt_annex.setText(Html.fromHtml("<u>"+obj.getJSONObject("file").getString("fileName")+"</u>"));
        }else{
            gt_annex.setText("暂无附件");
            gt_annex.setEnabled(false);
        }
    }

    public void initCheckData() {
        SharedPreferences sp=getSharedPreferences("processData", MODE_PRIVATE);
        Log.w(TAG,sp.getString("obj" , ""));
        JSONObject obj = JSON.parseObject(sp.getString("graduation_thesis" , ""));
        Log.w(TAG,obj.toString());
        id = obj.getString("id");
        String status = obj.getString("cStatus");
        String cStatus = null;
        Log.w(TAG,status+"喔喔");
        if(status.equals("0")){
            cStatus = "审核不通过";
            graduation_thesis_edit.setVisibility(View.GONE);
            gt_annotation.setEnabled(true);
        }else if(status.equals("1")){
            cStatus = "审核通过";
            graduation_thesis_edit.setVisibility(View.GONE);
            graduation_thesis_submit.setVisibility(View.GONE);
            graduation_thesis_sure.setVisibility(View.GONE);
            gt_suggest.setEnabled(false);
            setSpinnerItemSelectedByValue(gt_result,cStatus);
            gt_result.setEnabled(false);
        }else if(status.equals("2")|| status.equals("3")){
            cStatus = "审核中";
            gt_annotation.setEnabled(true);
        }
        gt_state.setText(cStatus);
        gt_suggest.setText(obj.getString("cSuggest"));
        gt_check.setText(obj.getString("cSuggest"));
        gt_keywords.setText(obj.getString("keywords"));
        gt_innovatePoint.setText(obj.getString("innovatePoint"));
        gt_cnSummary.setText(obj.getString("cnSummary"));
        gt_enSummary.setText(obj.getString("enSummary"));
        gt_annotation.setText(obj.getString("annotation"));
        gt_other.setText(obj.getString("other"));
        fileId = obj.getString("fileId");
        docFileId = obj.getString("docFileId");
        if(obj.containsKey("docFile")){
            fileName = obj.getJSONObject("docFile").getString("fileName");
            gt_word.setText(Html.fromHtml("<u>"+obj.getJSONObject("file").getString("fileName")+"</u>"));
        }else{
            gt_word.setText("暂无附件");
            gt_word.setEnabled(false);
        }
        if(obj.containsKey("file")){
            fileName = obj.getJSONObject("file").getString("fileName");
            gt_annex.setText(Html.fromHtml("<u>"+obj.getJSONObject("file").getString("fileName")+"</u>"));
        }else{
            gt_annex.setText("暂无附件");
            gt_annex.setEnabled(false);
        }
    }

    public void submitData() {
        keywords=gt_keywords.getText().toString().trim();
        innovatePoint=gt_innovatePoint.getText().toString().trim();
        cnSummary=gt_cnSummary.getText().toString().trim();
        enSummary=gt_enSummary.getText().toString().trim();
        other=gt_other.getText().toString().trim();
        uploadDocFile=gt_word.getText().toString().trim();
        uploadAttFile=gt_annex.getText().toString().trim();
        OkManager manager = OkManager.getInstance();
        RequestBody requestBody;
        if(file == null){
            requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("id", id)
                    .addFormDataPart("keywords", keywords)
                    .addFormDataPart("innovatePoint", innovatePoint)
                    .addFormDataPart("cnSummary", cnSummary)
                    .addFormDataPart("enSummary", enSummary)
                    .addFormDataPart("other", other)
                    .addFormDataPart("docFileId", docFileId)
                    .addFormDataPart("fileId", fileId)
                    .addFormDataPart("uploadDocFile", uploadDocFile)
                    .addFormDataPart("uploadAttFile", uploadAttFile)
                    .build();
        }else{
            requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("id", id)
                    .addFormDataPart("keywords", keywords)
                    .addFormDataPart("innovatePoint", innovatePoint)
                    .addFormDataPart("cnSummary", cnSummary)
                    .addFormDataPart("enSummary", enSummary)
                    .addFormDataPart("other", other)
                    .addFormDataPart("docFileId", docFileId)
                    .addFormDataPart("fileId", fileId)
                    .addFormDataPart("uploadDocFile", uploadDocFile, RequestBody.create(MediaType.parse("*/*"), file))
                    .addFormDataPart("uploadAttFile", uploadAttFile, RequestBody.create(MediaType.parse("*/*"), file))
                    .build();
        }

        manager.postFile(ApiConstants.studentApi + "/modifyGraduationProject", requestBody,new okhttp3.Callback() {
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
                            Toast.makeText(StudentGraduationThesisActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(StudentGraduationThesisActivity.this,StudentGraduationThesisMainActivity.class);
                            startActivity(intent);
                        }else {
                            Toast.makeText(StudentGraduationThesisActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }

    public void submitCheckData() {
        final Intent intent1 = getIntent();
        String status;
        if(gt_result.getSelectedItem().toString().equals("审核通过")){
            status="1";
        }else{
            status="0";
        }
        OkManager manager = OkManager.getInstance();
        Map<String, String> map = new HashMap<String, String>();
        map.put("sid",intent1.getStringExtra("stu_id"));
        map.put("docId",id);
        map.put("annotation",gt_annotation.getText().toString().trim());
        map.put("status",status);
        map.put("suggest",gt_suggest.getText().toString().trim());
        manager.post(ApiConstants.teacherApi + "/verifyGraduationProject", map,new okhttp3.Callback() {
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
                            Toast.makeText(StudentGraduationThesisActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(StudentGraduationThesisActivity.this,StudentGraduationThesisMainActivity.class);
                            intent.putExtra("stu_id",intent1.getStringExtra("stu_id"));
                            startActivity(intent);
                        }else {
                            Toast.makeText(StudentGraduationThesisActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
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
                if(fileIdType == 1){
                    gt_word.setText(uploadfile);
                }
                if(fileIdType == 2){
                    gt_annex.setText(uploadfile);
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
                    gt_word.setText(uploadfile);
                }
                if(fileIdType == 2){
                    gt_annex.setText(uploadfile);
                }
                Toast.makeText(this,path,Toast.LENGTH_SHORT).show();
            } else {//4.4以下下系统调用方法
                path = getRealPathFromURI(this,uri);
                Log.w(TAG,path);
                Toast.makeText(StudentGraduationThesisActivity.this, path+"222222", Toast.LENGTH_SHORT).show();
            }
        }

    }

    public void downLoad(String title,String message,final String fileId){
        Log.w(TAG,"下载文档"+fileId);
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
                final ProgressDialog dialog= new ProgressDialog(StudentGraduationThesisActivity.this);
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
                        Toast.makeText(StudentGraduationThesisActivity.this, "下载成功", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(StudentGraduationThesisActivity.this, "下载失败", Toast.LENGTH_SHORT).show();
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