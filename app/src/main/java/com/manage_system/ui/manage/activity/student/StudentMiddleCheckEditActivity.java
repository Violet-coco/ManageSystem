package com.manage_system.ui.manage.activity.student;

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

public class StudentMiddleCheckEditActivity extends AppCompatActivity implements View.OnClickListener {

    ImageButton iv_back;
    @BindView(R.id.middle_check_submit)
    Button middle_check_submit;
    @BindView(R.id.mc_infro)
    EditText mc_infro;
    @BindView(R.id.mc_annex)
    EditText mc_annex;
    @BindView(R.id.mc_submit_annex)
    Button mc_submit_annex;
    private String TAG = "提交中期报告";
    private String intro;
    private String path,uploadfile;
    private File file;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ms_student_pd_middlecheck_edit);
        ButterKnife.bind(this);
        getId();
    }

    /**启动这个Activity的Intent
     * @param context
     * @return
     */
    public static Intent createIntent(Context context) {
        return new Intent(context, StudentMiddleCheckEditActivity.class);
    }

    /**
     * 获取id
     */
    private void getId()
    {
        iv_back = (ImageButton)findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);
    }

    @OnClick({R.id.middle_check_submit,R.id.mc_submit_annex})
    public void onClick(View v) {//直接调用不会显示v被点击效果
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.middle_check_submit:
                Log.w(TAG,"点击提交");
                initData();
                break;
            case R.id.mc_submit_annex:
                Log.w(TAG,"选择文件");
                showFileChooser();
                break;
            default:
                break;
        }
    }

    public void initData() {
        intro=mc_infro.getText().toString().trim();
        uploadfile=mc_annex.getText().toString().trim();
        OkManager manager = OkManager.getInstance();
        RequestBody requestBody;
        Log.w(TAG,intro);
        Log.w(TAG,uploadfile);
        if(file == null){
            requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("intro", intro) // 提交普通字段
                    .addFormDataPart("uploadfile", uploadfile)
                    .build();
        }else {
            requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("intro", intro) // 提交普通字段
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
                final JSONObject obj = JSON.parseObject(responseBody);
                Log.e(TAG,obj.toString());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(obj.get("statusCode").equals(100)){
                            Toast.makeText(StudentMiddleCheckEditActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(StudentMiddleCheckEditActivity.this,StudentMiddleCheckActivity.class);
                            startActivity(intent);
                            finish();
                        }else {
                            Toast.makeText(StudentMiddleCheckEditActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(StudentMiddleCheckEditActivity.this, path+"222222", Toast.LENGTH_SHORT).show();
            }
        }

    }
}