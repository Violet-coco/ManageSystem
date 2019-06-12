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
import com.manage_system.ui.manage.adapter.GradeDetailAdapter;
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

public class ManagerScoreTotalMainActivity extends AppCompatActivity {

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

    public List<Map<String,Object>> list=new ArrayList<>();

    private static String TAG = "ManagerScoreTotalMainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_tab);
        ButterKnife.bind(this);
        tool_bar.setVisibility(View.VISIBLE);
        show_tab_right.setVisibility(View.GONE);
        show_tab.setVisibility(View.VISIBLE);
        show_tab_item1.setTextSize(14);
        show_tab_item2.setTextSize(14);
        top_title.setText("成绩汇总列表");
        show_tab_item1.setText("成绩栏依次表示的是：");
        show_tab_item2.setText("指导成绩 评阅成绩 答辩成绩 评定成绩 评定等级");
        initData();
    }

    /**启动这个Activity的Intent
     * @param context
     * @return
     */
    public static Intent createIntent(Context context) {
        return new Intent(context, ManagerScoreTotalMainActivity.class);
    }

    public void initData() {
        OkManager manager = OkManager.getInstance();
        Map<String, String> map = new HashMap<String, String>();
        map.put("limit","10000");
        OkManager.post(ApiConstants.teacherApi + "/showDepStuScoreSummaryList", map,new okhttp3.Callback() {
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
                            JSONArray array = new JSONArray(obj.getJSONObject("data").getJSONArray("studentLists"));
                            for (int i = 0; i < array.size(); i++) {
                                JSONObject object = array.getJSONObject(i);
                                Log.e(TAG,object.toString());
                                Map<String, Object> map = new HashMap<>();
                                map.put("name",object.getString("name"));
                                map.put("identifier",object.getString("identifier"));
                                map.put("pName",object.getString("pName"));
                                map.put("stu_info",object.toString());
                                if(object.containsKey("proScore")){
                                    map.put("scoreGt", object.getJSONObject("proScore").getString("scoreGt"));
                                    map.put("scoreMt", object.getJSONObject("proScore").getString("scoreMt"));
                                    map.put("scoreDef", object.getJSONObject("proScore").getString("scoreDef"));
                                    map.put("scoreTotal", object.getJSONObject("proScore").getString("scoreTotal"));
                                    map.put("grade", object.getJSONObject("proScore").getString("grade"));
                                }else{
                                    map.put("scoreGt", "0");
                                    map.put("scoreMt", "0");
                                    map.put("scoreDef", "0");
                                    map.put("scoreTotal", "0");
                                    map.put("grade", "0");
                                }

                                list.add(map);
                            }
                            recycleView.setLayoutManager(new LinearLayoutManager(ManagerScoreTotalMainActivity.this,LinearLayoutManager.VERTICAL,false));
                            //设置适配器
                            GradeDetailAdapter adapter = new GradeDetailAdapter(ManagerScoreTotalMainActivity.this,list,"3108");
                            recycleView.setAdapter(adapter);
                            // 设置数据后就要给RecyclerView设置点击事件

                        }else{
                            Toast.makeText(ManagerScoreTotalMainActivity.this, msg, Toast.LENGTH_SHORT).show();
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