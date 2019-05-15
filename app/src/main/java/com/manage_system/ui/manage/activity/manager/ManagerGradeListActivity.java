package com.manage_system.ui.manage.activity.manager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.manage_system.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ManagerGradeListActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.stu_name_id)
    EditText stu_name_id;
    @BindView(R.id.stu_pName)
    EditText stu_pName;
    @BindView(R.id.stu_gt)
    EditText stu_gt;
    @BindView(R.id.stu_mt)
    EditText stu_mt;
    @BindView(R.id.stu_def)
    EditText stu_def;
    @BindView(R.id.stu_total)
    EditText stu_total;
    @BindView(R.id.stu_level)
    EditText stu_level;
    private static String TAG = "ManagerPdListActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ms_tm_grade_item);
        ButterKnife.bind(this);
        initData();
    }

    /**启动这个Activity的Intent
     * @param context
     * @return
     */
    public static Intent createIntent(Context context) {
        return new Intent(context, ManagerGradeListActivity.class);
    }

    @OnClick({R.id.iv_back})
    public void onClick(View v) {//直接调用不会显示v被点击效果
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            default:
                break;
        }
    }

    public void initData(){
        Intent intent = getIntent();
        JSONObject object = JSON.parseObject(intent.getStringExtra("stu_data"));
        Log.e(TAG,object.toString());
        stu_name_id.setText(object.getString("name")+"（"+object.getString("identifier")+"）");
        stu_pName.setText(object.getString("pName"));
        if(object.containsKey("proScore")){
            stu_gt.setText(object.getJSONObject("proScore").getString("scoreGt"));
            stu_mt.setText(object.getJSONObject("proScore").getString("scoreMt"));
            stu_def.setText(object.getJSONObject("proScore").getString("scoreDef"));
            stu_total.setText(object.getJSONObject("proScore").getString("scoreTotal"));
            stu_level.setText(object.getJSONObject("proScore").getString("grade"));
        }

    }
}