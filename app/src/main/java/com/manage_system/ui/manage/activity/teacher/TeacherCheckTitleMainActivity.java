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

public class TeacherCheckTitleMainActivity extends AppCompatActivity {

    @BindView(R.id.recycler)
    RecyclerView recycleView;
    @BindView(R.id.tool_bar)
    RelativeLayout tool_bar;
    @BindView(R.id.iv_back)
    ImageButton iv_back;

    public List<Map<String,Object>> list=new ArrayList<>();

    private static String TAG = "已出题界面";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_tab);
        ButterKnife.bind(this);
        tool_bar.setVisibility(View.VISIBLE);
        initData();
    }

    /**启动这个Activity的Intent
     * @param context
     * @return
     */
    public static Intent createIntent(Context context) {
        return new Intent(context, TeacherCheckTitleMainActivity.class);
    }

    public void initData() {
        OkManager manager = OkManager.getInstance();
        Map<String, String> map = new HashMap<String, String>();
        map.put("limit","100000");
        OkManager.post(ApiConstants.teacherApi + "/showProChooses", map,new okhttp3.Callback() {
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
                            SharedPreferences sp=getSharedPreferences("processData", MODE_PRIVATE);
                            SharedPreferences.Editor editor=sp.edit();
                            editor.putString("teacher_checkTitle",obj.toString());
                            editor.commit();
                            JSONArray array = new JSONArray(obj.getJSONObject("data").getJSONArray("chooseList"));
                            Log.d(TAG,array.getJSONObject(0).toString());
                            for (int i = 0; i < array.size(); i++) {
                                JSONObject object = array.getJSONObject(i);
                                JSONObject project = object.getJSONObject("project");
                                JSONObject student = object.getJSONObject("student");
                                Log.e(TAG,object.toString());
                                Map<String, Object> map = new HashMap<>();
                                map.put("id", project.getString("id"));
                                map.put("title", project.getString("title"));
                                map.put("name", student.getString("name"));
                                map.put("identifier", student.getString("identifier"));
                                String cStatus = null;
                                if(object.getString("cStatus").equals("0")){
                                    cStatus = "审核不通过";
                                }else if(object.getString("cStatus").equals("1")){
                                    cStatus = "审核通过";
                                }else if(object.getString("cStatus").equals("2")|| object.getString("cStatus").equals("3")){
                                    cStatus = "审核中";
                                }
                                map.put("cStatus", cStatus);
                                if(project.containsKey("taskBook")){
                                    map.put("taskBook", "已上传");
                                }else{
                                    map.put("taskBook", "未上传");
                                }
                                list.add(map);
                            }
                            recycleView.setLayoutManager(new LinearLayoutManager(TeacherCheckTitleMainActivity.this,LinearLayoutManager.VERTICAL,false));
                            //设置适配器
                            MyAdapter adapter = new MyAdapter(TeacherCheckTitleMainActivity.this,list,"2002");
                            adapter.setOnItemClickListener(new MyAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(int position) {
                                    // 这里本来是跳转页面 ，我们就在这里直接让其弹toast来演示
                                    Log.w(TAG,"位置是："+position);
                                    Intent intent = new Intent(TeacherCheckTitleMainActivity.this,TeacherCheckTitleActivity.class);
                                    intent.putExtra("id",list.get(position).get("id").toString());
                                    intent.putExtra("position",position + "");
                                    startActivity(intent);
                                    Toast.makeText(TeacherCheckTitleMainActivity.this , list.get(position).get("title").toString() , Toast.LENGTH_SHORT).show();
                                }
                            });
                            recycleView.setAdapter(adapter);
                            // 设置数据后就要给RecyclerView设置点击事件

                        }else{
                            Toast.makeText(TeacherCheckTitleMainActivity.this, msg, Toast.LENGTH_SHORT).show();
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