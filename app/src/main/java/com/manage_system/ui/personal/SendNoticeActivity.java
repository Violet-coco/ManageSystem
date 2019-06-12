package com.manage_system.ui.personal;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.manage_system.R;
import com.manage_system.net.ApiConstants;
import com.manage_system.ui.inter.Notice;
import com.manage_system.ui.inter.Teacher;
import com.manage_system.ui.manage.activity.manager.ManagerGroupGuideEditActivity;
import com.manage_system.ui.manage.activity.manager.ManagerReplyGroupMainActivity;
import com.manage_system.ui.manage.activity.student.StudentMiddleCheckActivity;
import com.manage_system.utils.OkManager;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.manage_system.utils.FileUtils.getPath;
import static com.manage_system.utils.FileUtils.getRealPathFromURI;

public class SendNoticeActivity extends AppCompatActivity implements View.OnClickListener {

    ImageButton iv_back;
    @BindView(R.id.notice_submit)
    Button notice_submit;
    @BindView(R.id.notice_content)
    EditText notice_content;
    private String TAG = "SendNoticeActivity";
    //用于存储条目数据
    private List<String> list = new ArrayList<>();
    //用于存放已选择的条目
    public List<Map<String,Object>> listID=new ArrayList<>();
    final List<BigInteger> listId = new ArrayList<>();
    final List<BigInteger> listStuId = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ms_person_notice);
        ButterKnife.bind(this);
        getId();
        SharedPreferences sp=getSharedPreferences("loginInfo", MODE_PRIVATE);
        if(!sp.getString("authority","").equals("1")){
            initData();
        }
    }

    /**启动这个Activity的Intent
     * @param context
     * @return
     */
    public static Intent createIntent(Context context) {
        return new Intent(context, SendNoticeActivity.class);
    }

    /**
     * 获取id
     */
    private void getId()
    {
        iv_back = (ImageButton)findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);
    }

    @OnClick({R.id.notice_submit})
    public void onClick(View v) {//直接调用不会显示v被点击效果
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.notice_submit:
                SharedPreferences sp=getSharedPreferences("loginInfo", MODE_PRIVATE);
                if(!sp.getString("authority","").equals("1")){
                    showMutilAlertDialog();
                }else{
                    submitStuData();
                }
                break;
            default:
                break;
        }
    }

    public void initData() {
        OkManager manager = OkManager.getInstance();
        Map<String, String> map = new HashMap<String, String>();
        OkManager.post(ApiConstants.teacherApi + "/showTeaStudentDefInfos", map, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "onFailure: ", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseBody = response.body().string();
                Log.e(TAG, responseBody);
                final JSONObject obj = JSON.parseObject(responseBody);
                final String msg = obj.getString("msg");
                Log.e(TAG, responseBody);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (obj.get("statusCode").equals(100)) {
                            JSONArray array = new JSONArray(obj.getJSONArray("data"));
                            for (int i = 0; i < array.size(); i++) {
                                list.add(array.getJSONObject(i).getString("name"));
                                Map<String, Object> map = new HashMap<>();
                                map.put("id",array.getJSONObject(i).getString("identifier"));
                                map.put("name",array.getJSONObject(i).getString("name"));
                                listID.add(map);
                            }
                        } else {
                            Toast.makeText(SendNoticeActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }

    public void submitData() {
        OkManager manager = OkManager.getInstance();

        Notice notice = new Notice();
        notice.setContent(notice_content.getText().toString().trim());
        notice.setRecvIds(listId);

        Log.w(TAG,notice_content.getText().toString().trim());
        Log.w(TAG,listId.toString());
        Gson gson = new Gson();
        //使用Gson将对象转换为json字符串
        String json = gson.toJson(notice);
        Log.w(TAG,json);

        RequestBody requestBody = FormBody.create(MediaType.parse("application/json; charset=utf-8"),json);
        OkManager.postJson(ApiConstants.commonApi + "/sendRoleNotification", requestBody, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "onFailure: ", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseBody = response.body().string();
                Log.e(TAG, responseBody);
                final JSONObject obj = JSON.parseObject(responseBody);
                final String msg = obj.getString("msg");
                Log.e(TAG, responseBody);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (obj.get("statusCode").equals(100)) {
                            finish();
                            Toast.makeText(SendNoticeActivity.this, msg, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SendNoticeActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }

    public void submitStuData() {
        OkManager manager = OkManager.getInstance();

        Notice notice = new Notice();
        notice.setContent(notice_content.getText().toString().trim());
        SharedPreferences sp=getSharedPreferences("personInfo", MODE_PRIVATE);
        BigInteger data = new BigInteger(sp.getString("tId",""));
        listStuId.add(data);
        notice.setRecvIds(listStuId);

        Log.w(TAG,notice_content.getText().toString().trim());
        Log.w(TAG,listId.toString());
        Gson gson = new Gson();
        //使用Gson将对象转换为json字符串
        String json = gson.toJson(notice);
        Log.w(TAG,json);

        RequestBody requestBody = FormBody.create(MediaType.parse("application/json; charset=utf-8"),json);
        OkManager.postJson(ApiConstants.commonApi + "/sendRoleNotification", requestBody, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "onFailure: ", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseBody = response.body().string();
                Log.e(TAG, responseBody);
                final JSONObject obj = JSON.parseObject(responseBody);
                final String msg = obj.getString("msg");
                Log.e(TAG, responseBody);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (obj.get("statusCode").equals(100)) {
                            finish();
                            Toast.makeText(SendNoticeActivity.this, msg, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SendNoticeActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }

    private AlertDialog alertDialog3;

    public void showMutilAlertDialog() {
        Log.e(TAG, list.toString());
        final String[] items = list.toArray(new String[list.size()]);
        List<Boolean> selectStatus = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            selectStatus.add(false);
        }

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle("发送消息");
        /**
         *第一个参数:弹出框的消息集合，一般为字符串集合
         * 第二个参数：默认被选中的，布尔类数组
         * 第三个参数：勾选事件监听
         */
        alertBuilder.setMultiChoiceItems(items, null, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i, boolean isChecked) {
                if (isChecked) {
                    if(listId.contains(listID.get(i).get("id").toString())){
                        BigInteger data = new BigInteger(listID.get(i).get("id").toString());
                        listId.remove(data);
                    }else{
                        Log.w(TAG,listID.get(i).get("id").toString());
                        BigInteger data = new BigInteger(listID.get(i).get("id").toString());
                        listId.add(data);
                    }
                    Toast.makeText(SendNoticeActivity.this, "选择" + items[i], Toast.LENGTH_SHORT).show();
                } else {
                    if(listId.contains(listID.get(i).get("id").toString())){
                        BigInteger data = new BigInteger(listID.get(i).get("id").toString());
                        listId.remove(data);
                    }else{
                        BigInteger data = new BigInteger(listID.get(i).get("id").toString());
                        listId.add(data);
                    }
                    Toast.makeText(SendNoticeActivity.this, "取消选择" + items[i], Toast.LENGTH_SHORT).show();
                }
            }
        });
        alertBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialog3.dismiss();
                submitData();
            }
        });

        alertBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialog3.dismiss();
            }
        });

        alertDialog3 = alertBuilder.create();
        alertDialog3.show();
    }
}