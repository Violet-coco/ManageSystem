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

public class TeacherReplyPlanMainActivity extends AppCompatActivity {

    @BindView(R.id.recycler)
    RecyclerView recycleView;
    @BindView(R.id.tool_bar)
    RelativeLayout tool_bar;
    @BindView(R.id.iv_back)
    ImageButton iv_back;
    @BindView(R.id.top_title)
    TextView top_title;

    public List<Map<String,Object>> list=new ArrayList<>();

    private static String TAG = "已出题界面";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_tab);
        ButterKnife.bind(this);
        tool_bar.setVisibility(View.VISIBLE);
        top_title.setText("答辩安排");
        initData();
    }

    /**启动这个Activity的Intent
     * @param context
     * @return
     */
    public static Intent createIntent(Context context) {
        return new Intent(context, TeacherReplyPlanMainActivity.class);
    }

    public void initData() {
        OkManager manager = OkManager.getInstance();
        Map<String, String> map = new HashMap<String, String>();
        manager.post(ApiConstants.teacherApi + "/showTeaStudentDefInfos", map,new okhttp3.Callback() {
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
                                map.put("name", object.getString("name"));
                                map.put("identifier", object.getString("identifier"));
                                if(object.containsKey("defInfo")){
                                    map.put("date", DateUtil.getDateFormat(object.getJSONObject("defInfo").getString("defDate")));
                                    map.put("defWeek", object.getJSONObject("defInfo").getString("defWeek"));
                                    map.put("defDay", object.getJSONObject("defInfo").getString("defDay"));

                                    map.put("defClass", object.getJSONObject("defInfo").getString("defClass"));
                                    JSONArray array_group = new JSONArray(object.getJSONObject("defInfo").getJSONArray("teaGroup"));
                                    String reply_teacher_name,reply_teachers = "";
                                    for (int j=0;j<array_group.size();j++){
                                        JSONObject object_group = array_group.getJSONObject(j);
                                        if(object_group.getBoolean("leader")){
                                            map.put("leader",object_group.getJSONObject("teaAuth").getString("name"));
                                        }else{
                                            reply_teacher_name = object_group.getJSONObject("teaAuth").getString("name");
                                            reply_teachers = reply_teachers + reply_teacher_name + " ";
                                        }
                                    }
                                    map.put("reply_teacher", reply_teachers);
                                    map.put("group",object.getJSONObject("defInfo").getString("groupId"));
                                }else{
                                    map.put("date", "暂无安排");
                                    map.put("defWeek", "0");
                                    map.put("defDay", "0");

                                    map.put("defClass", "暂无安排");
                                    map.put("leader","暂无安排");
                                    map.put("reply_teacher", "暂无安排");
                                    map.put("group","暂无安排");
                                }

                                if(object.containsKey("mt")){
                                    map.put("comment_teacher",object.getJSONObject("mt").getString("name"));
                                }else{
                                    map.put("comment_teacher","暂无");
                                }

                                list.add(map);
                            }
                            recycleView.setLayoutManager(new LinearLayoutManager(TeacherReplyPlanMainActivity.this,LinearLayoutManager.VERTICAL,false));
                            //设置适配器
                            ShowAdapter adapter = new ShowAdapter(TeacherReplyPlanMainActivity.this,list,"2007");
                            recycleView.setAdapter(adapter);
                            // 设置数据后就要给RecyclerView设置点击事件

                        }else{
                            Toast.makeText(TeacherReplyPlanMainActivity.this, msg, Toast.LENGTH_SHORT).show();
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