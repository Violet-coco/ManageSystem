package com.manage_system;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.manage_system.net.ApiConstants;
import com.manage_system.ui.personal.HelpActivity;
import com.manage_system.utils.OkManager;

import com.manage_system.utils.TimeCountUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;


public class LoginByPhoneActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText edit_phone;
    private EditText edit_cord;
    private TextView login_by_phone,help;
    private Button btn_checkCode;
    private Button btn_byphone_login;
    private TimeCountUtil mTimeCountUtil;

    private String path=ApiConstants.commonApi + "/sendBindCode";

    private String TAG = "发送验证码页面：";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ms_login_phone);
        getId();
        //这里的倒计时的时间 是 ：用第二参数 / 第三个三参数 = 倒计时为60秒
        mTimeCountUtil = new TimeCountUtil(btn_checkCode, 60000, 1000);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 获取id
     */
    private void getId()
    {
        edit_cord=(EditText)findViewById(R.id.edit_cord);
        btn_checkCode=(Button)findViewById(R.id.btn_checkCode);
        btn_byphone_login=(Button)findViewById(R.id.btn_byphone_login);
        login_by_phone=(TextView)findViewById(R.id.login_by_phone);
        help=(TextView)findViewById(R.id.help);
        btn_checkCode.setOnClickListener(this);
        btn_byphone_login.setOnClickListener(this);
        login_by_phone.setOnClickListener(this);
        help.setOnClickListener(this);
    }

    /**
     * 按钮点击事件
     */
    public void onClick(View v)
    {
        Intent intent;
        switch (v.getId())
        {
            case R.id.btn_checkCode:
                mTimeCountUtil.start();
                getCode();
                break;
            case R.id.btn_byphone_login:
                checkCode();
                break;
            case R.id.login_by_phone:
                intent = new Intent(LoginByPhoneActivity.this,LoginActivity.class);
                LoginByPhoneActivity.this.startActivity(intent);
                break;
            case R.id.help:
                intent = new Intent(LoginByPhoneActivity.this,HelpActivity.class);
                LoginByPhoneActivity.this.startActivity(intent);
                break;
            default:
                break;
        }
    }

    public void getCode(){
        edit_phone = (EditText) findViewById(R.id.edit_phone);
        String tel = edit_phone.getText().toString().trim();
        OkManager manager = OkManager.getInstance();
        Map<String, String> map = new HashMap<String, String>();
        map.put("tel",tel);
        manager.post(path, map,new okhttp3.Callback() {
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
                        if(obj.get("statusCode").equals(100)){
                            Toast.makeText(LoginByPhoneActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(LoginByPhoneActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }

    public void checkCode(){
        edit_phone = (EditText) findViewById(R.id.edit_phone);
        edit_cord = (EditText) findViewById(R.id.edit_cord);
        String tel = edit_phone.getText().toString().trim();
        String telCode = edit_cord.getText().toString().trim();
        OkManager manager = OkManager.getInstance();
        Map<String, String> map = new HashMap<String, String>();
        map.put("tel",tel);
        map.put("telCode",telCode);
        manager.post(ApiConstants.commonApi + "/telLogin", map,new okhttp3.Callback() {
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
                        if(obj.get("statusCode").equals(100)){
                            SharedPreferences sp=getSharedPreferences("loginInfo", MODE_PRIVATE);
                            //获取编辑器
                            SharedPreferences.Editor editor=sp.edit();
                            editor.putString("authority", obj.getJSONObject("data").get("authority").toString());
                            editor.putString("token", obj.getJSONObject("data").get("jwtToken").toString());
                            editor.putBoolean("isLogin", true);
                            editor.commit();
                            Toast.makeText(LoginByPhoneActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginByPhoneActivity.this,MainActivity.class);
                            startActivity(intent);
                        }else{
                            Toast.makeText(LoginByPhoneActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }

}