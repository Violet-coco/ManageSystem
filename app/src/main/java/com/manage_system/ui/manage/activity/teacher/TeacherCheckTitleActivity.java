package com.manage_system.ui.manage.activity.teacher;

import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.manage_system.R;
import com.manage_system.net.ApiConstants;
import com.manage_system.ui.personal.GuideStudentInfoActivity;
import com.manage_system.utils.DownloadUtil;
import com.manage_system.utils.OkManager;
import com.manage_system.utils.OpenFileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

public class TeacherCheckTitleActivity extends AppCompatActivity implements View.OnClickListener {

    private String id = null;
    private String sid = null;
    private Context mContext;
    private String fileName = null;


    @BindView(R.id.ot_title)
    EditText ot_title;
    @BindView(R.id.ot_source)
    EditText ot_source;
    @BindView(R.id.ot_type)
    EditText ot_type;
    @BindView(R.id.ot_name_id)
    TextView ot_name_id;
    @BindView(R.id.ot_guide_teacher)
    EditText ot_guide_teacher;
    @BindView(R.id.ot_state)
    EditText ot_state;
    @BindView(R.id.ot_pass)
    Button ot_pass;
    @BindView(R.id.ot_no_pass)
    Button ot_no_pass;
    @BindView(R.id.ot_check_pass)
    LinearLayout ot_check_pass;
    private static String TAG = "TeacherCheckTitleActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ms_tm_check_ct_item);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        Log.w(TAG,id+"哈哈"+intent.getStringExtra("position"));

        SharedPreferences sp=getSharedPreferences("processData", MODE_PRIVATE);
        Log.w(TAG,sp.getString("teacher_checkTitle" , ""));
        JSONObject obj = JSON.parseObject(sp.getString("teacher_checkTitle" , ""));
        JSONArray array = new JSONArray(obj.getJSONObject("data").getJSONArray("chooseList"));
        JSONObject object = array.getJSONObject(Integer.parseInt(intent.getStringExtra("position")));
        JSONObject project = object.getJSONObject("project");
        JSONObject student = object.getJSONObject("student");
        sid = student.getString("identifier");
        SharedPreferences.Editor editor=sp.edit();
        editor.putString("teacher_checkTitle_detail",object.toString());
        editor.putString("student",student.toString());
        editor.commit();
        Log.e(TAG,object.toString());
        ot_title.setText(project.getString("title"));
        ot_title.setEnabled(false);
        ot_title.setTextColor(Color.parseColor("#666666"));
        ot_type.setText(project.getString("genre"));
        ot_source.setText(project.getString("source"));
        ot_name_id.setText(Html.fromHtml("<u>"+student.getString("name")+"（"+student.getString("identifier")+"）"+"</u>"));
        ot_guide_teacher.setText(student.getString("tName"));
        String cStatus = null;
        if(object.getString("cStatus").equals("0")){
            cStatus = "审核不通过";
            ot_check_pass.setVisibility(View.GONE);
        }else if(object.getString("cStatus").equals("1")){
            cStatus = "审核通过";
            ot_check_pass.setVisibility(View.GONE);
        }else if(object.getString("cStatus").equals("2")|| object.getString("cStatus").equals("3")){
            cStatus = "审核中";
        }
        ot_state.setText(cStatus);
    }

    /**启动这个Activity的Intent
     * @param context
     * @return
     */
    public static Intent createIntent(Context context) {
        return new Intent(context, TeacherCheckTitleActivity.class);
    }

    @OnClick({R.id.iv_back,R.id.ot_title,R.id.ot_name_id,R.id.ot_pass,R.id.ot_no_pass})
    public void onClick(View v) {//直接调用不会显示v被点击效果
        Intent intent;
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.ot_title:
                intent = new Intent(TeacherCheckTitleActivity.this,TeacherOutTitleDetailActivity.class);
                intent.putExtra("from_way","ot_check");
                startActivity(intent);
                break;
            case R.id.ot_name_id:
                intent = new Intent(TeacherCheckTitleActivity.this,GuideStudentInfoActivity.class);
                intent.putExtra("stu_info","");
                startActivity(intent);
                break;
            case R.id.ot_pass:
                isPass("2");
                break;
            case R.id.ot_no_pass:
                isPass("0");
                break;
            default:
                break;
        }
    }

    public void isPass(String status) {
        OkManager manager = OkManager.getInstance();
        Map<String, String> map = new HashMap<String, String>();
        map.put("sid",sid);
        map.put("pid", id);
//        status   (审核结果,0或2)
        map.put("status",status);
        OkManager.post(ApiConstants.teacherApi+"/reviewProChoose", map,new okhttp3.Callback() {
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
                            Toast.makeText(TeacherCheckTitleActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(TeacherCheckTitleActivity.this,TeacherOutTitleMainActivity.class);
                            startActivity(intent);
                            finish();
                        }else{
                            Toast.makeText(TeacherCheckTitleActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }

    public void showDialog(){
        final Dialog dialog = new Dialog(TeacherCheckTitleActivity.this, R.style.MyDialog);
        //设置它的ContentView
        dialog.setContentView(R.layout.alert_dialog);
        ((TextView)dialog.findViewById(R.id.tvAlertDialogTitle)).setText("删除选题");
        ((TextView)dialog.findViewById(R.id.tvAlertDialogMessage)).setText("确认删除该选题？");
        dialog.show();

        Button confirm = (Button)dialog.findViewById(R.id.btnAlertDialogPositive);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                OkManager manager = OkManager.getInstance();
                Map<String, String> map = new HashMap<String, String>();
                Log.w(TAG,id+"嘻嘻");
                map.put("pid", id);
                OkManager.post(ApiConstants.teacherApi+"/deleteProject", map,new okhttp3.Callback() {
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
                                    Toast.makeText(TeacherCheckTitleActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(TeacherCheckTitleActivity.this,TeacherOutTitleMainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }else{
                                    Toast.makeText(TeacherCheckTitleActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
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

    public void downLoad(String title,String message,final String fileId){
        final Dialog dialog = new Dialog(TeacherCheckTitleActivity.this, R.style.MyDialog);
        //设置它的ContentView
        dialog.setContentView(R.layout.alert_dialog);
        ((TextView)dialog.findViewById(R.id.tvAlertDialogTitle)).setText(title);
        ((TextView)dialog.findViewById(R.id.tvAlertDialogMessage)).setText(message);
        dialog.show();

        Button confirm = (Button)dialog.findViewById(R.id.btnAlertDialogPositive);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Map<String, String> map = new HashMap<String, String>();
                map.put("fileId",fileId);
                Log.w(TAG,fileId+"嘻嘻");
                //文件在手机内存存储的路径
                final String saveurl="/download/";
                Log.w(TAG,"路径1："+saveurl);
                //配置progressDialog
                final ProgressDialog dialog= new ProgressDialog(TeacherCheckTitleActivity.this);
                dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                dialog.setCanceledOnTouchOutside(false);
                dialog.setCancelable(true);
                dialog.setTitle("正在下载中");
                dialog.setMessage("请稍后...");
                dialog.setProgress(0);
                dialog.setMax(100);
                dialog.show();
                startDownload(dialog,fileId,saveurl);

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

    public void startDownload(final ProgressDialog dialog,String fileId,String saveurl){
        //启动下载方法
        DownloadUtil.get().download(TeacherCheckTitleActivity.this, ApiConstants.commonApi + "/downloadFile"+"?fileId="+fileId,saveurl,fileName,  new DownloadUtil.OnDownloadListener() {
            @Override
            public void onDownloadSuccess() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getNotificationManager().notify(1,getNotification("文件下载成功，点击进行查看",-1));
                        Toast.makeText(TeacherCheckTitleActivity.this, "下载成功", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();

                        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

                            return;
                        }

                        File file = new File(Environment.getExternalStorageDirectory().getPath() + "/download/"+fileName);
                        Log.w(TAG,"路径2："+file);
                        try {
                            Log.w(TAG,"打开");
                            OpenFileUtils.openFile(mContext, file);
                        } catch (Exception e) {
                            Log.w(TAG,"无打开方式");
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void onDownloading(int progress) {

                dialog.setProgress(progress);
                getNotificationManager().notify(1,getNotification("下载中...",progress));
            }

            @Override
            public void onDownloadFailed() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getNotificationManager().notify(1,getNotification("下载失败",-1));
                        Toast.makeText(TeacherCheckTitleActivity.this, "下载失败", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
            }
        });
    }

    private NotificationManager getNotificationManager(){
        return (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    // 显示对话框；
    private Notification getNotification(String title, int progress) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(TeacherCheckTitleActivity.this);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle(title);
        if (progress > 0) {
            builder.setContentText(progress+"%");
            builder.setProgress(100,progress,false);
        }
        return builder.build();
    }
}