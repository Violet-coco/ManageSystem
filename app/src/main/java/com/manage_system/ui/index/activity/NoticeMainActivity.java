package com.manage_system.ui.index.activity;

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
import com.manage_system.ui.index.adapter.NoticeAdapter;
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

public class NoticeMainActivity extends AppCompatActivity {

    @BindView(R.id.recycler)
    RecyclerView recycleView;
    @BindView(R.id.tool_bar)
    RelativeLayout tool_bar;
    @BindView(R.id.iv_back)
    ImageButton iv_back;
    @BindView(R.id.top_title)
    TextView top_title;

    public List<Map<String,Object>> list=new ArrayList<>();
    public List<Map<String,Object>> teacher_info=new ArrayList<>();

    private static String TAG = "NoticeMainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_tab);
        ButterKnife.bind(this);
        tool_bar.setVisibility(View.VISIBLE);
        Intent intent = getIntent();
        if(intent.getStringExtra("read_type").equals("done")){
            initData();
            top_title.setText("已读通知列表");
        }else if(intent.getStringExtra("read_type").equals("ann_done")){
            initAnnData();
            top_title.setText("已读公告列表");
        } else if(intent.getStringExtra("read_type").equals("ann")){
            initNoAnnData();
            top_title.setText("未读公告列表");
        }else{
            initNoDoneData();
            top_title.setText("未读通知列表");
        }

    }

    /**启动这个Activity的Intent
     * @param context
     * @return
     */
    public static Intent createIntent(Context context) {
        return new Intent(context, NoticeMainActivity.class);
    }

    public void initData() {
        OkManager manager = OkManager.getInstance();
        Map<String, String> map = new HashMap<String, String>();
        map.put("hasRead","1");
        map.put("limit","100000");
        manager.post(ApiConstants.commonApi + "/showAllNotifications", map,new okhttp3.Callback() {
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
                            final JSONArray array = new JSONArray(obj.getJSONObject("data").getJSONArray("notificationsList"));
                            for (int i = 0; i < array.size(); i++) {
                                JSONObject object = array.getJSONObject(i);
                                Map<String, Object> map = new HashMap<>();
                                map.put("title", object.getString("title"));
                                map.put("name", object.getJSONObject("recvRole").getString("name"));
                                map.put("id", object.getJSONObject("recvRole").getString("id"));
                                map.put("sendDate", DateUtil.getDateFormat(object.getString("sendDate")));
                                map.put("content", object.getString("content"));
                                list.add(map);
                            }
                            recycleView.setLayoutManager(new LinearLayoutManager(NoticeMainActivity.this,LinearLayoutManager.VERTICAL,false));
                            //设置适配器
                            NoticeAdapter adapter = new NoticeAdapter(NoticeMainActivity.this,list,"0003");
                            recycleView.setAdapter(adapter);
                            // 设置数据后就要给RecyclerView设置点击事件
                            Toast.makeText(NoticeMainActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(NoticeMainActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }

    public void initNoDoneData() {
        OkManager manager = OkManager.getInstance();
        Map<String, String> map = new HashMap<String, String>();
        map.put("hasRead","0");
        map.put("limit","100000");
        manager.post(ApiConstants.commonApi + "/showAllNotifications", map,new okhttp3.Callback() {
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
                            final JSONArray array = new JSONArray(obj.getJSONObject("data").getJSONArray("notificationsList"));
                            Log.d(TAG,array.getJSONObject(0).toString());
                            for (int i = 0; i < array.size(); i++) {
                                JSONObject object = array.getJSONObject(i);
                                Map<String, Object> map = new HashMap<>();
                                map.put("title", object.getString("title"));
                                if(object.containsKey("recvRole")){
                                    map.put("name", object.getJSONObject("recvRole").getString("name"));
                                    map.put("id", object.getJSONObject("recvRole").getString("id"));
                                }else{
                                    map.put("name", "");
                                    map.put("id", "");
                                }

                                if(object.containsKey("content")){
                                    map.put("content", object.getString("content"));
                                }else{
                                    map.put("content", "");
                                }
                                map.put("nid",object.getString("id"));

                                map.put("sendDate", DateUtil.getDateFormat(object.getString("sendDate")));
                                list.add(map);
                            }
                            recycleView.setLayoutManager(new LinearLayoutManager(NoticeMainActivity.this,LinearLayoutManager.VERTICAL,false));
                            //设置适配器
                            NoticeAdapter adapter = new NoticeAdapter(NoticeMainActivity.this,list,"0004");
                            recycleView.setAdapter(adapter);
                            // 设置数据后就要给RecyclerView设置点击事件
                            Toast.makeText(NoticeMainActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(NoticeMainActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }

    public void initAnnData() {
        OkManager manager = OkManager.getInstance();
        Map<String, String> map = new HashMap<String, String>();
        map.put("hasRead","1");
        map.put("limit","100000");
        manager.post(ApiConstants.commonApi + "/showAllAnnounces", map,new okhttp3.Callback() {
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
                            final JSONArray array = new JSONArray(obj.getJSONObject("data").getJSONArray("announcesList"));
                            for (int i = 0; i < array.size(); i++) {
                                JSONObject object = array.getJSONObject(i);
                                Map<String, Object> map = new HashMap<>();
                                map.put("title", object.getJSONObject("notice").getString("title"));
                                map.put("sendName", object.getString("sendName"));
                                map.put("sendDate", DateUtil.getDateFormat(object.getString("sendDate")));
                                map.put("content", object.getJSONObject("notice").getString("content"));
                                list.add(map);
                            }
                            recycleView.setLayoutManager(new LinearLayoutManager(NoticeMainActivity.this,LinearLayoutManager.VERTICAL,false));
                            //设置适配器
                            NoticeAdapter adapter = new NoticeAdapter(NoticeMainActivity.this,list,"0005");
                            recycleView.setAdapter(adapter);
                            // 设置数据后就要给RecyclerView设置点击事件
                            Toast.makeText(NoticeMainActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(NoticeMainActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }

    public void initNoAnnData() {
        OkManager manager = OkManager.getInstance();
        Map<String, String> map = new HashMap<String, String>();
        map.put("hasRead","0");
        map.put("limit","100000");
        manager.post(ApiConstants.commonApi + "/showAllAnnounces", map,new okhttp3.Callback() {
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
                            final JSONArray array = new JSONArray(obj.getJSONObject("data").getJSONArray("announcesList"));
                            for (int i = 0; i < array.size(); i++) {
                                JSONObject object = array.getJSONObject(i);
                                Map<String, Object> map = new HashMap<>();
                                map.put("title", object.getJSONObject("notice").getString("title"));
                                map.put("sendName", object.getString("sendName"));
                                map.put("sendDate", DateUtil.getDateFormat(object.getString("sendDate")));
                                map.put("content", object.getJSONObject("notice").getString("content"));
                                map.put("aid",object.getString("id"));
                                list.add(map);
                            }
                            recycleView.setLayoutManager(new LinearLayoutManager(NoticeMainActivity.this,LinearLayoutManager.VERTICAL,false));
                            //设置适配器
                            NoticeAdapter adapter = new NoticeAdapter(NoticeMainActivity.this,list,"0006");
                            recycleView.setAdapter(adapter);
                            // 设置数据后就要给RecyclerView设置点击事件
                            Toast.makeText(NoticeMainActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(NoticeMainActivity.this, msg, Toast.LENGTH_SHORT).show();
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