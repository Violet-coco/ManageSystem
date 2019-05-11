package com.manage_system.ui.manage.activity.teacher;

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
import android.widget.TextView;
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
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TeacherThesisCommentDetailActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.stu_pName)
    EditText stu_pName;
    @BindView(R.id.stu_name)
    EditText stu_name;
    @BindView(R.id.stu_id)
    EditText stu_id;
    @BindView(R.id.stu_guide_teacher)
    EditText stu_guide_teacher;
    @BindView(R.id.scoreLiterature)
    EditText scoreLiterature;
    @BindView(R.id.scoreInstruction)
    EditText scoreInstruction;
    @BindView(R.id.scoreWorkload)
    EditText scoreWorkload;
    @BindView(R.id.scoreInnovate)
    EditText scoreInnovate;
    @BindView(R.id.comment)
    EditText comment;
    @BindView(R.id.ct_item_submit)
    Button ct_item_submit;
    private static String TAG = "选题界面";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ms_teacher_reply_review_detail);
        ButterKnife.bind(this);

        Intent intent = getIntent();

        SharedPreferences sp=getSharedPreferences("processData", MODE_PRIVATE);
        Log.w(TAG,sp.getString("teacher_thesis_comment" , ""));
        JSONObject obj = JSON.parseObject(sp.getString("teacher_thesis_comment" , ""));
        JSONArray array = new JSONArray(obj.getJSONArray("data"));
        JSONObject object = array.getJSONObject(Integer.parseInt(intent.getStringExtra("position")));

        stu_name.setText(object.getString("name"));
        stu_id.setText(object.getString("identifier"));
        stu_pName.setText(object.getString("pName"));
        stu_guide_teacher.setText(object.getJSONObject("gt").getString("name"));

    }

    /**启动这个Activity的Intent
     * @param context
     * @return
     */
    public static Intent createIntent(Context context) {
        return new Intent(context, TeacherThesisCommentDetailActivity.class);
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

        OkManager manager = OkManager.getInstance();
        Map<String, String> map = new HashMap<String, String>();

        map.put("sid",stu_id.getText().toString().trim());
        map.put("scoreLiterature",scoreLiterature.getText().toString().trim());
        map.put("scoreInstruction",scoreInstruction.getText().toString().trim());
        map.put("scoreWorkload",scoreWorkload.getText().toString().trim());
        map.put("scoreInnovate",scoreInnovate.getText().toString().trim());
        map.put("comment",comment.getText().toString().trim());

        manager.post(ApiConstants.teacherApi + "/markingMtDoc", map,new okhttp3.Callback() {
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
                            Toast.makeText(TeacherThesisCommentDetailActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(TeacherThesisCommentDetailActivity.this,TeacherThesisCommentMainActivity.class);
                            startActivity(intent);
                            finish();
                        }else {
                            Toast.makeText(TeacherThesisCommentDetailActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }

    public void showDialog(){
        final Dialog dialog = new Dialog(TeacherThesisCommentDetailActivity.this, R.style.MyDialog);
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