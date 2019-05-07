package com.manage_system.ui.manage.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
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
import com.manage_system.utils.DateUtil;
import com.manage_system.utils.FileUtils;
import com.manage_system.utils.OkManager;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

import static com.manage_system.utils.FileUtils.getPath;
import static com.manage_system.utils.FileUtils.getRealPathFromURI;

public class StudentOpenReportActivity extends AppCompatActivity implements View.OnClickListener {

    ImageButton iv_back;
    Button open_record_edit;
    private String TAG = "开题报告";
    @BindView(R.id.or_time)
    TextView or_time;
    @BindView(R.id.or_state)
    TextView or_state;
    @BindView(R.id.or_aim)
    EditText or_aim;
    @BindView(R.id.or_content)
    EditText or_content;
    @BindView(R.id.or_tech)
    EditText or_tech;
    @BindView(R.id.or_plan)
    EditText or_plan;
    @BindView(R.id.or_annotation)
    EditText or_annotation;
    @BindView(R.id.or_annex)
    EditText or_annex;
    @BindView(R.id.or_annotation_main)
    RelativeLayout or_annotation_main;
    @BindView(R.id.tm_open_record)
    LinearLayout tm_open_record;
    @BindView(R.id.or_submit_annex)
    Button or_submit_annex;
    @BindView(R.id.open_record_submit)
    Button open_record_submit;
    private String aim,content,tech,plan,uploadfile;
    private String fileId = "0";
    private String path;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ms_student_pd_openrecord);
        ButterKnife.bind(this);
        or_submit_annex.setVisibility(View.GONE);
        tm_open_record.setVisibility(View.GONE);
        open_record_submit.setVisibility(View.GONE);
        getId();
        initEditStatus();
        initData();
        // 申请并获得权限
        if (ContextCompat.checkSelfPermission(StudentOpenReportActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(StudentOpenReportActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE} ,1);
        }
    }

    /**启动这个Activity的Intent
     * @param context
     * @return
     */
    public static Intent createIntent(Context context) {
        return new Intent(context, StudentOpenReportActivity.class);
    }

    /**
     * 获取id
     */
    private void getId()
    {
        iv_back = (ImageButton)findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);
        open_record_edit = (Button)findViewById(R.id.open_record_edit);
        open_record_edit.setOnClickListener(this);
    }

    @OnClick({R.id.open_record_submit,R.id.or_submit_annex})
    public void onClick(View v) {//直接调用不会显示v被点击效果
        switch (v.getId()) {
            case R.id.iv_back:
                onViewClicked();
                break;
            case R.id.open_record_edit:
                Log.w(TAG,"点击修改");
                changeState();
                break;
            case R.id.open_record_submit:
                Log.w(TAG,"点击提交");
                submitData();
                break;
            case R.id.or_submit_annex:
                Log.w(TAG,"选择文件");
                showFileChooser();
                break;
            default:
                break;
        }
    }

    public void initEditStatus() {
        or_time.setEnabled(false);
        or_state.setEnabled(false);
        or_aim.setEnabled(false);
        or_content.setEnabled(false);

        or_tech.setEnabled(false);
        or_plan.setEnabled(false);
        or_annotation.setEnabled(false);
        or_annex.setEnabled(false);
    }

    public void changeState() {
        Log.w(TAG,"可编辑");
        or_aim.setEnabled(true);
        or_aim.setFocusable(true);
        or_content.setEnabled(true);
        or_tech.setEnabled(true);
        or_plan.setEnabled(true);
        or_annotation_main.setVisibility(View.GONE);
        or_annex.setEnabled(true);
        or_submit_annex.setVisibility(View.VISIBLE);
        open_record_edit.setVisibility(View.GONE);
        open_record_submit.setVisibility(View.VISIBLE);
    }

    public void initData() {
        OkManager manager = OkManager.getInstance();
        Map<String, String> map = new HashMap<String, String>();
        manager.post(ApiConstants.studentApi + "/showOpeningReport", map,new okhttp3.Callback() {
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
                            or_time.setText(DateUtil.getDateFormat(object.getString("submitDate")));
                            String status = object.getString("cStatus");
                            String cStatus = null;
                            Log.w(TAG,status+"喔喔");
                            if(status.equals("0")){
                                cStatus = "审核不通过";
                            }else if(status.equals("1")){
                                cStatus = "审核通过";
                            }else if(status.equals("2")|| status.equals("3")){
                                cStatus = "审核中";
                            }
                            or_state.setText(cStatus);
                            or_aim.setText(object.getString("aim"));
                            or_content.setText(object.getString("content"));
                            or_tech.setText(object.getString("tech"));
                            or_plan.setText(object.getString("plan"));
                            or_annotation.setText(object.getString("annotation"));
                            fileId = object.getString("fileId");
                            if(object.get("fileId").equals(0)){
                                or_annex.setText("暂无附件");
                                or_annex.setEnabled(false);
                            }else{
                                or_annex.setText(Html.fromHtml("<u>"+"开题报告.附件"+"</u>"));
                            }

                        }else if(obj.get("statusCode").equals(101)){
                            Intent intent = new Intent(StudentOpenReportActivity.this,StudentOpenReportEditActivity.class);
                            startActivity(intent);
                        }else{
                            Toast.makeText(StudentOpenReportActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }

    public void submitData() {
        aim=or_aim.getText().toString().trim();
        content=or_content.getText().toString().trim();
        tech=or_tech.getText().toString().trim();
        plan=or_plan.getText().toString().trim();
//        uploadfile=or_annex.getText().toString().trim();
        Log.w(TAG,"hhh1:"+aim);
        Log.w(TAG,"hhh2:"+content);
        Log.w(TAG,"hhh3:"+tech);
        Log.w(TAG,"hhh4:"+plan);
        Log.w(TAG,"hhh5:"+uploadfile);
        OkManager manager = OkManager.getInstance();
        Map<String, String> map = new HashMap<String, String>();
        map.put("aim",aim);
        map.put("content",content);
        map.put("tech",tech);
        map.put("plan",plan);
        map.put("uploadfile",uploadfile);
        map.put("fileId",fileId);
        manager.postFile(ApiConstants.studentApi + "/commitOpeningReport", map,new okhttp3.Callback() {
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
                            Toast.makeText(StudentOpenReportActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(StudentOpenReportActivity.this,StudentOpenReportActivity.class);
                            startActivity(intent);
                        }else {
                            Toast.makeText(StudentOpenReportActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
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
                File file = new File(path);
                uploadfile = file.getName();
                or_annex.setText(uploadfile);
                Log.w(TAG,"getName==="+uploadfile);
                Toast.makeText(this,path+"11111",Toast.LENGTH_SHORT).show();
                return;
            }
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {//4.4以后
                path = getPath(this, uri);
                Log.w(TAG,path);
                File file = new File(path);
                uploadfile = file.getName();
                or_annex.setText(uploadfile);
                Log.w(TAG,"getName==="+uploadfile);
                Toast.makeText(this,path,Toast.LENGTH_SHORT).show();
            } else {//4.4以下下系统调用方法
                path = getRealPathFromURI(this,uri);
                Log.w(TAG,path);
                Toast.makeText(StudentOpenReportActivity.this, path+"222222", Toast.LENGTH_SHORT).show();
            }
        }

    }


    public void onViewClicked() {
        finish();
    }
}