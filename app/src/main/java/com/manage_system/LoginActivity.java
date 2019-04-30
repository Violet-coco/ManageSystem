package com.manage_system;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.manage_system.net.ApiConstants;
import com.manage_system.ui.personal.EditPasswordActivity;
import com.manage_system.ui.personal.FindPasswordActivity;
import com.manage_system.ui.personal.HelpActivity;
import com.manage_system.utils.MD5Utils;
import com.manage_system.utils.OkManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    //标题
    private TextView tv_main_title;
    //返回键,手机号登陆，忘记密码，帮助
    private TextView tv_back,login_by_phone,forget_password,help;
    //登录按钮
    private Button btn_login;
    //获取的用户名，密码，加密密码
    private String userName,psw,spPsw;
    //编辑框
    private EditText et_user_name,et_psw;
    //复选框
    private CheckBox remember_password;

    private final static int SUCCESS_SATUS = 1;
    private final static int FAILURE = 0;
    private final static String Tag = "11111111";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private OkManager manager;

    private OkHttpClient clients;

    //登录验证请求
    private String login_path = ApiConstants.Api + ApiConstants.commonApi + "/idloginMobile";
//    private String login_path="http://www.yuanbw.cn:20086/gpms/rol/idloginMobile";

    private String person_info_path = ApiConstants.Api + ApiConstants.commonApi + "/showRoleInfo";
//    private String person_info_path="http://www.yuanbw.cn:20086/gpms/rol/showRoleInfo";
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ms_login);
        //设置此界面为竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        init();
    }
    //获取界面控件
    private void init() {
        //从main_title_bar中获取的id
        tv_main_title=(TextView) findViewById(R.id.tv_main_title);
        tv_main_title.setText("登录");
        tv_back=(TextView)findViewById(R.id.tv_back);
        //从login.xml中获取的
        forget_password=(TextView)findViewById(R.id.forget_password);
        btn_login=(Button)findViewById(R.id.btn_login);
        et_user_name=(EditText)findViewById(R.id.et_user_name);
        et_psw=(EditText) findViewById(R.id.et_psw);
        login_by_phone=(TextView) findViewById(R.id.login_by_phone);
        help=(TextView)findViewById(R.id.help);
        //是否记住密码
        remember_password = (CheckBox) findViewById(R.id.remember_password);
        //返回键的点击事件
        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //登录界面销毁
                LoginActivity.this.finish();
            }
        });
        //切换为手机账号登陆
        login_by_phone.setOnClickListener(new View.OnClickListener(){
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(LoginActivity.this,LoginByPhoneActivity.class);
               LoginActivity.this.startActivity(intent);
           }
        });
        //找回密码控件的点击事件
        forget_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到找回密码界面
                Intent intent = new Intent(LoginActivity.this,FindPasswordActivity.class);
                LoginActivity.this.startActivity(intent);
            }
        });
        //记住密码的点击事件
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isRemember =sharedPreferences.getBoolean("remember_password",false);
        if(isRemember)
        {
            et_user_name.setText(sharedPreferences.getString("et_user_name",""));
            et_psw.setText(sharedPreferences.getString("et_psw",""));
            remember_password.setChecked(true);
        }

        //帮助的点击事件
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到找回密码界面
                Intent intent = new Intent(LoginActivity.this,HelpActivity.class);
                LoginActivity.this.startActivity(intent);
            }
        });

        //-----------------------------------------------------------------------------------------
        manager = OkManager.getInstance();
        //-------------------------------------------------------------------------

        //登录按钮的点击事件
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sp1=getSharedPreferences("loginInfo", MODE_PRIVATE);
                Log.w(Tag,sp1.getString("token","")+"登录");
                Log.w(Tag,"点击登录");

                //开始登录，获取用户名和密码 getText().toString().trim();
                userName=et_user_name.getText().toString().trim();
                psw=et_psw.getText().toString().trim();

                //对当前用户输入的密码进行MD5加密再进行比对判断, MD5Utils.md5( ); psw 进行加密判断是否一致
                String md5Psw= MD5Utils.md5(psw);
                // md5Psw ; spPsw 为 根据从SharedPreferences中用户名读取密码
                // 定义方法 readPsw为了读取用户名，得到密码
                spPsw=readPsw(userName);

                // TextUtils.isEmpty
                if(TextUtils.isEmpty(userName)){
                    Toast.makeText(LoginActivity.this, "请输入账号", Toast.LENGTH_SHORT).show();
                    return;
                }else if(TextUtils.isEmpty(psw)){
                    Toast.makeText(LoginActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                    return;
                }else if((spPsw!=null&&!TextUtils.isEmpty(spPsw)&&!md5Psw.equals(spPsw))){
                    Toast.makeText(LoginActivity.this, "输入的用户名和密码不一致", Toast.LENGTH_SHORT).show();
                    return;
                }
//                else{
//                    Toast.makeText(LoginActivity.this, "此用户名不存在", Toast.LENGTH_SHORT).show();
//                }

                // 连接接口
                Map<String, String> map = new HashMap<String, String>();
                map.put("id", userName);
                map.put("password", psw);
                Log.w(Tag,userName);
                Log.w(Tag,psw);

                manager.post(login_path, map,new okhttp3.Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e(Tag, "onFailure: ",e);
                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String responseBody = response.body().string();
                        final JSONObject obj = JSON.parseObject(responseBody);
                        Log.e(Tag,obj.toString());
                        final String msg = obj.getString("msg");

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(obj.get("statusCode").equals(100)|| MD5Utils.md5(psw).equals(spPsw)){
                                    final String authority  = obj.getJSONObject("data").get("authority").toString();
                                    final String token = obj.getJSONObject("data").get("jwtToken").toString();
                                    Log.w(Tag,"token");
                                    //保存登录状态，在界面保存登录的用户名 定义个方法 saveLoginStatus boolean 状态 , userName 用户名;
                                    saveLoginStatus(true, userName,authority,token);
                                    //一致登录成功
                                    Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                                    //登录成功后关闭此页面进入主页
                                    Intent data=new Intent();
                                    //datad.putExtra( ); name , value ;
                                    data.putExtra("isLogin",true);
                                    //RESULT_OK为Activity系统常量，状态码为-1
                                    // 表示此页面下的内容操作成功将data返回到上一页面，如果是用back返回过去的则不存在用setResult传递data值
                                    setResult(RESULT_OK,data);
                                    if(authority.equals("1")){
                                        Log.i(Tag, msg);
                                        editor=sharedPreferences.edit();
                                        if(remember_password.isChecked())
                                        {
                                            editor.putString("et_user_name",userName);
                                            editor.putString("et_psw",psw);
                                            editor.putBoolean("remember_password",true);
                                        }
                                        else {
                                            editor.clear();
                                        }
                                        editor.apply();

                                        // 连接接口
                                        Map<String, String> map1 = new HashMap<String, String>();
                                        manager.post(person_info_path, map1,new okhttp3.Callback() {
                                            @Override
                                            public void onFailure(Call call, IOException e) {
                                                Log.e(Tag, "onFailure: ",e);
                                            }
                                            @Override
                                            public void onResponse(Call call, Response response) throws IOException {
                                                String responseBody = response.body().string();
                                                final JSONObject obj = JSON.parseObject(responseBody);
                                                savePersonStatus( obj.getJSONObject("data").getString("name"),obj.getJSONObject("data").getString("identifier"));
                                                Log.e(Tag,obj.toString());
                                                Log.e(Tag,obj.getJSONObject("data").getString("name"));
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        SharedPreferences sp = getSharedPreferences("personInfo", MODE_PRIVATE);
                                                        //获取编辑器
                                                        SharedPreferences.Editor editor = sp.edit();
                                                        editor.putString("identifier", obj.getJSONObject("data").getString("identifier"));
                                                        editor.putString("name", obj.getJSONObject("data").getString("name"));
                                                        editor.putString("college", obj.getJSONObject("data").getString("college"));
                                                        editor.putString("sex", obj.getJSONObject("data").getString("sex"));
                                                        editor.putString("contactTel", obj.getJSONObject("data").getString("contactTel"));
                                                        editor.putString("bindTel", obj.getJSONObject("data").getString("bindTel"));
                                                        editor.putString("major", obj.getJSONObject("data").getString("major"));
                                                        editor.putString("grade", obj.getJSONObject("data").getString("grade"));
                                                        editor.putString("classNo", obj.getJSONObject("data").getString("classNo"));
                                                        editor.putString("department", obj.getJSONObject("data").getString("department"));
                                                        editor.putString("email", obj.getJSONObject("data").getString("email"));
                                                        //提交修改
                                                        editor.commit();
                                                    }
                                                });

                                            }
                                        });

//                                        manager.sendComplexForm(person_info_path, map1, new OkManager.Fun4() {
//                                            @Override
//                                            public void onResponse(org.json.JSONObject jsonObject) {
//                                                JSONObject obj = JSON.parseObject(jsonObject.toString());
//                                                Log.w(Tag,obj.toString());
//                                                SharedPreferences sp=getSharedPreferences("personInfo", MODE_PRIVATE);
//                                                //获取编辑器
//                                                SharedPreferences.Editor editor=sp.edit();
//                                                editor.putString("identifier",obj.getJSONObject("data").getString("identifier"));
//                                                editor.putString("name", obj.getJSONObject("data").getString("name"));
//                                                editor.putString("college", obj.getJSONObject("data").getString("college"));
//                                                editor.putString("sex", obj.getJSONObject("data").getString("sex"));
//                                                editor.putString("contactTel", obj.getJSONObject("data").getString("contactTel"));
//                                                editor.putString("bindTel", obj.getJSONObject("data").getString("bindTel"));
//                                                editor.putString("major", obj.getJSONObject("data").getString("major"));
//                                                editor.putString("grade", obj.getJSONObject("data").getString("grade"));
//                                                editor.putString("classNo", obj.getJSONObject("data").getString("classNo"));
//                                                editor.putString("department", obj.getJSONObject("data").getString("department"));
//                                                editor.putString("email", obj.getJSONObject("data").getString("email"));
//                                                //提交修改
//                                                editor.commit();
//                                            }
//                                        });

                                        Toast.makeText(LoginActivity.this, "登陆成功", Toast.LENGTH_SHORT).show();
                                        //跳转到主界面，登录成功的状态传递到 MainActivity 中
                                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                        //销毁登录界面
                                        finish();
                                    }

                                }else{
                                    Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }
                        });

                    }
                });
//                manager.sendComplexForm(login_path, map, new OkManager.Fun4() {
//                    @Override
//                    public void onResponse(org.json.JSONObject jsonObject) {
//                        Log.i(Tag, jsonObject.toString());
//                        JSONObject obj = JSON.parseObject(jsonObject.toString());
//                        String msg = obj.getString("msg");
//                        String authority = null;
//                        String token = null;
//                        if(!TextUtils.isEmpty(obj.getString("data")) ){
//                            authority = obj.getJSONObject("data").get("authority").toString();
//                            token = obj.getJSONObject("data").get("jwtToken").toString();
//                        }
//                        Log.w(Tag,"token");
//                        //保存登录状态，在界面保存登录的用户名 定义个方法 saveLoginStatus boolean 状态 , userName 用户名;
//                        saveLoginStatus(true, userName,authority,token);
//                        //一致登录成功
//                        Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
//                        //登录成功后关闭此页面进入主页
//                        Intent data=new Intent();
//                        //datad.putExtra( ); name , value ;
//                        data.putExtra("isLogin",true);
//                        //RESULT_OK为Activity系统常量，状态码为-1
//                        // 表示此页面下的内容操作成功将data返回到上一页面，如果是用back返回过去的则不存在用setResult传递data值
//                        setResult(RESULT_OK,data);
//                        // md5Psw.equals(); 判断，输入的密码加密后，是否与保存在SharedPreferences中一致
//                        if(obj.get("statusCode").equals(100)|| MD5Utils.md5(psw).equals(spPsw)){
//                            if(authority.equals("1")){
//                                Log.i(Tag, msg);
//                                editor=sharedPreferences.edit();
//                                if(remember_password.isChecked())
//                                {
//                                    editor.putString("et_user_name",userName);
//                                    editor.putString("et_psw",psw);
//                                    editor.putBoolean("remember_password",true);
//                                }
//                                else {
//                                    editor.clear();
//                                }
//                                editor.apply();
//
//                                // 连接接口
//                                Map<String, String> map1 = new HashMap<String, String>();
//                                manager.sendComplexForm(person_info_path, map1, new OkManager.Fun4() {
//                                    @Override
//                                    public void onResponse(org.json.JSONObject jsonObject) {
//                                        JSONObject obj = JSON.parseObject(jsonObject.toString());
//                                        Log.w(Tag,obj.toString());
//                                        SharedPreferences sp=getSharedPreferences("personInfo", MODE_PRIVATE);
//                                        //获取编辑器
//                                        SharedPreferences.Editor editor=sp.edit();
//                                        editor.putString("identifier",obj.getJSONObject("data").getString("identifier"));
//                                        editor.putString("name", obj.getJSONObject("data").getString("name"));
//                                        editor.putString("college", obj.getJSONObject("data").getString("college"));
//                                        editor.putString("sex", obj.getJSONObject("data").getString("sex"));
//                                        editor.putString("contactTel", obj.getJSONObject("data").getString("contactTel"));
//                                        editor.putString("bindTel", obj.getJSONObject("data").getString("bindTel"));
//                                        editor.putString("major", obj.getJSONObject("data").getString("major"));
//                                        editor.putString("grade", obj.getJSONObject("data").getString("grade"));
//                                        editor.putString("classNo", obj.getJSONObject("data").getString("classNo"));
//                                        editor.putString("department", obj.getJSONObject("data").getString("department"));
//                                        editor.putString("email", obj.getJSONObject("data").getString("email"));
//                                        //提交修改
//                                        editor.commit();
//                                    }
//                                });
//
//                                Toast.makeText(LoginActivity.this, "登陆成功", Toast.LENGTH_SHORT).show();
//                                //跳转到主界面，登录成功的状态传递到 MainActivity 中
//                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
//                                //销毁登录界面
//                                finish();
//                            }
//
//                        }else{
//                            Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
//                            return;
//                        }
//                    }
//                });
            }
        });
    }
    /**
     *从SharedPreferences中根据用户名读取密码
     */
    private String readPsw(String userName){
        //getSharedPreferences("loginInfo",MODE_PRIVATE);
        //"loginInfo",mode_private; MODE_PRIVATE表示可以继续写入
        SharedPreferences sp=getSharedPreferences("loginInfo", MODE_PRIVATE);
        //sp.getString() userName, "";
        return sp.getString(userName , "");
    }
    /**
     *保存登录状态和登录用户名到SharedPreferences中
     */
    private void saveLoginStatus(boolean status,String userName,String authority,String token){
        //saveLoginStatus(true, userName);
        //loginInfo表示文件名  SharedPreferences sp=getSharedPreferences("loginInfo", MODE_PRIVATE);
        SharedPreferences sp=getSharedPreferences("loginInfo", MODE_PRIVATE);
        //获取编辑器
        SharedPreferences.Editor editor=sp.edit();
        //存入boolean类型的登录状态
        editor.putBoolean("isLogin", status);
        //存入登录状态时的用户名
        editor.putString("loginUserName", userName);
        //存入用户身份
        editor.putString("authority", authority);
        //存入token
        editor.putString("token", token);
        //提交修改
        editor.commit();
    }

    private void savePersonStatus(String name,String id) {
        SharedPreferences sp=getSharedPreferences("loginInfo", MODE_PRIVATE);
        //获取编辑器
        SharedPreferences.Editor editor=sp.edit();
        editor.putString("name", name);
        editor.putString("id", id);
        Log.w(Tag,"名字是"+name);
        //提交修改
        editor.commit();
    }

    @Override
    //显示数据， onActivityResult
    //startActivityForResult(intent, 1); 从注册界面中获取数据
    //int requestCode , int resultCode , Intent data
    // LoginActivity -> startActivityForResult -> onActivityResult();
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if(data!=null){
            //是获取注册界面回传过来的用户名
            // getExtra().getString("***");
            String userName=data.getStringExtra("userName");
            if(!TextUtils.isEmpty(userName)){
                //设置用户名到 et_user_name 控件
                et_user_name.setText(userName);
                //et_user_name控件的setSelection()方法来设置光标位置
                et_user_name.setSelection(userName.length());
            }
        }
    }
}
