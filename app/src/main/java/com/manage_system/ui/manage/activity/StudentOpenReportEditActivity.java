package com.manage_system.ui.manage.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.manage_system.R;
import com.manage_system.utils.OkManager;

import java.util.HashMap;
import java.util.Map;

public class StudentOpenReportEditActivity extends AppCompatActivity implements View.OnClickListener {

    ImageButton iv_back;
    private OkManager manager;

    private String path="http://www.yuanbw.cn:20086/gpms/stu/commitOpeningReport";
    private String TAG = "修改开题报告";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 连接接口
        manager = OkManager.getInstance();
        Map<String, String> map = new HashMap<String, String>();

        manager.sendComplexForm(path, map, new OkManager.Fun4() {
            @Override
            public void onResponse(org.json.JSONObject jsonObject) {
                JSONObject obj = JSON.parseObject(jsonObject.toString());
                Log.w(TAG,obj.toString());
            }
        });
        setContentView(R.layout.ms_student_pd_openrecord_edit);
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

    public void onClick(View v) {//直接调用不会显示v被点击效果
        switch (v.getId()) {
            case R.id.iv_back:
                onViewClicked();
                break;
            default:
                break;
        }
    }

    public void onViewClicked() {
        finish();
    }
}