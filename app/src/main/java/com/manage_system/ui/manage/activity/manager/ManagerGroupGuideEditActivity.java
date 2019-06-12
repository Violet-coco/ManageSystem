package com.manage_system.ui.manage.activity.manager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.manage_system.R;
import com.manage_system.net.ApiConstants;
import com.manage_system.ui.inter.Teacher;
import com.manage_system.ui.manage.adapter.MyAdapter;
import com.manage_system.utils.OkManager;

import java.io.IOException;
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

public class ManagerGroupGuideEditActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.groupId)
    EditText groupId;
    @BindView(R.id.number)
    EditText number;
    @BindView(R.id.leader)
    EditText leader;
    @BindView(R.id.teachers)
    EditText teachers;
    @BindView(R.id.guide_teacher)
    EditText guide_teacher;
    @BindView(R.id.choose_gt)
    Button choose_gt;
    private static String TAG = "ManagerPdListActivity";
    //用于存储条目数据
    private List<String> list = new ArrayList<>();
    //用于存放已选择的条目
    private List<String> selectList = new ArrayList<>();
    public List<Map<String,Object>> listID=new ArrayList<>();
    final List<String> listId = new ArrayList<>();
    private String gid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ms_manager_guide_edit);
        ButterKnife.bind(this);
        initData();
    }

    /**
     * 启动这个Activity的Intent
     *
     * @param context
     * @return
     */
    public static Intent createIntent(Context context) {
        return new Intent(context, ManagerGroupGuideEditActivity.class);
    }

    @OnClick({R.id.iv_back, R.id.choose_gt})
    public void onClick(View v) {//直接调用不会显示v被点击效果
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.choose_gt:
                showMutilAlertDialog();
                break;
            default:
                break;
        }
    }

    public void initData() {
        Intent intent = getIntent();
        JSONObject object = JSON.parseObject(intent.getStringExtra("gt_data"));
        groupId.setText("第" + object.getString("groupId") + "组");
        number.setText(object.getString("groupSize") + "/" + object.getString("groupNum"));
        String teachers_all = "";
        JSONArray array = new JSONArray(object.getJSONArray("teaGroup"));
        for (int i = 0; i < array.size(); i++) {
            if (array.getJSONObject(i).getBoolean("leader")) {
                leader.setText(array.getJSONObject(i).getJSONObject("teaAuth").getString("name"));
            } else {
                teachers_all = teachers_all + array.getJSONObject(i).getJSONObject("teaAuth").getString("name") + " ";
            }
        }

        String gt_all = "";
        JSONArray array_gt = new JSONArray(object.getJSONArray("gtGroup"));
        for (int i = 0; i < array_gt.size(); i++) {
            gt_all = gt_all + array_gt.getJSONObject(i).getJSONObject("teacher").getString("name") + " ";
            list.add(array_gt.getJSONObject(i).getJSONObject("teacher").getString("name"));
            selectList.add(array_gt.getJSONObject(i).getJSONObject("teacher").getString("name"));
            Map<String, Object> map = new HashMap<>();
            map.put("id",array_gt.getJSONObject(i).getJSONObject("teacher").getString("id"));
            map.put("name",array_gt.getJSONObject(i).getJSONObject("teacher").getString("name"));
            listID.add(map);
        }
        guide_teacher.setText(gt_all);
        teachers.setText(teachers_all);
        OkManager manager = OkManager.getInstance();
        Map<String, String> map = new HashMap<String, String>();
        map.put("gid", object.getString("id"));
        gid = object.getString("id");
        OkManager.post(ApiConstants.teacherApi + "/showUnAssignedGtList", map, new okhttp3.Callback() {
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
                                map.put("id",array.getJSONObject(i).getString("id"));
                                map.put("name",array.getJSONObject(i).getString("name"));
                                listID.add(map);
                            }
                        } else {
                            Toast.makeText(ManagerGroupGuideEditActivity.this, msg, Toast.LENGTH_SHORT).show();
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
//        selectList
        for (int j = 0; j < selectList.size(); j++) {
            selectStatus.add(true);
        }
        for (int i = 0; i < list.size() - selectList.size(); i++) {
            selectStatus.add(false);
        }

        final boolean[] checkedItems = new boolean[selectStatus.size()];
        for (int i=0;i<selectStatus.size();i++){
            checkedItems[i]=selectStatus.get(i);
            if(selectStatus.get(i).equals(true)){
                listId.add(listID.get(i).get("id").toString());
            }
        }

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle("选择指导老师");
        /**
         *第一个参数:弹出框的消息集合，一般为字符串集合
         * 第二个参数：默认被选中的，布尔类数组
         * 第三个参数：勾选事件监听
         */
        alertBuilder.setMultiChoiceItems(items, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i, boolean isChecked) {
                if (isChecked) {
                    if(listId.contains(listID.get(i).get("id").toString())){
                        listId.remove(listID.get(i).get("id").toString());
                    }else{
                        listId.add(listID.get(i).get("id").toString());
                    }
                    Toast.makeText(ManagerGroupGuideEditActivity.this, "选择" + items[i], Toast.LENGTH_SHORT).show();
                } else {
                    if(listId.contains(listID.get(i).get("id").toString())){
                        listId.remove(listID.get(i).get("id").toString());
                    }else{
                        listId.add(listID.get(i).get("id").toString());
                    }
                    Toast.makeText(ManagerGroupGuideEditActivity.this, "取消选择" + items[i], Toast.LENGTH_SHORT).show();
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

    public void submitData() {
        OkManager manager = OkManager.getInstance();

        Teacher teacher = new Teacher();
        teacher.setGid(gid);
        teacher.setGtList(listId);

        Log.w(TAG,gid);
        Log.w(TAG,listId.toString());
        Gson gson = new Gson();
        //使用Gson将对象转换为json字符串
        String json = gson.toJson(teacher);
        Log.w(TAG,json);

        RequestBody requestBody = FormBody.create(MediaType.parse("application/json; charset=utf-8"),json);
        OkManager.postJson(ApiConstants.teacherApi + "/modifyDefTeaGroup", requestBody, new okhttp3.Callback() {
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
                            Intent intent = new Intent(ManagerGroupGuideEditActivity.this,ManagerReplyGroupMainActivity.class);
                            intent.putExtra("reply_group","teacher_guide");
                            startActivity(intent);
                            finish();
                            Toast.makeText(ManagerGroupGuideEditActivity.this, msg, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ManagerGroupGuideEditActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }
}