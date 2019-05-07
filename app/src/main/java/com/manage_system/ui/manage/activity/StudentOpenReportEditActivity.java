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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.manage_system.R;
import com.manage_system.net.ApiConstants;
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
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.manage_system.utils.FileUtils.getPath;
import static com.manage_system.utils.FileUtils.getRealPathFromURI;

public class StudentOpenReportEditActivity extends AppCompatActivity implements View.OnClickListener {

    ImageButton iv_back;
    private OkManager manager;

    private String path;
    private String TAG = "修改开题报告";
    @BindView(R.id.or_aim)
    EditText or_aim;
    @BindView(R.id.or_content)
    EditText or_content;
    @BindView(R.id.or_tech)
    EditText or_tech;
    @BindView(R.id.or_plan)
    EditText or_plan;
    @BindView(R.id.or_annex)
    EditText or_annex;
    @BindView(R.id.open_record_submit)
    Button open_record_submit;
    @BindView(R.id.or_submit_annex)
    Button or_submit_annex;
    private String aim,content,tech,plan,uploadfile;
    private File tempFile;
    private OkHttpClient okHttpClient;
    private static final int FILE_SELECT_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ms_student_pd_openrecord_edit);
        ButterKnife.bind(this);
        getId();
    }

    /**启动这个Activity的Intent
     * @param context
     * @return
     */
    public static Intent createIntent(Context context) {
        return new Intent(context, StudentOpenReportEditActivity.class);
    }

    /**
     * 获取id
     */
    private void getId()
    {
        iv_back = (ImageButton)findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);
    }

    @OnClick({R.id.open_record_submit,R.id.or_submit_annex})
    public void onClick(View v) {//直接调用不会显示v被点击效果
        switch (v.getId()) {
            case R.id.iv_back:
                onViewClicked();
                break;
            case R.id.open_record_submit:
                Log.w(TAG,"点击提交");
                initData();
                break;
            case R.id.or_submit_annex:
                Log.w(TAG,"选择文件");
                showFileChooser();
                break;
            default:
                break;
        }
    }

    public void initData() {
        aim=or_aim.getText().toString().trim();
        content=or_content.getText().toString().trim();
        tech=or_tech.getText().toString().trim();
        plan=or_plan.getText().toString().trim();
        uploadfile=or_annex.getText().toString().trim();
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
        manager.postFile(ApiConstants.studentApi + "/commitOpeningReport", map,new okhttp3.Callback() {
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
                            Toast.makeText(StudentOpenReportEditActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(StudentOpenReportEditActivity.this,StudentOpenReportActivity.class);
                            startActivity(intent);
                        }else {
                            Toast.makeText(StudentOpenReportEditActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(StudentOpenReportEditActivity.this, path+"222222", Toast.LENGTH_SHORT).show();
            }
        }

    }

    public void onViewClicked() {
        finish();
    }
}