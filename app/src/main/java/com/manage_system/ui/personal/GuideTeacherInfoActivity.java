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
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.manage_system.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        SharedPreferences sp=getSharedPreferences("processData", MODE_PRIVATE);
        Log.w(TAG,sp.getString("teacher" , ""));
        JSONObject obj = JSON.parseObject(sp.getString("teacher" , ""));

        t_name_id.setText(obj.getString("name")+"（"+obj.getString("identifier")+"）");
        t_pro.setText(obj.getString("title"));
        t_email.setText(obj.getString("email"));
        t_phone.setText(obj.getString("bindTel"));
        t_college.setText(obj.getString("college"));
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
                onViewClicked();
            default:
                break;
        }
    }

    public void onViewClicked() {
        finish();
    }
}