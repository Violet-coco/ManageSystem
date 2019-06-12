package com.manage_system.ui.manage.activity.teacher;

import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonArray;
import com.manage_system.R;
import com.manage_system.net.ApiConstants;
import com.manage_system.ui.personal.GuideStudentInfoActivity;
import com.manage_system.utils.DownloadUtil;
import com.manage_system.utils.OkManager;
import com.manage_system.utils.OpenFileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TeacherGtCommentActivity extends AppCompatActivity implements View.OnClickListener {

    public String list;

    @BindView(R.id.stu_pName)
    EditText stu_pName;
    @BindView(R.id.stu_name)
    EditText stu_name;
    @BindView(R.id.stu_id)
    EditText stu_id;
    @BindView(R.id.stu_guide_teacher)
    EditText stu_guide_teacher;
    @BindView(R.id.scoreAnalysis)
    EditText scoreAnalysis;
    @BindView(R.id.scoreTechnology)
    EditText scoreTechnology;
    @BindView(R.id.scoreInnovate)
    EditText scoreInnovate;
    @BindView(R.id.scoreInstruction)
    EditText scoreInstruction;
    @BindView(R.id.scoreInvestigation)
    EditText scoreInvestigation;
    @BindView(R.id.scoreWorkload)
    EditText scoreWorkload;
    @BindView(R.id.attendDefence)
    Spinner attendDefence;

    private static String TAG = "选题界面";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ms_tm_gt_review_detail);
        ButterKnife.bind(this);

        Intent intent = getIntent();

        SharedPreferences sp=getSharedPreferences("processData", MODE_PRIVATE);
        Log.w(TAG,sp.getString("teacher_gt_comment" , ""));
        JSONObject obj = JSON.parseObject(sp.getString("teacher_gt_comment" , ""));
        JSONArray array = new JSONArray(obj.getJSONArray("data"));
        JSONObject object = array.getJSONObject(Integer.parseInt(intent.getStringExtra("position")));

        stu_pName.setText(object.getString("pName"));
        stu_name.setText(object.getString("name"));
        stu_id.setText(object.getString("identifier"));
        SharedPreferences sp1=getSharedPreferences("loginInfo", MODE_PRIVATE);
        stu_guide_teacher.setText(sp1.getString("name" , ""));

    }

    /**启动这个Activity的Intent
     * @param context
     * @return
     */
    public static Intent createIntent(Context context) {
        return new Intent(context, TeacherGtCommentActivity.class);
    }

    @OnClick({R.id.iv_back,R.id.ct_item_submit})
    public void onClick(View v) {//直接调用不会显示v被点击效果
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.ct_item_submit:
                submitData();
                break;
            default:
                break;
        }
    }

    public void submitData() {
        String rangeType;
        if(attendDefence.getSelectedItem().toString().equals("是")){
            rangeType="1";
        }else{
            rangeType="0";
        }

        OkManager manager = OkManager.getInstance();

        Map<String, String> map = new HashMap<String, String>();
        map.put("sid",stu_id.getText().toString().trim());
        map.put("scoreInvestigation",scoreInvestigation.getText().toString().trim());
        map.put("scoreTechnology",scoreTechnology.getText().toString().trim());
        map.put("scoreAnalysis",scoreAnalysis.getText().toString().trim());
        map.put("scoreWorkload",scoreWorkload.getText().toString().trim());
        map.put("scoreInstruction",scoreInstruction.getText().toString().trim());
        map.put("scoreInnovate",scoreInnovate.getText().toString().trim());
        map.put("attendDefence",rangeType);
        Log.w(TAG,scoreInvestigation.getText().toString().trim());
        OkManager.post(ApiConstants.teacherApi + "/markingGtDoc", map,new okhttp3.Callback() {
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
                            Toast.makeText(TeacherGtCommentActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(TeacherGtCommentActivity.this,TeacherGtCommentMainActivity.class);
                            startActivity(intent);
                            finish();
                        }else {
                            Toast.makeText(TeacherGtCommentActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }

}