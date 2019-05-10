package com.manage_system.ui.manage.activity.teacher;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.manage_system.R;
import com.manage_system.net.ApiConstants;
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
import okhttp3.Response;

public class TeacherReplyMainActivity extends AppCompatActivity {

    @BindView(R.id.recycler)
    RecyclerView recycleView;
    @BindView(R.id.tool_bar)
    RelativeLayout tool_bar;
    @BindView(R.id.iv_back)
    ImageButton iv_back;
    @BindView(R.id.top_title)
    TextView top_title;

    public List<Map<String,Object>> list=new ArrayList<>();

    private static String TAG = "教师信息界面";
    String leader_name = "";
    String reply_teacher_name = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_tab);
        ButterKnife.bind(this);
        tool_bar.setVisibility(View.VISIBLE);
        Intent intent = getIntent();
        if(intent.getStringExtra("reply_type").equals("teacher_info")){
            top_title.setText("教师信息");
            initData();
        }else{
            top_title.setText("学生信息");
            initStudentData();
        }

    }

    /**启动这个Activity的Intent
     * @param context
     * @return
     */
    public static Intent createIntent(Context context) {
        return new Intent(context, TeacherReplyMainActivity.class);
    }

    public void initData() {
        OkManager manager = OkManager.getInstance();
        Map<String, String> map = new HashMap<String, String>();
        manager.post(ApiConstants.teacherApi + "/showDefPlan", map,new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "onFailure: ",e);
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseBody = response.body().string();
                Log.e(TAG,responseBody);
                final JSONObject obj = JSON.parseObject(responseBody);
                final String msg = obj.getString("msg");
                Log.e(TAG,responseBody);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(obj.get("statusCode").equals(100)){
                            Map<String, Object> map = new HashMap<>();
                            map.put("id", obj.getJSONObject("data").getString("id"));
                            map.put("defClass", obj.getJSONObject("data").getString("defClass"));
                            map.put("defDate", obj.getJSONObject("data").getString("defDate"));
                            map.put("defDay", obj.getJSONObject("data").getString("defDay"));
                            map.put("defWeek", obj.getJSONObject("data").getString("defWeek"));
                            map.put("groupId", obj.getJSONObject("data").getString("groupId"));
                            map.put("groupNum", obj.getJSONObject("data").getString("groupNum"));
                            map.put("groupSize", obj.getJSONObject("data").getString("groupSize"));
                            JSONArray array = new JSONArray(obj.getJSONObject("data").getJSONArray("teaGroup"));
                            String reply_teachers = "";
                            Log.d(TAG,array.toString());
                            for (int i = 0; i < array.size(); i++) {
                                Log.e(TAG,"ttt");
                                JSONObject object = array.getJSONObject(i);
                                if(object.getBooleanValue("leader")){
                                    leader_name = object.getJSONObject("teaAuth").getString("name");
                                    Log.e(TAG,"hhh");
                                }else {
                                    reply_teacher_name = object.getJSONObject("teaAuth").getString("name");
                                    reply_teachers = reply_teachers + reply_teacher_name + " ";
                                }
                            }
                            Log.e(TAG,leader_name+"111");
                            Log.e(TAG,reply_teacher_name+"2222");
                            Log.e(TAG,reply_teachers+"2222");
                            map.put("leader_name",leader_name);
                            map.put("reply_teachers",reply_teachers);
                            list.add(map);
                            recycleView.setLayoutManager(new LinearLayoutManager(TeacherReplyMainActivity.this,LinearLayoutManager.VERTICAL,false));
//                            设置适配器
                            MyAdapter adapter = new MyAdapter(TeacherReplyMainActivity.this,list,"2003");
                            recycleView.setAdapter(adapter);
                            // 设置数据后就要给RecyclerView设置点击事件

                        }else{
                            Toast.makeText(TeacherReplyMainActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }

    public void initStudentData() {
        OkManager manager = OkManager.getInstance();
        Map<String, String> map = new HashMap<String, String>();
        manager.post(ApiConstants.teacherApi + "/showDefStudents", map,new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "onFailure: ",e);
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseBody = response.body().string();
                Log.e(TAG,responseBody);
                final JSONObject obj = JSON.parseObject(responseBody);
                final String msg = obj.getString("msg");
                Log.e(TAG,responseBody);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(obj.get("statusCode").equals(100)){
                            JSONArray array = new JSONArray(obj.getJSONObject("data").getJSONArray("studentList"));
                            for (int i = 0; i < array.size(); i++) {
                                JSONObject object = array.getJSONObject(i);
                                Map<String, Object> map = new HashMap<>();
                                map.put("identifier", object.getString("identifier"));
                                map.put("name", object.getString("name"));
                                map.put("pName", object.getString("pName"));
                                map.put("gt_identifier", object.getJSONObject("gt").getString("id"));
                                map.put("gt_name", object.getJSONObject("gt").getString("name"));
                                list.add(map);
                            }
                            recycleView.setLayoutManager(new LinearLayoutManager(TeacherReplyMainActivity.this,LinearLayoutManager.VERTICAL,false));
//                            设置适配器
                            MyAdapter adapter = new MyAdapter(TeacherReplyMainActivity.this,list,"2004");
                            recycleView.setAdapter(adapter);

                        }else{
                            Toast.makeText(TeacherReplyMainActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }

    @OnClick(R.id.iv_back)
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            default:
                break;
        }
    }

}