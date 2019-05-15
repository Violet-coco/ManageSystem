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

public class ManagerGroupStudentMainActivity extends AppCompatActivity {

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
        top_title.setText("学生分组列表");
        initData();
    }

    /**启动这个Activity的Intent
     * @param context
     * @return
     */
    public static Intent createIntent(Context context) {
        return new Intent(context, ManagerGroupStudentMainActivity.class);
    }

    public void initData() {
        OkManager manager = OkManager.getInstance();
        Map<String, String> map = new HashMap<String, String>();
        map.put("limit","10000");
        manager.post(ApiConstants.teacherApi + "/showDepStuDefGroup", map,new okhttp3.Callback() {
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
                            final JSONArray array = new JSONArray(obj.getJSONObject("data").getJSONArray("studentLists"));
                            for (int i = 0; i < array.size(); i++) {
                                JSONObject object = array.getJSONObject(i);
                                Map<String, Object> map = new HashMap<>();
                                map.put("identifier", object.getString("identifier"));
                                map.put("name", object.getString("name"));
                                map.put("gt_name", object.getJSONObject("gt").getString("name"));
                                if(object.containsKey("mt")){
                                    map.put("mt_name",object.getJSONObject("mt").getString("name"));
                                }else{
                                    map.put("mt_name","暂无数据");
                                }
                                if(object.containsKey("defInfo")){
                                    map.put("groupId",object.getJSONObject("defInfo").getString("groupId"));
                                    map.put("defDate",object.getJSONObject("defInfo").getString("defDate"));
                                    map.put("defWeek",object.getJSONObject("defInfo").getString("defWeek"));
                                    map.put("defDay",object.getJSONObject("defInfo").getString("defDay"));
                                }else{
                                    map.put("groupId","暂无数据");
                                    map.put("defDate","暂无数据");
                                    map.put("defWeek","暂无数据");
                                    map.put("defDay","暂无数据");
                                }

                                list.add(map);
                            }
                            recycleView.setLayoutManager(new LinearLayoutManager(ManagerGroupStudentMainActivity.this,LinearLayoutManager.VERTICAL,false));
//                            设置适配器
                            ShowAdapter adapter = new ShowAdapter(ManagerGroupStudentMainActivity.this,list,"3107");
                            recycleView.setAdapter(adapter);

                        }else{
                            Toast.makeText(ManagerGroupStudentMainActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }

    public void showDialog(){
        final Dialog dialog = new Dialog(ManagerGroupStudentMainActivity.this, R.style.MyDialog);
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
        manager.post(ApiConstants.teacherApi + "/commitStuTotalScore", map,new okhttp3.Callback() {
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
                            Toast.makeText(ManagerGroupStudentMainActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(ManagerGroupStudentMainActivity.this, msg, Toast.LENGTH_SHORT).show();
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