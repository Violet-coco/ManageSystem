package com.manage_system.ui.manage.activity.teacher;

import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.manage_system.LoginActivity;
import com.manage_system.R;
import com.manage_system.net.ApiConstants;
import com.manage_system.ui.personal.GuideStudentInfoActivity;
import com.manage_system.ui.personal.GuideTeacherInfoActivity;
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

public class TeacherScoreDetailActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.pName)
    EditText pName;
    @BindView(R.id.stu_name)
    EditText stu_name;
    @BindView(R.id.stu_id)
    EditText stu_id;
    @BindView(R.id.guide_teacher_name)
    EditText guide_teacher_name;
    @BindView(R.id.scoreWorth)
    EditText scoreWorth;
    @BindView(R.id.scoreReport)
    EditText scoreReport;
    @BindView(R.id.scoreProcess)
    EditText scoreProcess;
    @BindView(R.id.scoreDefence)
    EditText scoreDefence;
    @BindView(R.id.comment)
    EditText comment;
    @BindView(R.id.ct_item_submit)
    Button ct_item_submit;
    private static String TAG = "选题界面";
    private String sid,ts_scoreWorth,ts_scoreReport,ts_scoreProcess,ts_scoreDefence,ts_comment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ms_teacher_reply_plan_score_detail);
        ButterKnife.bind(this);

        Intent intent = getIntent();

        SharedPreferences sp=getSharedPreferences("processData", MODE_PRIVATE);
        JSONObject obj = JSON.parseObject(sp.getString("showDefStudents" , ""));
        Log.w(TAG,obj.toString());
        JSONArray array = new JSONArray(obj.getJSONArray("studentList"));
        Log.w(TAG,array.toString());
        Log.w(TAG,"position==="+intent.getStringExtra("position"));

        JSONObject object = array.getJSONObject(Integer.parseInt(intent.getStringExtra("position")));

        stu_name.setText(object.getString("name"));
        stu_id.setText(object.getString("identifier"));
        pName.setText(object.getString("pName"));
        guide_teacher_name.setText(object.getJSONObject("gt").getString("name"));

    }

    /**启动这个Activity的Intent
     * @param context
     * @return
     */
    public static Intent createIntent(Context context) {
        return new Intent(context, TeacherScoreDetailActivity.class);
    }

    @OnClick({R.id.iv_back,R.id.ct_item_submit})
    public void onClick(View v) {//直接调用不会显示v被点击效果
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.ct_item_submit:
                showDialog();
                break;
            default:
                break;
        }
    }

    public void submitData() {
        sid = stu_id.getText().toString().trim();
        ts_scoreWorth=scoreWorth.getText().toString().trim();
        ts_scoreReport=scoreReport.getText().toString().trim();
        ts_scoreProcess=scoreProcess.getText().toString().trim();
        ts_scoreDefence=scoreDefence.getText().toString().trim();
        ts_comment=comment.getText().toString().trim();

        OkManager manager = OkManager.getInstance();
        // 接口名称
        String api;
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM)
                .addFormDataPart("sid", sid)
                .addFormDataPart("scoreWorth", ts_scoreWorth)
                .addFormDataPart("scoreReport", ts_scoreReport)
                .addFormDataPart("scoreProcess", ts_scoreProcess)
                .addFormDataPart("scoreDefence", ts_scoreDefence)
                .addFormDataPart("comment", ts_comment);

        RequestBody requestBody = builder.build();

        OkManager.postFile(ApiConstants.teacherApi + "/scoringDefence", requestBody,new okhttp3.Callback() {
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
                            Toast.makeText(TeacherScoreDetailActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(TeacherScoreDetailActivity.this,TeacherScoreMainActivity.class);
                            startActivity(intent);
                            finish();
                        }else if(obj.get("statusCode").equals(102)){
                            Toast.makeText(TeacherScoreDetailActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(TeacherScoreDetailActivity.this,LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }else {
                            Toast.makeText(TeacherScoreDetailActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }

    public void showDialog(){
        final Dialog dialog = new Dialog(TeacherScoreDetailActivity.this, R.style.MyDialog);
        //设置它的ContentView
        dialog.setContentView(R.layout.alert_dialog);
        ((TextView)dialog.findViewById(R.id.tvAlertDialogTitle)).setText("确认提交");
        ((TextView)dialog.findViewById(R.id.tvAlertDialogMessage)).setText("确认提交分数？");
        dialog.show();

        Button confirm = (Button)dialog.findViewById(R.id.btnAlertDialogPositive);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                submitData();
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

}