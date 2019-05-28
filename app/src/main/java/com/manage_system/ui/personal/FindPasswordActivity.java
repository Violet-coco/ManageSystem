package com.manage_system.ui.personal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.manage_system.LoginActivity;
import com.manage_system.LoginByPhoneActivity;
import com.manage_system.R;
import com.manage_system.net.ApiConstants;
import com.manage_system.utils.OkManager;
import com.manage_system.utils.TimeCountUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

public class FindPasswordActivity extends Activity implements View.OnClickListener  {

    @BindView(R.id.iv_back)
    ImageButton iv_back;
    @BindView(R.id.bind_phone)
    EditText bind_phone;
    @BindView(R.id.btn_checkCode)
    Button btn_checkCode;
    @BindView(R.id.edit_cord)
    EditText edit_cord;
    @BindView(R.id.psw_one)
    EditText psw_one;
    @BindView(R.id.psw_two)
    EditText psw_two;
    @BindView(R.id.check_main)
    LinearLayout check_main;
    @BindView(R.id.psw_main)
    LinearLayout psw_main;
    @BindView(R.id.btn_check)
    Button btn_check;
    @BindView(R.id.btn_submit)
    Button btn_submit;
    private String TAG = "FindPasswordActivity";
    private String tel;
    private TimeCountUtil mTimeCountUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ms_forget_password);
        ButterKnife.bind(this);
        psw_main.setVisibility(View.GONE);
        mTimeCountUtil = new TimeCountUtil(btn_checkCode, 60000, 1000);
    }


    @OnClick({R.id.iv_back,R.id.btn_checkCode,R.id.btn_check,R.id.btn_submit})
    public void onClick(View v) {//直接调用不会显示v被点击效果
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.btn_checkCode:
                mTimeCountUtil.start();
                getBindPhone();
                break;
            case R.id.btn_check:
                checkCode();
                break;
            case R.id.btn_submit:
                setPassword();
                break;
            default:
                break;
        }
    }

    public void getBindPhone(){
        final OkManager manager = OkManager.getInstance();
        Map<String, String> map = new HashMap<>();
        map.put("id",bind_phone.getText().toString().trim());
        manager.post(ApiConstants.commonApi+"/forgetPwd_find", map,new okhttp3.Callback() {
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
                            Map<String, String> map = new HashMap<>();
                            tel = obj.getJSONObject("data").getString("bindTel");
                            map.put("tel",obj.getJSONObject("data").getString("bindTel"));
                            manager.post(ApiConstants.commonApi+"/sendBindCode", map,new okhttp3.Callback() {
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
                                                Toast.makeText(FindPasswordActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                                            }else{
                                                Toast.makeText(FindPasswordActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                                }
                            });
                        }else if(obj.get("statusCode").equals(102)){
                            Toast.makeText(FindPasswordActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(FindPasswordActivity.this,LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }else{
                            Toast.makeText(FindPasswordActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }

    public void checkCode(){
        OkManager manager = OkManager.getInstance();
        Map<String, String> map = new HashMap<>();
        map.put("tel",tel);
        map.put("telCode",edit_cord.getText().toString().trim());
        manager.post(ApiConstants.commonApi+"/forgetPwd_check", map,new okhttp3.Callback() {
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
                            check_main.setVisibility(View.GONE);
                            psw_main.setVisibility(View.VISIBLE);
                            Toast.makeText(FindPasswordActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                        }else if(obj.get("statusCode").equals(102)){
                            Toast.makeText(FindPasswordActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(FindPasswordActivity.this,LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }else{
                            Toast.makeText(FindPasswordActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }

    public void setPassword(){
        OkManager manager = OkManager.getInstance();
        Map<String, String> map = new HashMap<>();
        map.put("id",bind_phone.getText().toString().trim());
        map.put("pwd",psw_one.getText().toString().trim());
        map.put("pwd_second",psw_two.getText().toString().trim());
        Log.w(TAG,bind_phone.getText().toString().trim());
        Log.w(TAG,psw_one.getText().toString().trim());
        Log.w(TAG,psw_two.getText().toString().trim());
        manager.post(ApiConstants.commonApi+"/forgetPwd_update_Mobile", map,new okhttp3.Callback() {
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
                            Intent intent = new Intent(FindPasswordActivity.this,LoginActivity.class);
                            startActivity(intent);
                            finish();
                            Toast.makeText(FindPasswordActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                        }else if(obj.get("statusCode").equals(102)){
                            Toast.makeText(FindPasswordActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(FindPasswordActivity.this,LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }else{
                            Toast.makeText(FindPasswordActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }
}