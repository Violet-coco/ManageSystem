package com.manage_system.ui.manage.activity.student;

import android.app.Activity;
import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
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
import com.manage_system.R;
import com.manage_system.net.ApiConstants;
import com.manage_system.utils.DateUtil;
import com.manage_system.utils.DownloadUtil;
import com.manage_system.utils.OkManager;
import com.manage_system.utils.OpenFileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
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

public class StudentGuideReportActivity extends AppCompatActivity implements View.OnClickListener {

    ImageButton iv_back;
    Button guide_record_edit;
    @BindView(R.id.nav_title)
    TextView nav_title;
    @BindView(R.id.gr_state)
    TextView gr_state;
    @BindView(R.id.gr_suggest)
    TextView gr_suggest;
    @BindView(R.id.gr_theme)
    EditText gr_theme;
    @BindView(R.id.gr_date)
    Button gr_date;
    @BindView(R.id.gr_work)
    EditText gr_work;
    @BindView(R.id.gr_annotation)
    EditText gr_annotation;
    @BindView(R.id.gr_annex)
    EditText gr_annex;
    @BindView(R.id.guide_record_annex)
    Button guide_record_annex;
    @BindView(R.id.gr_annotation_main)
    RelativeLayout gr_annotation_main;
    @BindView(R.id.guide_record_submit)
    Button guide_record_submit;
    @BindView(R.id.guide_record_check)
    LinearLayout guide_record_check;
    @BindView(R.id.guide_record_sure)
    Button guide_record_sure;
    @BindView(R.id.gr_check)
    EditText gr_check;
    @BindView(R.id.gr_result)
    Spinner gr_result;
    private String TAG = "指导记录";
    private String fileId = "0";
    private String path,id,sid,theme,work,date,uploadfile;
    private File file;
    private String fileName = null;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ms_student_pd_guide_record);
        ButterKnife.bind(this);
        guide_record_annex.setVisibility(View.GONE);
        guide_record_submit.setVisibility(View.GONE);
        initEditStatus();
        getId();

        SharedPreferences sp=getSharedPreferences("loginInfo", MODE_PRIVATE);
        if(sp.getString("authority","").equals("1")){
            guide_record_check.setVisibility(View.GONE);
            guide_record_sure.setVisibility(View.GONE);
            initData();
        }else if(sp.getString("authority","").equals("2")){
            guide_record_edit.setVisibility(View.GONE);
            initCheckData();
        }
    }

    /**启动这个Activity的Intent
     * @param context
     * @return
     */
    public static Intent createIntent(Context context) {
        return new Intent(context, StudentGuideReportActivity.class);
    }

    public void initEditStatus() {
        gr_theme.setEnabled(false);
        gr_date.setEnabled(false);
        gr_work.setEnabled(false);
        gr_annotation.setEnabled(false);
    }

    public void changeState() {
        Log.w(TAG,"可编辑");
        gr_theme.setEnabled(true);
        gr_theme.setFocusable(true);
        gr_date.setEnabled(true);
        gr_work.setEnabled(true);
        gr_annotation_main.setVisibility(View.GONE);
        guide_record_annex.setVisibility(View.VISIBLE);
        guide_record_edit.setVisibility(View.GONE);
        guide_record_submit.setVisibility(View.VISIBLE);
    }

    /**
     * 获取id
     */
    private void getId()
    {
        iv_back = (ImageButton)findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);
        guide_record_edit = (Button)findViewById(R.id.guide_record_edit);
        guide_record_edit.setOnClickListener(this);
    }

    @OnClick({R.id.guide_record_submit,R.id.gr_date,R.id.guide_record_annex,R.id.gr_annex,R.id.guide_record_sure})
    public void onClick(View v) {//直接调用不会显示v被点击效果
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.guide_record_edit:
                changeState();
                nav_title.setText("修改指导记录");
                break;
            case R.id.guide_record_submit:
                submitData();
                break;
            case R.id.guide_record_sure:
                submitCheckData();
                break;
            case R.id.gr_date:
                showDatePicker();
                break;
            case R.id.guide_record_annex:
                showFileChooser();
                break;
            case R.id.gr_annex:
                downLoad("下载文件","确认下载附件？",fileId);
                break;
            default:
                break;
        }
    }

    public void initData() {
        SharedPreferences sp=getSharedPreferences("processData", MODE_PRIVATE);
        Log.w(TAG,sp.getString("obj" , ""));
        JSONObject obj = JSON.parseObject(sp.getString("guide_record" , ""));
        Log.w(TAG,obj.toString());
        id = obj.getString("id");
        String status = obj.getString("cStatus");
        String cStatus = null;
        Log.w(TAG,status+"喔喔");
        if(status.equals("0")){
            cStatus = "审核不通过";
        }else if(status.equals("1")){
            cStatus = "审核通过";
            guide_record_edit.setVisibility(View.GONE);
        }else if(status.equals("2")|| status.equals("3")){
            cStatus = "审核中";
        }
        gr_state.setText(cStatus);
        gr_suggest.setText(obj.getString("cSuggest"));
        gr_theme.setText(obj.getString("theme"));
        gr_date.setText(DateUtil.getDateFormatNoTime(obj.getString("date")));
        gr_work.setText(obj.getString("work"));
        gr_annotation.setText(obj.getString("annotation"));
        fileId = obj.getString("fileId");
        if(obj.containsKey("file")){
            fileName = obj.getJSONObject("file").getString("fileName");
            gr_annex.setText(Html.fromHtml("<u>"+obj.getJSONObject("file").getString("fileName")+"</u>"));
        }else{
            fileName = obj.getString("title");
            gr_annex.setText("暂无附件");
            gr_annex.setEnabled(false);
        }
    }

    public void initCheckData() {
        SharedPreferences sp=getSharedPreferences("processData", MODE_PRIVATE);
        Log.w(TAG,sp.getString("obj" , ""));
        JSONObject obj = JSON.parseObject(sp.getString("guide_record" , ""));
        Log.w(TAG,obj.toString());
        id = obj.getString("id");
        String status = obj.getString("cStatus");
        String cStatus = null;
        Log.w(TAG,status+"喔喔");
        if(status.equals("0")){
            cStatus = "审核不通过";
            gr_annotation.setEnabled(true);
        }else if(status.equals("1")){
            cStatus = "审核通过";
            guide_record_edit.setVisibility(View.GONE);
            guide_record_submit.setVisibility(View.GONE);
            guide_record_sure.setVisibility(View.GONE);
            gr_suggest.setEnabled(false);
            setSpinnerItemSelectedByValue(gr_result,cStatus);
            gr_result.setEnabled(false);
        }else if(status.equals("2")|| status.equals("3")){
            cStatus = "审核中";
            gr_annotation.setEnabled(true);
        }
        gr_state.setText(cStatus);
        gr_suggest.setText(obj.getString("cSuggest"));
        gr_check.setText(obj.getString("cSuggest"));
        gr_theme.setText(obj.getString("theme"));
        gr_date.setText(DateUtil.getDateFormatNoTime(obj.getString("date")));
        gr_work.setText(obj.getString("work"));
        gr_annotation.setText(obj.getString("annotation"));
        fileId = obj.getString("fileId");
        if(obj.containsKey("file")){
            fileName = obj.getJSONObject("file").getString("fileName");
            gr_annex.setText(Html.fromHtml("<u>"+obj.getJSONObject("file").getString("fileName")+"</u>"));
        }else{
            fileName = obj.getString("title");
            gr_annex.setText("暂无附件");
            gr_annex.setEnabled(false);
        }
    }

    public void submitData() {
        theme=gr_theme.getText().toString().trim();
        work=gr_work.getText().toString().trim();
        date = DateUtil.date2TimeStamp(gr_date.getText().toString().trim());
        Log.w(TAG,date+"哈哈 ");
        uploadfile=gr_annex.getText().toString().trim();
        Log.w(TAG,uploadfile);
        Log.e(TAG,fileId);
        OkManager manager = OkManager.getInstance();
        RequestBody requestBody;
        if(file == null){
            requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("id", id)
                    .addFormDataPart("theme", theme)
                    .addFormDataPart("work", work)
                    .addFormDataPart("date", date)
                    .addFormDataPart("fileId", fileId)
                    .addFormDataPart("uploadfile", uploadfile)
                    .build();
        }else {
            requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("id", id)
                    .addFormDataPart("theme", theme)
                    .addFormDataPart("work", work)
                    .addFormDataPart("date", date)
                    .addFormDataPart("fileId", fileId)
                    .addFormDataPart("uploadfile", uploadfile, RequestBody.create(MediaType.parse("*/*"), file))
                    .build();
        }

        manager.postFile(ApiConstants.studentApi + "/modifyGuidanceRecord", requestBody,new okhttp3.Callback() {
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
                            Toast.makeText(StudentGuideReportActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(StudentGuideReportActivity.this,StudentGuideReportMainActivity.class);
                            startActivity(intent);
                            finish();
                        }else {
                            Toast.makeText(StudentGuideReportActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }

    public void submitCheckData() {
        final Intent intent1 = getIntent();
        String status;
        if(gr_result.getSelectedItem().toString().equals("审核通过")){
            status="1";
        }else{
            status="0";
        }
        OkManager manager = OkManager.getInstance();
        Map<String, String> map = new HashMap<String, String>();
        map.put("sid",intent1.getStringExtra("stu_id"));
        map.put("docId",id);
        map.put("annotation",gr_annotation.getText().toString().trim());
        map.put("status",status);
        map.put("suggest",gr_suggest.getText().toString().trim());
        manager.post(ApiConstants.teacherApi + "/verifyGuidanceRecord", map,new okhttp3.Callback() {
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
                            Toast.makeText(StudentGuideReportActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(StudentGuideReportActivity.this,StudentGuideReportMainActivity.class);
                            intent.putExtra("stu_id",intent1.getStringExtra("stu_id"));
                            startActivity(intent);
                            finish();
                        }else {
                            Toast.makeText(StudentGuideReportActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
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

    public void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(StudentGuideReportActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Log.d(TAG, "onDateSet: year: " + year + ", month: " + month + ", dayOfMonth: " + dayOfMonth);
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        gr_date.setText(DateUtil.date2String(calendar.getTime()));
                        Log.e(TAG,DateUtil.date2String(calendar.getTime()));
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
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
                gr_annex.setText(uploadfile);
                Log.w(TAG,"getName==="+uploadfile);
                Toast.makeText(this,path+"11111",Toast.LENGTH_SHORT).show();
                return;
            }
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {//4.4以后
                path = getPath(this, uri);
                Log.w(TAG,path);
                file = new File(path);
                uploadfile = file.getName();
                gr_annex.setText(uploadfile);
                Log.w(TAG,"getName==="+uploadfile);
                Toast.makeText(this,path,Toast.LENGTH_SHORT).show();
            } else {//4.4以下下系统调用方法
                path = getRealPathFromURI(this,uri);
                Log.w(TAG,path);
                Toast.makeText(StudentGuideReportActivity.this, path+"222222", Toast.LENGTH_SHORT).show();
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
                final ProgressDialog dialog= new ProgressDialog(StudentGuideReportActivity.this);
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
                        Toast.makeText(StudentGuideReportActivity.this, "下载成功", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(StudentGuideReportActivity.this, "下载失败", Toast.LENGTH_SHORT).show();
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