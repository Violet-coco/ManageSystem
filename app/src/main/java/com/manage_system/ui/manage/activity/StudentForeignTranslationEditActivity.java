package com.manage_system.ui.manage.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.manage_system.R;
import com.manage_system.net.ApiConstants;
import com.manage_system.utils.OkManager;

import java.io.File;
import java.io.IOException;

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

public class StudentForeignTranslationEditActivity extends AppCompatActivity implements View.OnClickListener {

    ImageButton iv_back;
    @BindView(R.id.ft_foreign)
    EditText ft_foreign;
    @BindView(R.id.ft_original)
    EditText ft_original;
    @BindView(R.id.ft_forFile)
    EditText ft_forFile;
    @BindView(R.id.ft_oriFile)
    EditText ft_oriFile;
    private int fileIdType = 0;
    private String TAG = "外文译文和原文修改";
    private String path;
    private File file;
    private String original,foreign,uploadOrifile,uploadForfile,uploadfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ms_student_pd_fortra_edit);
        ButterKnife.bind(this);
        getId();
    }

    /**启动这个Activity的Intent
     * @param context
     * @return
     */
    public static Intent createIntent(Context context) {
        return new Intent(context, StudentForeignTranslationEditActivity.class);
    }

    /**
     * 获取id
     */
    private void getId()
    {
        iv_back = (ImageButton)findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);
    }

    @OnClick({R.id.ft_submit_for_annex,R.id.ft_submit_ori_annex,R.id.for_tra_submit})
    public void onClick(View v) {//直接调用不会显示v被点击效果
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
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
            default:
                break;
        }
    }

    public void submitData() {
        original=ft_foreign.getText().toString().trim();
        foreign=ft_original.getText().toString().trim();
        uploadOrifile=ft_forFile.getText().toString().trim();
        uploadForfile=ft_oriFile.getText().toString().trim();
        OkManager manager = OkManager.getInstance();
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("foreign", foreign)
                .addFormDataPart("original", original) // 提交普通字段
                .addFormDataPart("uploadForfile", uploadForfile, RequestBody.create(MediaType.parse("*/*"), file))
                .addFormDataPart("uploadOrifile", uploadOrifile, RequestBody.create(MediaType.parse("*/*"), file))
                .build();

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
                            Toast.makeText(StudentForeignTranslationEditActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(StudentForeignTranslationEditActivity.this,StudentForeignTranslationActivity.class);
                            startActivity(intent);
                        }else {
                            Toast.makeText(StudentForeignTranslationEditActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(StudentForeignTranslationEditActivity.this, path+"222222", Toast.LENGTH_SHORT).show();
            }
        }

    }

}