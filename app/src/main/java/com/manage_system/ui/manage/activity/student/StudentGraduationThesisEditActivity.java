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

public class StudentGraduationThesisEditActivity extends AppCompatActivity implements View.OnClickListener {

    ImageButton iv_back;
    @BindView(R.id.gt_keywords)
    EditText gt_keywords;
    @BindView(R.id.gt_innovatePoint)
    EditText gt_innovatePoint;
    @BindView(R.id.gt_cnSummary)
    EditText gt_cnSummary;
    @BindView(R.id.gt_enSummary)
    EditText gt_enSummary;
    @BindView(R.id.gt_other)
    EditText gt_other;
    @BindView(R.id.gt_word)
    EditText gt_word;
    @BindView(R.id.gt_annex)
    EditText gt_annex;
    private String TAG = "毕业论文展示";
    private String path,uploadfile;
    private int fileIdType = 0;
    private File file,file1;
    private Context mContext;
    private String keywords,innovatePoint,cnSummary,enSummary,other,uploadDocFile,uploadAttFile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ms_student_pd_graduation_thesis_edit);
        ButterKnife.bind(this);
    }

    /**启动这个Activity的Intent
     * @param context
     * @return
     */
    public static Intent createIntent(Context context) {
        return new Intent(context, StudentGraduationThesisEditActivity.class);
    }


    @OnClick({R.id.iv_back,R.id.graduation_thesis_submit,R.id.gt_word_submit,R.id.gt_annex_submit,R.id.gt_word,R.id.gt_annex})
    public void onClick(View v) {//直接调用不会显示v被点击效果
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.graduation_thesis_submit:
                submitData();
                break;
            case R.id.gt_word_submit:
                fileIdType = 1;
                showFileChooser();
                break;
            case R.id.gt_annex_submit:
                fileIdType = 2;
                showFileChooser();
                break;
            default:
                break;
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

        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM)
                .addFormDataPart("keywords", keywords)
                .addFormDataPart("innovatePoint", innovatePoint)
                .addFormDataPart("cnSummary", cnSummary)
                .addFormDataPart("enSummary", enSummary)
                .addFormDataPart("other", other);

        if(file == null || file1 == null){
            if(file == null&& file1 !=null){
                builder.addFormDataPart("uploadDocFile", uploadDocFile)
                        .addFormDataPart("uploadAttFile", uploadAttFile,RequestBody.create(MediaType.parse("*/*"),file1))
                        .build();
            }else if(file1 == null&&file!=null){
                builder.addFormDataPart("uploadDocFile", uploadDocFile,RequestBody.create(MediaType.parse("*/*"), file))
                        .addFormDataPart("uploadAttFile", uploadAttFile)
                        .build();
            }else{
                builder.addFormDataPart("uploadDocFile", uploadDocFile)
                        .addFormDataPart("uploadAttFile", uploadAttFile)
                        .build();
            }
        }else{
            builder.addFormDataPart("uploadDocFile", uploadDocFile,RequestBody.create(MediaType.parse("*/*"), file))
                    .addFormDataPart("uploadAttFile", uploadAttFile,RequestBody.create(MediaType.parse("*/*"), file1))
                    .build();
        }
        RequestBody requestBody = builder.build();

        manager.postFile(ApiConstants.studentApi + "/addGraduationProject", requestBody,new okhttp3.Callback() {
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
                            Toast.makeText(StudentGraduationThesisEditActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(StudentGraduationThesisEditActivity.this,StudentGraduationThesisMainActivity.class);
                            startActivity(intent);
                            finish();
                        }else {
                            Toast.makeText(StudentGraduationThesisEditActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
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
                    uploadfile = file.getName();
                    gt_word.setText(uploadfile);
                }
                if(fileIdType == 2){
                    file1 = new File(path);
                    uploadfile = file1.getName();
                    gt_annex.setText(uploadfile);
                }
                Toast.makeText(this,path+"11111",Toast.LENGTH_SHORT).show();
                return;
            }
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {//4.4以后
                path = getPath(this, uri);
                Log.w(TAG,path);
                if(fileIdType == 1){
                    file = new File(path);
                    uploadfile = file.getName();
                    gt_word.setText(uploadfile);
                }
                if(fileIdType == 2){
                    file1 = new File(path);
                    uploadfile = file1.getName();
                    gt_annex.setText(uploadfile);
                }
                Toast.makeText(this,path,Toast.LENGTH_SHORT).show();
            } else {//4.4以下下系统调用方法
                path = getRealPathFromURI(this,uri);
                Log.w(TAG,path);
                Toast.makeText(StudentGraduationThesisEditActivity.this, path+"222222", Toast.LENGTH_SHORT).show();
            }
        }

    }
}