package com.manage_system.ui.manage.activity.manager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.manage_system.R;
import com.manage_system.net.ApiConstants;
import com.manage_system.ui.manage.adapter.LeaderAdapter;
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

public class ManagerTotalReportMainActivity extends AppCompatActivity {

    @BindView(R.id.recycler)
    RecyclerView recycleView;
    @BindView(R.id.tool_bar)
    RelativeLayout tool_bar;
    @BindView(R.id.iv_back)
    ImageButton iv_back;
    @BindView(R.id.top_title)
    TextView top_title;
    @BindView(R.id.show_tab)
    LinearLayout show_tab;
    @BindView(R.id.show_tab_item1)
    TextView show_tab_item1;
    @BindView(R.id.show_tab_item2)
    TextView show_tab_item2;
    @BindView(R.id.show_tab_right)
    LinearLayout show_tab_right;
    @BindView(R.id.show_tab_item3)
    TextView show_tab_item3;
    @BindView(R.id.show_tab_item4)
    TextView show_tab_item4;

    public List<Map<String,Object>> list=new ArrayList<>();

    private static String TAG = "教师界面";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_tab);
        ButterKnife.bind(this);
        tool_bar.setVisibility(View.VISIBLE);
        top_title.setText("题目统计列表");
        initCount();
        initData();
    }

    /**启动这个Activity的Intent
     * @param context
     * @return
     */
    public static Intent createIntent(Context context) {
        return new Intent(context, ManagerTotalReportMainActivity.class);
    }

    public void initCount() {
        OkManager manager = OkManager.getInstance();
        Map<String, String> map = new HashMap<String, String>();
        manager.post(ApiConstants.teacherApi + "/showDepProCount", map,new okhttp3.Callback() {
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
                            show_tab.setVisibility(View.VISIBLE);
                            show_tab_right.setVisibility(View.VISIBLE);
                            show_tab_item1.setText("出题数："+obj.getJSONObject("data").getString("total_count"));
                            show_tab_item2.setText("选题数："+obj.getJSONObject("data").getString("choose_rest_count"));
                            show_tab_item3.setText("未选择题数："+obj.getJSONObject("data").getString("not_choose_count"));
                            show_tab_item4.setText("未选完题数："+obj.getJSONObject("data").getString("choose_not_rest_count"));
                        }else{
                            Toast.makeText(ManagerTotalReportMainActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }

    public void initData() {
        OkManager manager = OkManager.getInstance();
        Map<String, String> map = new HashMap<String, String>();
        map.put("limit","10000");
        manager.post(ApiConstants.teacherApi + "/showDepProjects", map,new okhttp3.Callback() {
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
                            JSONArray array = new JSONArray(obj.getJSONObject("data").getJSONArray("projectList"));
                            Log.d(TAG,array.getJSONObject(0).toString());
                            for (int i = 0; i < array.size(); i++) {
                                JSONObject object = array.getJSONObject(i);
                                Log.e(TAG,object.toString());
                                Map<String, Object> map = new HashMap<>();
                                map.put("identifier", object.getString("identifier"));
                                map.put("name", object.getString("name"));
                                map.put("pName",object.getString("title"));
                                map.put("rest", object.getString("rest"));
                                map.put("number",object.getString("number"));
                                map.put("m_total",object.getJSONObject("set_tea").toString());
                                map.put("total_project",object.toString());
                                JSONArray choose_stu_list = new JSONArray(object.getJSONArray("choose_stu_list"));
                                String c_stu = "";
                                if(choose_stu_list.size()>0){
                                    for(int j=0;j<choose_stu_list.size();j++){
                                        c_stu = c_stu + choose_stu_list.getJSONObject(j).getString("name")+" ";
                                    }
                                    map.put("c_stu",c_stu);
                                }else{
                                    map.put("c_stu","暂无学生选题");
                                }
                                map.put("c_teacher_name",object.getJSONObject("set_tea").getString("name"));
                                map.put("c_teacher_id",object.getJSONObject("set_tea").getString("identifier"));
                                list.add(map);
                            }
                            recycleView.setLayoutManager(new LinearLayoutManager(ManagerTotalReportMainActivity.this,LinearLayoutManager.VERTICAL,false));
                            //设置适配器
                            LeaderAdapter adapter = new LeaderAdapter(ManagerTotalReportMainActivity.this,list,"3004");
                            recycleView.setAdapter(adapter);
                            // 设置数据后就要给RecyclerView设置点击事件

                        }else{
                            Toast.makeText(ManagerTotalReportMainActivity.this, msg, Toast.LENGTH_SHORT).show();
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