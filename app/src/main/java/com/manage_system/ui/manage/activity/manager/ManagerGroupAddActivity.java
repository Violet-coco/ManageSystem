package com.manage_system.ui.manage.activity.manager;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.manage_system.R;
import com.manage_system.net.ApiConstants;
import com.manage_system.ui.inter.Teacher;
import com.manage_system.ui.manage.activity.student.StudentGuideReportActivity;
import com.manage_system.ui.manage.activity.teacher.TeacherOutTitleMainActivity;
import com.manage_system.utils.DateUtil;
import com.manage_system.utils.DownloadUtil;
import com.manage_system.utils.OkManager;
import com.manage_system.utils.OpenFileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ManagerGroupAddActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.spinner_1)
    Button spinner_1;
    @BindView(R.id.c_date)
    Button c_date;
    @BindView(R.id.c_class)
    EditText c_class;
    @BindView(R.id.c_number)
    EditText c_number;
    @BindView(R.id.btn_choose)
    Button btn_choose;

    private static String TAG = "ManagerGroupAddActivity";
    //用于存储条目数据
    private List<String> list = new ArrayList<>();
    //用于存放已选择的条目
    private List<String> selectList = new ArrayList<>();
    public List<Map<String,Object>> listID=new ArrayList<>();
    final List<String> listId = new ArrayList<>();
    JSONObject jb = new JSONObject();
    JSONArray jArray = new JSONArray();
    String gid,leader_name = "",t_id = "";
    int type = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ms_manager_add_plan);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        if(intent.getStringExtra("gt_from").equals("from_edit")){
            type = 1;
            initEditData();
        }
        initData();
    }

    /**启动这个Activity的Intent
     * @param context
     * @return
     */
    public static Intent createIntent(Context context) {
        return new Intent(context, ManagerGroupAddActivity.class);
    }

    @OnClick({R.id.iv_back,R.id.btn_choose,R.id.c_date,R.id.spinner_1})
    public void onClick(View v) {//直接调用不会显示v被点击效果
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.btn_choose:
                showMutilAlertDialog();
                break;
            case R.id.c_date:
                showDatePicker();
                break;
            case R.id.spinner_1:
                showSingleAlertDialog();
                break;
            default:
                break;
        }
    }

    public void initEditData(){
        Intent intent = getIntent();
        JSONObject object = JSON.parseObject(intent.getStringExtra("gt_data"));
        c_date.setText(DateUtil.getDateFormatNoTime(object.getString("defDate")));
        c_class.setText(object.getString("defClass"));
        c_number.setText(object.getString("groupSize"));
        gid = object.getString("id");
        JSONArray array = new JSONArray(object.getJSONArray("teaGroup"));
        for(int i=0;i<array.size();i++){
            if(array.getJSONObject(i).getBoolean("leader")){
                btn_choose.setText(array.getJSONObject(i).getJSONObject("teaAuth").getString("name"));
            }
        }
    }

    public void initData(){
        OkManager manager = OkManager.getInstance();
        Map<String, String> map = new HashMap<String, String>();
        manager.post(ApiConstants.teacherApi + "/showUnAssignedTeaList", map,new okhttp3.Callback() {
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
                    JSONArray array = new JSONArray(obj.getJSONArray("data"));
                    for(int i=0;i<array.size();i++){
                        Log.w(TAG,array.getJSONObject(i).getString("name"));
                        list.add(array.getJSONObject(i).getString("name"));
                        Map<String, Object> map = new HashMap<>();
                        map.put("tid",array.getJSONObject(i).getString("id"));
                        map.put("name",array.getJSONObject(i).getString("name"));
                        listID.add(map);
                    }
                }
            }
        });
    }

    public void submitData(){
        OkManager manager = OkManager.getInstance();
        Gson gson = new Gson();

        jb.put("groupSize",c_number.getText().toString().trim());
        jb.put("defDate",DateUtil.date2TimeStamp(c_date.getText().toString().trim()));
        jb.put("defClass",c_class.getText().toString().trim());

        Log.w(TAG,jb.toString());

        //使用Gson将对象转换为json字符串
        String json = gson.toJson(jb);
        Log.e(TAG,json);

        RequestBody requestBody = FormBody.create(MediaType.parse("application/json; charset=utf-8"),json);
        manager.postJson(ApiConstants.teacherApi + "/addDefGroup", requestBody, new okhttp3.Callback() {
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
                            Intent intent = new Intent(ManagerGroupAddActivity.this,ManagerReplyGroupMainActivity.class);
                            intent.putExtra("reply_group","teacher_reply");
                            startActivity(intent);
                            finish();
                            Toast.makeText(ManagerGroupAddActivity.this, msg, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ManagerGroupAddActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }

    public void submitEditData(){
        OkManager manager = OkManager.getInstance();
        Gson gson = new Gson();

        jb.put("gid",gid);
        jb.put("groupSize",c_number.getText().toString().trim());
        jb.put("defDate",DateUtil.date2TimeStamp(c_date.getText().toString().trim()));
        jb.put("defClass",c_class.getText().toString().trim());

        Log.w(TAG,jb.toString());

        //使用Gson将对象转换为json字符串
        String json = gson.toJson(jb);
        Log.e(TAG,json);

        RequestBody requestBody = FormBody.create(MediaType.parse("application/json; charset=utf-8"),json);
        manager.postJson(ApiConstants.teacherApi + "/modifyDefGroup", requestBody, new okhttp3.Callback() {
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
                            Intent intent = new Intent(ManagerGroupAddActivity.this,ManagerReplyGroupMainActivity.class);
                            intent.putExtra("reply_group","teacher_reply");
                            startActivity(intent);
                            finish();
                            Toast.makeText(ManagerGroupAddActivity.this, msg, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ManagerGroupAddActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }

    public void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(ManagerGroupAddActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Log.d(TAG, "onDateSet: year: " + year + ", month: " + month + ", dayOfMonth: " + dayOfMonth);
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        c_date.setText(DateUtil.date2String(calendar.getTime()));
                        Log.e(TAG,DateUtil.date2String(calendar.getTime()));
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private AlertDialog alertDialog2;
    public void showSingleAlertDialog(){
        final String[] items = list.toArray(new String[list.size()]);
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle("选择答辩组长");
        listId.add(listID.get(0).get("tid").toString());
        leader_name = listID.get(0).get("name").toString();
        final JSONObject jObject = new JSONObject();
        jObject.put("tid",listID.get(0).get("tid").toString());
        jObject.put("leader", 1);
        jArray.add(jObject);
        jb.put("teaIdList",jArray);
        Log.e(TAG,listId.toString());
        Log.e(TAG,jb.toString());
        alertBuilder.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                listId.add(listID.get(i).get("tid").toString());
                if(listId.size()>1){
                    listId.remove(listID.get(i).get("tid").toString());
                    leader_name = listID.get(i).get("name").toString();
                    JSONArray array = new JSONArray(jb.getJSONArray("teaIdList"));
                    jArray.remove(array.getJSONObject(0));

                    t_id = listID.get(i).get("tid").toString();
                    jObject.put("tid",listID.get(i).get("tid").toString());
                    jObject.put("leader", 1);
                    jArray.add(jObject);
                    jb.put("teaIdList",jArray);
                }
                Log.w(TAG,jArray.toString());
                Log.w(TAG,jb.toString());
                Toast.makeText(ManagerGroupAddActivity.this, items[i], Toast.LENGTH_SHORT).show();
            }
        });

        alertBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialog2.dismiss();
                spinner_1.setText(leader_name);
            }
        });

        alertBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialog2.dismiss();
            }
        });

        alertDialog2 = alertBuilder.create();
        alertDialog2.show();
    }

    private AlertDialog alertDialog3;
    public void showMutilAlertDialog() {
        final String[] items = list.toArray(new String[list.size()]);
        List<Boolean> selectStatus = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            selectStatus.add(false);
        }

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle("选择答辩老师");
        /**
         *第一个参数:弹出框的消息集合，一般为字符串集合
         * 第二个参数：默认被选中的，布尔类数组
         * 第三个参数：勾选事件监听
         */
        alertBuilder.setMultiChoiceItems(items, null, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i, boolean isChecked) {
                JSONObject jObject = new JSONObject();
                if (isChecked) {
                    Log.w(TAG,"选中不包含");
                    if(selectList.contains(listID.get(i).get("tid").toString())){
                        return;
                    }else{
                        if(listID.get(i).get("tid").toString().equals(t_id)){
                            Toast.makeText(ManagerGroupAddActivity.this, "已选择其为答辩老师" , Toast.LENGTH_SHORT).show();
                        }else{
                            selectList.add(listID.get(i).get("tid").toString());
                            jObject.put("tid",listID.get(i).get("tid").toString());
                            jObject.put("leader", 0);
                            jArray.add(jObject);
                            jb.put("teaIdList",jArray);
                            Log.e(TAG,jb.toString());
                            Toast.makeText(ManagerGroupAddActivity.this, "选择" + items[i], Toast.LENGTH_SHORT).show();
                        }
                    }

                } else {
                    Log.w(TAG,"不选中包含");
                    selectList.remove(listID.get(i).get("tid").toString());
                    JSONArray array = new JSONArray(jb.getJSONArray("teaIdList"));

                    for(int j=0;j<array.size();j++){
                        if(array.getJSONObject(j+1).equals(listID.get(i).get("tid").toString())){
                            jArray.remove(array.getJSONObject(j+1));
                        }
                    }
                    Log.w(TAG,array.size()+"哈哈哈");
                    Log.e(TAG,jb.toString());
                    Toast.makeText(ManagerGroupAddActivity.this, "取消选择" + items[i], Toast.LENGTH_SHORT).show();
                }
            }
        });
        alertBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialog3.dismiss();
                if(type == 1){
                    submitEditData();
                }else{
                    submitData();
                }

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