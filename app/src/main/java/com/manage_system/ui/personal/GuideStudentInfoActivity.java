package com.manage_system.ui.personal;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.manage_system.R;
import com.manage_system.net.ApiConstants;
import com.manage_system.utils.OkManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Response;

public class GuideStudentInfoActivity extends AppCompatActivity implements View.OnClickListener {

    ImageButton iv_back;
    @BindView(R.id.stu_name)
    EditText stu_name;
    @BindView(R.id.stu_id)
    EditText stu_id;
    @BindView(R.id.stu_college)
    EditText stu_college;
    @BindView(R.id.stu_class)
    EditText stu_class;
    @BindView(R.id.stu_sex)
    EditText stu_sex;
    @BindView(R.id.stu_major)
    EditText stu_major;
    @BindView(R.id.stu_department)
    EditText stu_department;
    @BindView(R.id.stu_tel)
    EditText stu_tel;
    @BindView(R.id.stu_email)
    EditText stu_email;
    @BindView(R.id.gt_department_main)
    RelativeLayout gt_department_main;
    private String TAG = "学生信息";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ms_guide_student_detail);
        ButterKnife.bind(this);
        getId();

        Intent intent = getIntent();
        if(intent.getStringExtra("stu_info").equals("from_reply")){
            initData(intent.getStringExtra("stu_id"));
        }else if(intent.getStringExtra("stu_info").equals("from_m_reply")){
            JSONObject obj = JSON.parseObject(intent.getStringExtra("student_info"));
            stu_name.setText(obj.getString("name"));
            stu_id.setText(obj.getString("identifier"));
            stu_college.setText(obj.getString("college"));
            stu_class.setText(obj.getString("grade")+"级"+obj.getString("classNo")+"班");
            stu_sex.setText(obj.getString("sex"));
            stu_major.setText(obj.getString("major"));
            gt_department_main.setVisibility(View.GONE);
            stu_tel.setText(obj.getString("contactTel"));
            stu_email.setText(obj.getString("email"));
        }else if(intent.getStringExtra("stu_info").equals("from_m_total")){
            JSONObject object = JSON.parseObject(intent.getStringExtra("total_stu_info"));
            Log.w(TAG,object.toString());
            JSONArray array = new JSONArray(object.getJSONArray("choose_stu_list"));
            JSONObject obj = array.getJSONObject(Integer.parseInt(intent.getStringExtra("position")));
            stu_name.setText(obj.getString("name"));
            stu_id.setText(obj.getString("identifier"));
            stu_college.setText(obj.getString("college"));
            stu_class.setText(obj.getString("grade")+"级"+obj.getString("classNo")+"班");
            stu_sex.setText(obj.getString("sex"));
            stu_major.setText(obj.getString("major"));
            gt_department_main.setVisibility(View.GONE);
            stu_tel.setText(obj.getString("contactTel"));
            stu_email.setText(obj.getString("email"));
        }else if(intent.getStringExtra("stu_info").equals("from_score")){
            JSONObject obj = JSON.parseObject(intent.getStringExtra("stu_data"));
            Log.w(TAG,obj.toString());

            stu_name.setText(obj.getString("name"));
            stu_id.setText(obj.getString("identifier"));
            stu_college.setText(obj.getString("college"));
            stu_class.setText(obj.getString("grade")+"级"+obj.getString("classNo")+"班");
            stu_sex.setText(obj.getString("sex"));
            stu_major.setText(obj.getString("major"));
            gt_department_main.setVisibility(View.GONE);
            stu_tel.setText(obj.getString("contactTel"));
            stu_email.setText(obj.getString("email"));
        }else if(intent.getStringExtra("stu_info").equals("from_grade_detail")){
            JSONObject obj = JSON.parseObject(intent.getStringExtra("stu_data"));
            Log.w(TAG,obj.toString());

            stu_name.setText(obj.getString("name"));
            stu_id.setText(obj.getString("identifier"));
            stu_college.setText(obj.getString("college"));
            stu_class.setText(obj.getString("grade")+"级"+obj.getString("classNo")+"班");
            stu_sex.setText(obj.getString("sex"));
            stu_major.setText(obj.getString("major"));
            gt_department_main.setVisibility(View.GONE);
            stu_tel.setText(obj.getString("contactTel"));
            stu_email.setText(obj.getString("email"));
        }else {
            SharedPreferences sp=getSharedPreferences("processData", MODE_PRIVATE);
            Log.w(TAG,sp.getString("student" , ""));
            JSONObject obj = JSON.parseObject(sp.getString("student" , ""));
            Log.w(TAG,obj.getString("name"));

            stu_name.setText(obj.getString("name"));
            stu_id.setText(obj.getString("identifier"));
            stu_college.setText(obj.getString("college"));
            stu_class.setText(obj.getString("grade")+"级"+obj.getString("classNo")+"班");
            stu_sex.setText(obj.getString("sex"));
            stu_major.setText(obj.getString("major"));
            stu_department.setText(obj.getString("department"));
            stu_tel.setText(obj.getString("contactTel"));
            stu_email.setText(obj.getString("email"));
        }

    }

    /**启动这个Activity的Intent
     * @param context
     * @return
     */
    public static Intent createIntent(Context context) {
        return new Intent(context, GuideStudentInfoActivity.class);
    }

    /**
     * 获取id
     */
    private void getId()
    {
        iv_back = (ImageButton)findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);
    }

    public void onClick(View v) {//直接调用不会显示v被点击效果
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
            default:
                break;
        }
    }

    public void initData(String str) {
        OkManager manager = OkManager.getInstance();
        Map<String, String> map = new HashMap<String, String>();
        map.put("sid",str);
        manager.post(ApiConstants.teacherApi + "/showDefStudents", map,new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "onFailure: ",e);
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseBody = response.body().string();
                Log.e(TAG,responseBody);
                final JSONObject obj = JSON.parseObject(responseBody);
                final String msg = obj.getString("msg");
                Log.e(TAG,responseBody);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(obj.get("statusCode").equals(100)){
                            JSONArray array = new JSONArray(obj.getJSONObject("data").getJSONArray("studentList"));
                            Log.d(TAG,array.toString());
                            JSONObject object = array.getJSONObject(0);
                            stu_name.setText(object.getString("name"));
                            stu_id.setText(object.getString("identifier"));
                            stu_college.setText(object.getString("college"));
                            stu_class.setText(object.getString("grade")+"级"+object.getString("classNo")+"班");
                            stu_sex.setText(object.getString("sex"));
                            stu_major.setText(object.getString("major"));
                            stu_department.setText(object.getString("department"));
                            stu_tel.setText(object.getString("contactTel"));
                            stu_email.setText(object.getString("email"));

                        }else{
                            Toast.makeText(GuideStudentInfoActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }

}