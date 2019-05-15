package com.manage_system.ui.manage.activity.manager;

import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.manage_system.R;
import com.manage_system.net.ApiConstants;
import com.manage_system.utils.DateUtil;
import com.manage_system.utils.DownloadUtil;
import com.manage_system.utils.OpenFileUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ManagerPdListActivity extends AppCompatActivity implements View.OnClickListener {

    private String id = null;
    private Context mContext;
    private String fileName = null;
    @BindView(R.id.stu_name_id)
    EditText stu_name_id;
    @BindView(R.id.stu_pName)
    EditText stu_pName;
    @BindView(R.id.pd_for_ori)
    EditText pd_for_ori;
    @BindView(R.id.pd_guide_record)
    EditText pd_guide_record;
    @BindView(R.id.pd_lit_review)
    EditText pd_lit_review;
    @BindView(R.id.pd_middle_check)
    EditText pd_middle_check;
    @BindView(R.id.pd_open_record)
    EditText pd_open_record;
    @BindView(R.id.pd_thesis)
    EditText pd_thesis;
    private static String TAG = "ManagerPdListActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ms_manager_material_total);
        ButterKnife.bind(this);
        initData();
    }

    /**启动这个Activity的Intent
     * @param context
     * @return
     */
    public static Intent createIntent(Context context) {
        return new Intent(context, ManagerPdListActivity.class);
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
        if(object.containsKey("openingReport")){
            pd_open_record.setText("已提交");
        }else{
            pd_open_record.setText("未提交");
            pd_open_record.setTextColor(Color.RED);
        }

        if(object.containsKey("midInspection")){
            pd_middle_check.setText("已提交");
        }else{
            pd_middle_check.setText("未提交");
            pd_middle_check.setTextColor(Color.RED);
        }

        if(object.containsKey("literatureReview")){
            pd_lit_review.setText("已提交");
        }else{
            pd_lit_review.setText("未提交");
            pd_lit_review.setTextColor(Color.RED);
        }

        if(object.containsKey("foreignOriginal")){
            pd_for_ori.setText("已提交");
        }else{
            pd_for_ori.setText("未提交");
            pd_for_ori.setTextColor(Color.RED);
        }

        if(object.containsKey("graduationProject")){
            pd_thesis.setText("已提交");
        }else{
            pd_thesis.setText("未提交");
            pd_thesis.setTextColor(Color.RED);
        }

        if(object.getJSONArray("guidanceRecordList").size()>0){
            pd_guide_record.setText("已提交");
        }else{
            pd_guide_record.setText("未提交");
            pd_guide_record.setTextColor(Color.RED);
        }

    }
}