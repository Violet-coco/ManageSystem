package com.manage_system.ui.personal;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.manage_system.LoginActivity;
import com.manage_system.R;
import com.manage_system.utils.OkManager;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

public class EditPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    ImageButton iv_back;
    EditText old_password,new_password,et_password;
    Button btn_edit_password;

    //登录验证请求
    private String path="http://www.yuanbw.cn:20086/gpms/rol/modifyPassword";

    private String TAG = "修改密码页面：";

    private String old_pwd,pwd,pwd_second;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ms_person_edit_password);
        getId();
    }

    /**启动这个Activity的Intent
     * @param context
     * @return
     */
    public static Intent createIntent(Context context) {
        return new Intent(context, EditPasswordActivity.class);
    }

    /**
     * 获取id
     */
    private void getId()
    {
        iv_back = (ImageButton)findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);

        old_password = (EditText) findViewById(R.id.old_password);
        new_password = (EditText)findViewById(R.id.new_password);
        et_password = (EditText)findViewById(R.id.et_password);
        btn_edit_password = (Button)findViewById(R.id.btn_edit_password);
        btn_edit_password.setOnClickListener(this);
    }

    public void onClick(View v) {//直接调用不会显示v被点击效果
        switch (v.getId()) {
            case R.id.iv_back:
                onViewClicked();
                break;
            case R.id.btn_edit_password:
                onSubmitEdit();
                break;
            default:
                break;
        }
    }

    public void onViewClicked() {
        finish();
    }

    private void onSubmitEdit() {
        old_pwd = old_password.getText().toString().trim();
        pwd = new_password.getText().toString().trim();
        pwd_second = et_password.getText().toString().trim();
        // 连接接口
        OkManager manager = OkManager.getInstance();
        Map<String, String> map = new HashMap<String, String>();
        map.put("old_pwd", old_pwd);
        map.put("pwd", pwd);
        map.put("pwd_second", pwd_second);

        Log.w(TAG,old_pwd);
        Log.w(TAG,pwd);
        Log.w(TAG,pwd_second);
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
                        if(obj.get("statusCode").equals(100)) {
                            Toast.makeText(EditPasswordActivity.this, obj.getString("msg")+",请重新登录", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(EditPasswordActivity.this,LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }else{
                            Toast.makeText(EditPasswordActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

//        manager.sendComplexForm(path, map, new OkManager.Fun4() {
//
//            @Override
//            public void onResponse(org.json.JSONObject jsonObject) {
//                final JSONObject obj = JSON.parseObject(jsonObject.toString());
//                Log.w(TAG,obj.toString());
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if(obj.get("statusCode").equals(100)) {
//                            Toast.makeText(EditPasswordActivity.this, obj.getString("msg")+",请重新登录", Toast.LENGTH_SHORT).show();
//                            Intent intent = new Intent(EditPasswordActivity.this,LoginActivity.class);
//                            startActivity(intent);
//                            finish();
//                        }else{
//                            Toast.makeText(EditPasswordActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//            }
//            @Override
//            public void onResponse(org.json.JSONObject jsonObject) {
//                JSONObject obj = JSON.parseObject(jsonObject.toString());
//                Log.w(TAG,obj.toString());
//                if(obj.get("statusCode").equals(100)){
//                    Toast.makeText(EditPasswordActivity.this, obj.getString("msg")+",请重新登录", Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(EditPasswordActivity.this,LoginActivity.class);
//                    startActivity(intent);
//                    finish();
//                }else {
//                    Toast.makeText(EditPasswordActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
//
//                }
//            }


//        });

    }

}