package com.manage_system.ui.manage.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.manage_system.R;
import com.manage_system.net.ApiConstants;
import com.manage_system.ui.manage.Manage;
import com.manage_system.ui.manage.fragment.ManageFragment;
import com.manage_system.ui.personal.GuideTeacherInfoActivity;
import com.manage_system.utils.DateUtil;
import com.manage_system.utils.OkManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Response;

public class StudentChooseDoneTitleActivity extends AppCompatActivity implements View.OnClickListener {

    ImageButton iv_back;
    Button ct_is_choose;
    EditText ct_teacher;
    EditText ct_topic,ct_type,ct_resource,ct_number;
    private String id = null;
    @BindView(R.id.ct_profession)
    EditText ct_profession;
    @BindView(R.id.ct_time)
    EditText ct_time;
    @BindView(R.id.ct_task)
    EditText ct_task;
    @BindView(R.id.ct_annex)
    EditText ct_annex;
    @BindView(R.id.ct_detail)
    EditText ct_detail;
    private String TAG = "已选题界面";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ms_student_ct_detail);
        ButterKnife.bind(this);
        getId();

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        Log.w(TAG,id);

        ct_topic = (EditText) findViewById(R.id.ct_topic);
        ct_type = (EditText)findViewById(R.id.ct_type);
        ct_resource = (EditText)findViewById(R.id.ct_resource);
        ct_number = (EditText)findViewById(R.id.ct_number);
        ct_is_choose.setText("取　消");

        SharedPreferences sp=getSharedPreferences("chooseTitle", MODE_PRIVATE);
        Log.w(TAG,sp.getString("obj" , ""));
        JSONObject obj = JSON.parseObject(sp.getString("obj" , ""));
        JSONArray array = new JSONArray(obj.getJSONObject("data").getJSONArray("projects"));
        for (int i = 0; i < Integer.parseInt(obj.getJSONObject("data").getString("count")); i++) {
            JSONObject object = array.getJSONObject(i);
            JSONObject project = object.getJSONObject("project");
            JSONObject teacher = object.getJSONObject("teacher");
            if(project.getString("id").equals(id)){
                ct_topic.setText(project.getString("title"));
                ct_type.setText(project.getString("genre"));
                ct_resource.setText(project.getString("source"));
                ct_number.setText(project.getString("rest"));
                ct_teacher.setText(teacher.getString("name"));
                ct_profession.setText(project.getString("major"));
                ct_time.setText(DateUtil.getDateFormat(object.getString("setDate")));
//        ct_task.setText(project.getString("fileName"));
//        ct_annex.setText(project.getString("fileType"));
                ct_detail.setText(project.getString("briefIntro"));
                SharedPreferences.Editor editor=sp.edit();
                editor.putString("teacher",teacher.toString());
                editor.commit();
            }
        }

    }

    /**启动这个Activity的Intent
     * @param context
     * @return
     */
    public static Intent createIntent(Context context) {
        return new Intent(context, StudentChooseDoneTitleActivity.class);
    }

    /**
     * 获取id
     */
    private void getId()
    {
        iv_back = (ImageButton)findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);
        ct_is_choose = (Button)findViewById(R.id.ct_is_choose);
        ct_is_choose.setOnClickListener(this);
        ct_teacher = (EditText)findViewById(R.id.ct_teacher);
        ct_teacher.setOnClickListener(this);
    }

    public void onClick(View v) {//直接调用不会显示v被点击效果
        switch (v.getId()) {
            case R.id.iv_back:
                onViewClicked();
                break;
            case R.id.ct_is_choose:
                showDialog();
                break;
            case R.id.ct_teacher:
                Intent intent = new Intent(StudentChooseDoneTitleActivity.this,GuideTeacherInfoActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    public void showDialog(){
        final Dialog dialog = new Dialog(StudentChooseDoneTitleActivity.this, R.style.MyDialog);
        //设置它的ContentView
        dialog.setContentView(R.layout.alert_dialog);
        ((TextView)dialog.findViewById(R.id.tvAlertDialogTitle)).setText("取消课题");
        ((TextView)dialog.findViewById(R.id.tvAlertDialogMessage)).setText("是否取消选择？");
        dialog.show();

        Button confirm = (Button)dialog.findViewById(R.id.btnAlertDialogPositive);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                OkManager manager = OkManager.getInstance();
                Map<String, String> map = new HashMap<String, String>();
                map.put("pid", id);
                Log.w(TAG,id+"取消");
                manager.post(ApiConstants.studentApi+"/cancelProject", map,new okhttp3.Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e(TAG, "onFailure: ",e);
                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String responseBody = response.body().string();
                        final JSONObject obj = JSON.parseObject(responseBody);
                        Log.e(TAG,obj.toString());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(obj.get("statusCode").equals(100)) {
                                    Toast.makeText(StudentChooseDoneTitleActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(StudentChooseDoneTitleActivity.this,StudentChooseDoneTitleMainActivity.class);
                                    startActivity(intent);
                                    finish();
                                    Manage.getData();
                                }else{
                                    Toast.makeText(StudentChooseDoneTitleActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }
                });

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

    public void onViewClicked() {
        finish();
    }
}