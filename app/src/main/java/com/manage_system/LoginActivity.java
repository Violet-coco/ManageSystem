package com.manage_system;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.manage_system.ui.personal.FindPasswordActivity;
import com.manage_system.ui.personal.HelpActivity;
import com.manage_system.utils.MD5Utils;
import com.manage_system.utils.OkManager;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;

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

    private final static int SUCCESS_SATUS = 1;
    private final static int FAILURE = 0;
//    private final static String Tag = LoginByPhoneActivity.class.getSimpleName();
    private final static String Tag = "11111111";

    private OkManager manager;

    private OkHttpClient clients;

    //登录验证请求
    private String login_path="http://www.yuanbw.cn:20086/gpms/rol/idloginMobile";
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
        //找回密码控件的点击事件
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
        //用于登录请求测试，登录用户名和登录密码应该Server上的对应
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, String> map = new HashMap<String, String>();
                map.put("id", "3120150905119");
                map.put("password", "123456");
                manager.sendComplexForm(login_path, map, new OkManager.Fun4() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Log.i(Tag, jsonObject.toString());
                    }
                });
            }
        });
        //登录按钮的点击事件
//        btn_login.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //开始登录，获取用户名和密码 getText().toString().trim();
//                userName=et_user_name.getText().toString().trim();
//                System.out.print(userName);
//                psw=et_psw.getText().toString().trim();
//                //对当前用户输入的密码进行MD5加密再进行比对判断, MD5Utils.md5( ); psw 进行加密判断是否一致
//                String md5Psw= MD5Utils.md5(psw);
//                // md5Psw ; spPsw 为 根据从SharedPreferences中用户名读取密码
//                // 定义方法 readPsw为了读取用户名，得到密码
//                spPsw=readPsw(userName);
//                // TextUtils.isEmpty
//                if(TextUtils.isEmpty(userName)){
//                    Toast.makeText(LoginActivity.this, "请输入账号", Toast.LENGTH_SHORT).show();
//                    return;
//                }else if(TextUtils.isEmpty(psw)){
//                    Toast.makeText(LoginActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
//                    return;
//                    // md5Psw.equals(); 判断，输入的密码加密后，是否与保存在SharedPreferences中一致
//                }else if(md5Psw.equals(spPsw)){
//                    //一致登录成功
//                    Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
//                    //保存登录状态，在界面保存登录的用户名 定义个方法 saveLoginStatus boolean 状态 , userName 用户名;
//                    saveLoginStatus(true, userName);
//                    //登录成功后关闭此页面进入主页
//                    Intent data=new Intent();
//                    //datad.putExtra( ); name , value ;
//                    data.putExtra("isLogin",true);
//                    //RESULT_OK为Activity系统常量，状态码为-1
//                    // 表示此页面下的内容操作成功将data返回到上一页面，如果是用back返回过去的则不存在用setResult传递data值
//                    setResult(RESULT_OK,data);
//                    //销毁登录界面
//                    LoginActivity.this.finish();
//                    //跳转到主界面，登录成功的状态传递到 LoginByPhoneActivity 中
//                    startActivity(new Intent(LoginActivity.this, LoginByPhoneActivity.class));
//                    return;
//                }else if((spPsw!=null&&!TextUtils.isEmpty(spPsw)&&!md5Psw.equals(spPsw))){
//                    Toast.makeText(LoginActivity.this, "输入的用户名和密码不一致", Toast.LENGTH_SHORT).show();
//                    return;
//                }else{
//                    Toast.makeText(LoginActivity.this, "此用户名不存在", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
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
    private void saveLoginStatus(boolean status,String userName){
        //saveLoginStatus(true, userName);
        //loginInfo表示文件名  SharedPreferences sp=getSharedPreferences("loginInfo", MODE_PRIVATE);
        SharedPreferences sp=getSharedPreferences("loginInfo", MODE_PRIVATE);
        //获取编辑器
        SharedPreferences.Editor editor=sp.edit();
        //存入boolean类型的登录状态
        editor.putBoolean("isLogin", status);
        //存入登录状态时的用户名
        editor.putString("loginUserName", userName);
        //提交修改
        editor.commit();
    }
    /**
     * 注册成功的数据返回至此
     * @param requestCode 请求码
     * @param resultCode 结果码
     * @param data 数据
     */
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
