package com.manage_system.ui.manage.activity.student;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.manage_system.LoginActivity;
import com.manage_system.R;
import com.manage_system.net.ApiConstants;
import com.manage_system.utils.DateUtil;
import com.manage_system.utils.OkManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Response;

public class StudentReplyPlanActivity extends AppCompatActivity implements View.OnClickListener {

    ImageButton iv_back;
    private String TAG = "答辩安排";
    @BindView(R.id.reply_title)
    EditText reply_title;
    @BindView(R.id.reply_guide_teacher)
    EditText reply_guide_teacher;
    @BindView(R.id.reply_time)
    EditText reply_time;
    @BindView(R.id.reply_class)
    EditText reply_class;
    @BindView(R.id.reply_teacher)
    EditText reply_teacher;
    @BindView(R.id.reply_group)
    EditText reply_group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ms_student_reply_plan);
        ButterKnife.bind(this);
        getId();
        initData();
        SharedPreferences sp = getSharedPreferences("personInfo", MODE_PRIVATE);
        reply_title.setText(sp.getString("pName",""));
        reply_guide_teacher.setText(sp.getString("mtName",""));
    }

    /**启动这个Activity的Intent
     * @param context
     * @return
     */
    public static Intent createIntent(Context context) {
        return new Intent(context, StudentReplyPlanActivity.class);
    }

    /**
     * 获取id
     */
    private void getId()
    {
        iv_back = (ImageButton)findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);
    }

    public void initData() {
        OkManager manager = OkManager.getInstance();
        Map<String, String> map = new HashMap<String, String>();
        OkManager.post(ApiConstants.studentApi + "/showDefence", map,new okhttp3.Callback() {
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
                            JSONObject object = obj.getJSONObject("data");
                            String teachers = "";
                            JSONArray array = new JSONArray(obj.getJSONObject("data").getJSONArray("teaGroup"));
                            for (int i = 0; i < array.size(); i++) {
                                Log.w(TAG,array.getJSONObject(i).toString());
                                teachers = teachers + array.getJSONObject(i).getJSONObject("teaAuth").getString("name") +"　";
                                Log.w(TAG,teachers);
                            }
                            reply_time.setText(DateUtil.getDateFormatNoTime(object.getString("defDate"))+"　第"+object.get("defWeek")+"周  星期"+object.get("defDay"));
                            reply_class.setText(object.getString("defClass"));
                            reply_teacher.setText(teachers);
                            reply_group.setText("第"+object.getString("groupNum")+"组");
                        }else if(obj.get("statusCode").equals(102)){
                            Toast.makeText(StudentReplyPlanActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(StudentReplyPlanActivity.this,LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }else{
                            Toast.makeText(StudentReplyPlanActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
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