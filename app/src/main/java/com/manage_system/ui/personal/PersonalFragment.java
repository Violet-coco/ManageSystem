package com.manage_system.ui.personal;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.manage_system.BuildConfig;
import com.manage_system.R;
import com.manage_system.component.ApplicationComponent;
import com.manage_system.net.ApiConstants;
import com.manage_system.test.ClipImageActivity;
import com.manage_system.test.util.FileUtil;
import com.manage_system.test.view.CircleImageView;
import com.manage_system.ui.base.BaseFragment;
import com.manage_system.ui.manage.Manage;
import com.manage_system.utils.OkManager;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.manage_system.test.util.FileUtil.getRealFilePathFromUri;

public class PersonalFragment extends BaseFragment implements View.OnClickListener{
    @BindView(R.id.person_icon)
    ImageView personIcon;
    @BindView(R.id.person_info)
    LinearLayout personInfo;
    @BindView(R.id.person_edit_password)
    LinearLayout personEditPassword;
    @BindView(R.id.person_exit)
    LinearLayout personExit;

    //头像1
    private CircleImageView person_icon;

    //调用照相机返回图片文件
    private File tempFile;

    private int type;

    private OkManager manager;

    public static PersonalFragment newInstance() {
        Bundle args = new Bundle();
        PersonalFragment fragment = new PersonalFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getContentLayout() {
        return R.layout.ms_personal_fragment;
    }

    @Override
    public void initInjector(ApplicationComponent appComponent) {

    }

    @Override
    public void bindView(View view, Bundle savedInstanceState) {
        final TextView person_name=(TextView) view.findViewById(R.id.person_name);
        final TextView person_id=(TextView) view.findViewById(R.id.person_id);

        OkManager manager = OkManager.getInstance();
        Map<String, String> map = new HashMap<String, String>();
        manager.post(ApiConstants.commonApi + "/showRoleInfo", map,new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "onFailure: ",e);
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseBody = response.body().string();
                Log.e(TAG,responseBody);
                final JSONObject obj = JSON.parseObject(responseBody);
                if(obj.get("statusCode").equals(100)){
                    person_name.setText(obj.getJSONObject("data").getString("name"));
                    person_id.setText(obj.getJSONObject("data").getString("identifier"));
                }

            }
        });
        Log.w(TAG,"hhh");
    }

    public void initPersonData() {
        Log.e(TAG,"调用了");

    }

    public void showDialog(){
        final Dialog dialog = new Dialog(getActivity(), R.style.MyDialog);
        //设置它的ContentView
        dialog.setContentView(R.layout.alert_dialog);
        ((TextView)dialog.findViewById(R.id.tvAlertDialogTitle)).setText("退出登录");
        ((TextView)dialog.findViewById(R.id.tvAlertDialogMessage)).setText("确定退出登录？");
        dialog.show();

        Button confirm = (Button)dialog.findViewById(R.id.btnAlertDialogPositive);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Toast.makeText(getActivity(), "退出成功", Toast.LENGTH_SHORT).show();
                Manage.startClear();
                // 连接接口
                manager = OkManager.getInstance();
                Map<String, String> map = new HashMap<String, String>();

                manager.post(ApiConstants.commonApi+"/logout", map,new okhttp3.Callback() {
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
                                Toast.makeText(getActivity(), obj.getString("msg"), Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });
                getActivity().finish();
                System.exit(0);
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

    private static final String TAG = "个人页面";

    @OnClick({R.id.person_icon,R.id.person_info,R.id.person_edit_password,R.id.person_exit})
    public void onClick(View v) {//直接调用不会显示v被点击效果
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.person_icon:
                type = 1;
                break;
            case R.id.person_info:
                intent.setClass(this.getActivity(), PersonInfoActivity.class);
                startActivity(intent);
                break;
            case R.id.person_edit_password:
                intent.setClass(this.getActivity(), EditPasswordActivity.class);
                startActivity(intent);
                break;
            case R.id.person_exit:
                showDialog();
                break;
            default:
                break;
        }
    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {

    }

    @Override
    public void initEvent() {

    }

    @Override
    public boolean isRunning() {
        return false;
    }


    @Override
    public void onRetry() {

    }
}
