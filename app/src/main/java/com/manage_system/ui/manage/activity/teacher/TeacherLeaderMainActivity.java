package com.manage_system.ui.manage.activity.teacher;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.manage_system.R;
import com.manage_system.net.ApiConstants;
import com.manage_system.ui.manage.Manage;
import com.manage_system.ui.manage.activity.student.StudentChooseDoneTitleActivity;
import com.manage_system.ui.manage.activity.student.StudentChooseDoneTitleMainActivity;
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

public class TeacherLeaderMainActivity extends AppCompatActivity {

    @BindView(R.id.recycler)
    RecyclerView recycleView;
    @BindView(R.id.tool_bar)
    RelativeLayout tool_bar;
    @BindView(R.id.iv_back)
    ImageButton iv_back;
    @BindView(R.id.top_title)
    TextView top_title;
    @BindView(R.id.add)
    Button add;

    public List<Map<String,Object>> list=new ArrayList<>();

    private static String TAG = "已出题界面";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_tab);
        ButterKnife.bind(this);
        tool_bar.setVisibility(View.VISIBLE);
        top_title.setText("答辩打分");
        add.setText("提交");
        initData();
    }

    /**启动这个Activity的Intent
     * @param context
     * @return
     */
    public static Intent createIntent(Context context) {
        return new Intent(context, TeacherLeaderMainActivity.class);
    }

    public void initData() {
        OkManager manager = OkManager.getInstance();
        Map<String, String> map = new HashMap<String, String>();
        manager.post(ApiConstants.teacherApi + "/showDefStudentsAndScores", map,new okhttp3.Callback() {
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
                            final JSONArray array = new JSONArray(obj.getJSONObject("data").getJSONArray("studentList"));

                            String score;
                            for (int i = 0; i < array.size(); i++) {
                                JSONObject object = array.getJSONObject(i);
                                JSONObject defenceScore = object.getJSONObject("defenceScore");
                                Map<String, Object> map = new HashMap<>();
                                map.put("identifier", object.getString("identifier"));
                                map.put("name", object.getString("name"));
                                map.put("pName", object.getString("pName"));
                                String name1="",name2="",name3="",name4="";


                                if(object.containsKey("defenceScore")){
                                    JSONArray array_teacher = object.getJSONObject("defenceScore").getJSONArray("defScoreList");
                                    name1 = array_teacher.getJSONObject(0).getJSONObject("tea").getString("name");
                                    name2 = array_teacher.getJSONObject(1).getJSONObject("tea").getString("name");
                                    name3 = array_teacher.getJSONObject(2).getJSONObject("tea").getString("name");
                                    name4 = array_teacher.getJSONObject(3).getJSONObject("tea").getString("name");

                                    map.put("scoreTotal1",array_teacher.getJSONObject(0).getString("scoreTotal"));
                                    map.put("scoreTotal2",array_teacher.getJSONObject(1).getString("scoreTotal"));
                                    map.put("scoreTotal3",array_teacher.getJSONObject(2).getString("scoreTotal"));
                                    map.put("scoreTotal4",array_teacher.getJSONObject(3).getString("scoreTotal"));
                                }else{
                                    map.put("scoreTotal1","暂无打分信息");
                                    map.put("scoreTotal2","暂无打分信息");
                                    map.put("scoreTotal3","暂无打分信息");
                                    map.put("scoreTotal4","暂无打分信息");
                                }

                                map.put("def_name1",name1+"  ");
                                map.put("def_name2",name2+"  ");
                                map.put("def_name3",name3+"  ");
                                map.put("def_name4",name4+"  ");

                                list.add(map);
                            }
                            recycleView.setLayoutManager(new LinearLayoutManager(TeacherLeaderMainActivity.this,LinearLayoutManager.VERTICAL,false));
//                            设置适配器
                            MyAdapter adapter = new MyAdapter(TeacherLeaderMainActivity.this,list,"2006");
                            recycleView.setAdapter(adapter);

                        }else{
                            Toast.makeText(TeacherLeaderMainActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }

    public void showDialog(){
        final Dialog dialog = new Dialog(TeacherLeaderMainActivity.this, R.style.MyDialog);
        //设置它的ContentView
        dialog.setContentView(R.layout.alert_dialog);
        ((TextView)dialog.findViewById(R.id.tvAlertDialogTitle)).setText("提交信息");
        ((TextView)dialog.findViewById(R.id.tvAlertDialogMessage)).setText("确认提交？");
        dialog.show();

        Button confirm = (Button)dialog.findViewById(R.id.btnAlertDialogPositive);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                submitData();
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

    public void submitData() {
        OkManager manager = OkManager.getInstance();
        Map<String, String> map = new HashMap<String, String>();
        manager.post(ApiConstants.teacherApi + "/commitDefence", map,new okhttp3.Callback() {
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
                            Toast.makeText(TeacherLeaderMainActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(TeacherLeaderMainActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }

    @OnClick({R.id.iv_back,R.id.add})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.add:
                showDialog();
                break;
            default:
                break;
        }
    }

}