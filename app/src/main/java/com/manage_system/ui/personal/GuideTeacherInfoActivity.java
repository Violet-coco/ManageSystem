package com.manage_system.ui.personal;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.manage_system.R;
import com.manage_system.net.ApiConstants;
import com.manage_system.ui.manage.activity.teacher.TeacherReplyMainActivity;
import com.manage_system.ui.manage.adapter.MyAdapter;
import com.manage_system.utils.OkManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

public class GuideTeacherInfoActivity extends AppCompatActivity implements View.OnClickListener {

    ImageButton iv_back;
    EditText t_name_id,t_pro,t_email,t_phone,t_college;
    private String TAG = "教师信息";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ms_guide_teacher_detail);
        getId();

        t_name_id = (EditText) findViewById(R.id.t_name_id);
        t_pro = (EditText) findViewById(R.id.t_pro);
        t_email = (EditText) findViewById(R.id.t_email);
        t_phone = (EditText) findViewById(R.id.t_phone);
        t_college = (EditText) findViewById(R.id.t_college);

        Intent intent = getIntent();
        if(intent.getStringExtra("tea_info").equals("from_reply")){
            initData();
        }else if(intent.getStringExtra("tea_info").equals("from_m_reply")){
            JSONObject obj = JSON.parseObject(intent.getStringExtra("teacher_info"));
            t_name_id.setText(obj.getString("name")+"（"+obj.getString("identifier")+"）");
            t_pro.setText(obj.getString("title"));
            t_email.setText(obj.getString("email"));
            t_phone.setText(obj.getString("bindTel"));
            t_college.setText(obj.getString("college"));
        }else{
            SharedPreferences sp=getSharedPreferences("processData", MODE_PRIVATE);
            Log.w(TAG,sp.getString("teacher" , ""));
            JSONObject obj = JSON.parseObject(sp.getString("teacher" , ""));
            t_name_id.setText(obj.getString("name")+"（"+obj.getString("identifier")+"）");
            t_pro.setText(obj.getString("title"));
            t_email.setText(obj.getString("email"));
            t_phone.setText(obj.getString("bindTel"));
            t_college.setText(obj.getString("college"));
        }
    }

    /**启动这个Activity的Intent
     * @param context
     * @return
     */
    public static Intent createIntent(Context context) {
        return new Intent(context, GuideTeacherInfoActivity.class);
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

    public void initData() {
        OkManager manager = OkManager.getInstance();
        Map<String, String> map = new HashMap<String, String>();
        manager.post(ApiConstants.teacherApi + "/showDefTeachers", map,new okhttp3.Callback() {
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
                            JSONArray array = new JSONArray(obj.getJSONArray("data"));
                            Log.d(TAG,array.toString());
                            for (int i = 0; i < array.size(); i++) {
                                JSONObject object = array.getJSONObject(i);
                                if(object.getBooleanValue("defLeader")){
                                    t_name_id.setText(object.getString("name")+"（"+object.getString("identifier")+"）");
                                    t_pro.setText(object.getString("title"));
                                    t_email.setText(object.getString("email"));
                                    t_phone.setText(object.getString("bindTel"));
                                    t_college.setText(object.getString("college"));
                                }
                            }

                        }else{
                            Toast.makeText(GuideTeacherInfoActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }

}