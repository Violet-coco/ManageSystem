package com.manage_system.ui.manage.activity.manager;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.manage_system.ui.manage.adapter.ShowAdapter;
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

public class ManagerScoreListMainActivity extends AppCompatActivity {

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
        top_title.setText("计算总成绩");
        add.setText("提交");
        initData();
    }

    /**启动这个Activity的Intent
     * @param context
     * @return
     */
    public static Intent createIntent(Context context) {
        return new Intent(context, ManagerScoreListMainActivity.class);
    }

    public void initData() {
        OkManager manager = OkManager.getInstance();
        Map<String, String> map = new HashMap<String, String>();
        map.put("limit","10000");
        OkManager.post(ApiConstants.teacherApi + "/showDepStuScoreList", map,new okhttp3.Callback() {
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
                            for (int i = 0; i < array.size(); i++) {
                                JSONObject object = array.getJSONObject(i);
                                Map<String, Object> map = new HashMap<>();
                                map.put("identifier", object.getString("identifier"));
                                map.put("name", object.getString("name"));
                                map.put("pName", object.getString("pName"));

                                if(object.containsKey("proScore")){
                                    if(object.containsKey("guidanceTeaGroup")&&object.getJSONObject("guidanceTeaGroup").getBoolean("hasReviewed")){
                                        map.put("scoreGt",object.getJSONObject("proScore").getString("scoreGt"));
                                    }else{
                                        map.put("scoreGt","暂无分数");
                                    }

                                    if(object.containsKey("defenceScore")&&object.getJSONObject("defenceScore").getBoolean("hasReviewed")){
                                        map.put("scoreDef",object.getJSONObject("proScore").getString("scoreDef"));
                                    }else{
                                        map.put("scoreDef","暂无分数");
                                    }

                                    if(object.containsKey("reviewTeaGroup")&&object.getJSONObject("reviewTeaGroup").getBoolean("hasReviewed")){
                                        map.put("scoreMt",object.getJSONObject("proScore").getString("scoreMt"));
                                    }else{
                                        map.put("scoreMt","暂无分数");
                                    }

                                    map.put("scoreTotal",object.getJSONObject("proScore").getString("scoreTotal"));
                                    map.put("grade",object.getJSONObject("proScore").getString("grade"));
                                }else{
                                    if(object.containsKey("guidanceTeaGroup")&&object.getJSONObject("guidanceTeaGroup").getBoolean("hasReviewed")){
                                        map.put("scoreGt",object.getJSONObject("guidanceTeaGroup").getString("scoreTotal"));
                                    }else{
                                        map.put("scoreGt","暂无分数");
                                    }

                                    if(object.containsKey("defenceScore")&&object.getJSONObject("defenceScore").getBoolean("hasReviewed")){
                                        map.put("scoreDef",object.getJSONObject("defenceScore").getString("scoreTotal"));
                                    }else{
                                        map.put("scoreDef","暂无分数");
                                    }

                                    if(object.containsKey("reviewTeaGroup")&&object.getJSONObject("reviewTeaGroup").getBoolean("hasReviewed")){
                                        map.put("scoreMt",object.getJSONObject("reviewTeaGroup").getString("scoreTotal"));
                                    }else{
                                        map.put("scoreMt","暂无分数");
                                    }

                                    map.put("scoreTotal","暂无分数");
                                    map.put("grade","暂无等级");
                                }
                                map.put("stu_info",object.toString());

                                list.add(map);
                            }
                            recycleView.setLayoutManager(new LinearLayoutManager(ManagerScoreListMainActivity.this,LinearLayoutManager.VERTICAL,false));
//                            设置适配器
                            ShowAdapter adapter = new ShowAdapter(ManagerScoreListMainActivity.this,list,"3106");
//                            adapter.setOnItemClickListener(new MyAdapter.OnItemClickListener() {
//                                @Override
//                                public void onItemClick(int position) {
//                                    // 这里本来是跳转页面 ，我们就在这里直接让其弹toast来演示
//                                    Log.w(TAG,"位置是："+position);
//                                    Intent intent = new Intent(TeacherLeaderMainActivity.this,TeacherScoreActivity.class);
//                                    intent.putExtra("position",position + "");
//                                    startActivity(intent);
//
//                                }
//                            });
                            recycleView.setAdapter(adapter);

                        }else{
                            Toast.makeText(ManagerScoreListMainActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }

    public void showDialog(){
        final Dialog dialog = new Dialog(ManagerScoreListMainActivity.this, R.style.MyDialog);
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
        OkManager.post(ApiConstants.teacherApi + "/commitStuTotalScore", map,new okhttp3.Callback() {
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
                            Toast.makeText(ManagerScoreListMainActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(ManagerScoreListMainActivity.this, msg, Toast.LENGTH_SHORT).show();
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