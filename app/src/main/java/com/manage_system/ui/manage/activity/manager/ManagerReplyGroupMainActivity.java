package com.manage_system.ui.manage.activity.manager;

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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.manage_system.R;
import com.manage_system.net.ApiConstants;
import com.manage_system.ui.manage.adapter.ShowAdapter;
import com.manage_system.utils.DateUtil;
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

public class ManagerReplyGroupMainActivity extends AppCompatActivity {

    @BindView(R.id.recycler)
    RecyclerView recycleView;
    @BindView(R.id.tool_bar)
    RelativeLayout tool_bar;
    @BindView(R.id.iv_back)
    ImageButton iv_back;
    @BindView(R.id.show_tab)
    LinearLayout show_tab;
    @BindView(R.id.show_tab_item1)
    TextView show_tab_item1;
    @BindView(R.id.show_tab_item2)
    TextView show_tab_item2;
    @BindView(R.id.show_tab_right)
    LinearLayout show_tab_right;
    @BindView(R.id.top_title)
    TextView top_title;
    @BindView(R.id.add)
    Button add;

    public List<Map<String,Object>> list=new ArrayList<>();

    private static String TAG = "ManagerReplyGroupMainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_tab);
        ButterKnife.bind(this);
        tool_bar.setVisibility(View.VISIBLE);
        initDefDate();
        Intent intent = getIntent();
        if(intent.getStringExtra("reply_group").equals("teacher_reply")){
            top_title.setText("答辩教师");
            add.setText("新增+");
            initData();
        }else{
            top_title.setText("指导教师");
            initGuideData();
        }

    }

    /**启动这个Activity的Intent
     * @param context
     * @return
     */
    public static Intent createIntent(Context context) {
        return new Intent(context, ManagerReplyGroupMainActivity.class);
    }

    public void initDefDate() {
        OkManager manager = OkManager.getInstance();
        Map<String, String> map = new HashMap<String, String>();
        manager.post(ApiConstants.teacherApi + "/showDepDefDate", map,new okhttp3.Callback() {
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
                            show_tab_right.setVisibility(View.GONE);
                            show_tab.setVisibility(View.VISIBLE);
                            show_tab_item1.setTextSize(16);
                            show_tab_item2.setTextSize(16);
                            show_tab_item1.setText("答辩开始日期："+"第"+obj.getJSONObject("data").getJSONObject("beginDate").getString("week")+"周 星期"+obj.getJSONObject("data").getJSONObject("beginDate").getString("day"));
                            show_tab_item2.setText("答辩结束日期：第"+obj.getJSONObject("data").getJSONObject("endDate").getString("week")+"周 星期"+obj.getJSONObject("data").getJSONObject("endDate").getString("day"));
                        }else{
                            Toast.makeText(ManagerReplyGroupMainActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }

    public void initData() {
        OkManager manager = OkManager.getInstance();
        Map<String, String> map = new HashMap<String, String>();
        manager.post(ApiConstants.teacherApi + "/showDepTeaDefGroup", map,new okhttp3.Callback() {
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
                            JSONArray array = new JSONArray(obj.getJSONArray("data"));
                            for (int i = 0; i < array.size(); i++) {
                                JSONObject object = array.getJSONObject(i);
                                Log.e(TAG,object.toString());
                                Map<String, Object> map = new HashMap<>();
                                map.put("date", DateUtil.getDateFormat(object.getString("defDate")));
                                map.put("week","第"+object.getString("defWeek")+"周 星期"+object.getString("defDay"));
                                map.put("class", object.getString("defClass"));
                                map.put("gid",object.getString("id"));
                                map.put("gt_data",object.toString());
                                String groups = "",leader="";
                                JSONArray teaGroup = new JSONArray(object.getJSONArray("teaGroup"));
                                for(int j=0;j<teaGroup.size();j++){
                                    if(teaGroup.getJSONObject(j).getBoolean("leader")){
                                        leader = teaGroup.getJSONObject(j).getJSONObject("teaAuth").getString("name");
                                    }else{
                                        groups = groups + teaGroup.getJSONObject(j).getJSONObject("teaAuth").getString("name") + " ";
                                    }
                                }
                                map.put("leader",leader);
                                map.put("groups",groups);
                                map.put("groupSize",object.getString("groupSize"));
                                map.put("groupNum",object.getString("groupNum"));
                                map.put("groupId",object.getString("groupId"));
                                list.add(map);
                            }
                            recycleView.setLayoutManager(new LinearLayoutManager(ManagerReplyGroupMainActivity.this,LinearLayoutManager.VERTICAL,false));
                            //设置适配器
                            ShowAdapter adapter = new ShowAdapter(ManagerReplyGroupMainActivity.this,list,"3100");
                            recycleView.setAdapter(adapter);
                            // 设置数据后就要给RecyclerView设置点击事件

                        }else{
                            Toast.makeText(ManagerReplyGroupMainActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }

    public void initGuideData() {
        OkManager manager = OkManager.getInstance();
        Map<String, String> map = new HashMap<String, String>();
        manager.post(ApiConstants.teacherApi + "/showDefInfoList", map,new okhttp3.Callback() {
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
                            JSONArray array = new JSONArray(obj.getJSONArray("data"));
                            for (int i = 0; i < array.size(); i++) {
                                JSONObject object = array.getJSONObject(i);
                                Log.e(TAG,object.toString());
                                Map<String, Object> map = new HashMap<>();
                                map.put("groupId",object.getString("groupId"));
                                map.put("groupSize",object.getString("groupSize"));
                                map.put("groupNum",object.getString("groupNum"));
                                map.put("gid",object.getString("id"));
                                map.put("gt_data",object.toString());
                                JSONArray gt = new JSONArray(object.getJSONArray("gtGroup"));
                                String guide_teacher="";
                                for(int j=0;j<gt.size();j++){
                                    guide_teacher = guide_teacher + gt.getJSONObject(j).getJSONObject("teacher").getString("name")+"（"+gt.getJSONObject(j).getJSONObject("teacher").getString("stuListCount")+"）";
                                }
                                map.put("guide_teacher", guide_teacher);
                                list.add(map);
                            }
                            recycleView.setLayoutManager(new LinearLayoutManager(ManagerReplyGroupMainActivity.this,LinearLayoutManager.VERTICAL,false));
                            //设置适配器
                            ShowAdapter adapter = new ShowAdapter(ManagerReplyGroupMainActivity.this,list,"3101");
                            recycleView.setAdapter(adapter);
                            finish();
                            // 设置数据后就要给RecyclerView设置点击事件

                        }else{
                            Toast.makeText(ManagerReplyGroupMainActivity.this, msg, Toast.LENGTH_SHORT).show();
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
                Intent intent = new Intent(ManagerReplyGroupMainActivity.this,ManagerGroupAddActivity.class);
                intent.putExtra("gt_from","");
                startActivity(intent);
                finish();
                break;
            default:
                break;
        }
    }

}