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

public class ManagerCtCheckMainActivity extends AppCompatActivity {

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

    public List<Map<String,Object>> list=new ArrayList<>();

    private static String TAG = "ManagerXtCheckMainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_tab);
        ButterKnife.bind(this);
        tool_bar.setVisibility(View.VISIBLE);
        top_title.setText("出题审核");
        initData();
    }

    /**启动这个Activity的Intent
     * @param context
     * @return
     */
    public static Intent createIntent(Context context) {
        return new Intent(context, ManagerCtCheckMainActivity.class);
    }

    public void initData() {
        OkManager manager = OkManager.getInstance();
        Map<String, String> map = new HashMap<String, String>();
        map.put("limit","10000");
        OkManager.post(ApiConstants.teacherApi + "/showTeaProSetList", map,new okhttp3.Callback() {
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
                            JSONArray array = new JSONArray(obj.getJSONObject("data").getJSONArray("proList"));
                            Log.d(TAG,array.getJSONObject(0).toString());
                            for (int i = 0; i < array.size(); i++) {
                                JSONObject object = array.getJSONObject(i);
                                Log.e(TAG,object.toString());
                                Map<String, Object> map = new HashMap<>();
                                map.put("name", object.getJSONObject("teacher").getString("name"));
                                map.put("tid",object.getJSONObject("teacher").getString("identifier"));
                                map.put("pid",object.getJSONObject("project").getString("id"));
                                map.put("pName",object.getJSONObject("project").getString("title"));
                                map.put("setDate",DateUtil.getDateFormat(object.getString("setDate")));
                                map.put("teacher_info",object.getJSONObject("teacher").toString());
                                map.put("project_info",object.toString());
                                String status;
                                if(object.getString("cStatus").equals("0")){
                                    status = "审核不通过";
                                }else if(object.getString("cStatus").equals("1")){
                                    status = "审核通过";
                                }else{
                                    status = "审核中";
                                }
                                map.put("status",status);
                                list.add(map);
                            }
                            recycleView.setLayoutManager(new LinearLayoutManager(ManagerCtCheckMainActivity.this,LinearLayoutManager.VERTICAL,false));
                            //设置适配器
                            LeaderAdapter adapter = new LeaderAdapter(ManagerCtCheckMainActivity.this,list,"3008");
                            recycleView.setAdapter(adapter);
                            // 设置数据后就要给RecyclerView设置点击事件

                        }else{
                            Toast.makeText(ManagerCtCheckMainActivity.this, msg, Toast.LENGTH_SHORT).show();
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