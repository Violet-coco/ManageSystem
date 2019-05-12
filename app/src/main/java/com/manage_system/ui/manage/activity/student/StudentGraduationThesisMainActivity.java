package com.manage_system.ui.manage.activity.student;

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

public class StudentGraduationThesisMainActivity extends AppCompatActivity {

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

    private static String TAG = "毕业论文界面";
    private int times;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_tab);
        ButterKnife.bind(this);
        top_title.setText("提交记录");
        add.setText("新增");
        tool_bar.setVisibility(View.VISIBLE);

        SharedPreferences sp=getSharedPreferences("loginInfo", MODE_PRIVATE);
        if(sp.getString("authority","").equals("1")){
            initData();
        }else{
            add.setVisibility(View.GONE);
            initCheckData();
        }
    }

    /**启动这个Activity的Intent
     * @param context
     * @return
     */
    public static Intent createIntent(Context context) {
        return new Intent(context, StudentGraduationThesisMainActivity.class);
    }

    public void initData() {
        OkManager manager = OkManager.getInstance();
        Map<String, String> map = new HashMap<String, String>();
        manager.post(ApiConstants.studentApi + "/showGraduationProjects", map,new okhttp3.Callback() {
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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(obj.get("statusCode").equals(100)){
                            JSONArray array = new JSONArray(obj.getJSONArray("data"));
                            times = array.size();
                            for (int i = 0; i < array.size(); i++) {
                                JSONObject object = array.getJSONObject(i);
                                Log.e(TAG,object.toString());
                                SharedPreferences sp=getSharedPreferences("personInfo", MODE_PRIVATE);
                                String status = object.getString("cStatus");
                                String cStatus = null;
                                Log.w(TAG,status+"喔喔");
                                if(status.equals("0")){
                                    cStatus = "审核不通过";
                                }else if(status.equals("1")){
                                    cStatus = "审核通过";
                                }else if(status.equals("2")|| status.equals("3")){
                                    cStatus = "审核中";
                                }

                                Map<String, Object> map = new HashMap<>();
                                map.put("id", object.getString("id"));
                                map.put("title", sp.getString("pName" , ""));
                                map.put("submitDate", DateUtil.getDateFormat(object.getString("submitDate")));
                                map.put("cStatus", cStatus);
                                map.put("record_times", i+1);
                                list.add(map);
                            }
                            recycleView.setLayoutManager(new LinearLayoutManager(StudentGraduationThesisMainActivity.this,LinearLayoutManager.VERTICAL,false));
                            //设置适配器
                            MyAdapter adapter = new MyAdapter(StudentGraduationThesisMainActivity.this,list,"1003");
                            adapter.setOnItemClickListener(new MyAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(int position) {
                                    // 这里本来是跳转页面 ，我们就在这里直接让其弹toast来演示
                                    Log.w(TAG,"位置是："+position);
                                    Intent intent = new Intent(StudentGraduationThesisMainActivity.this,StudentGraduationThesisActivity.class);
                                    intent.putExtra("id",list.get(position).get("id").toString());
                                    intent.putExtra("position",position + "");
                                    SharedPreferences sp=getSharedPreferences("processData", MODE_PRIVATE);
                                    SharedPreferences.Editor editor=sp.edit();
                                    editor.putString("graduation_thesis",obj.getJSONArray("data").getJSONObject(position).toString());
                                    editor.commit();
                                    startActivity(intent);
                                    Toast.makeText(StudentGraduationThesisMainActivity.this , list.get(position).get("title").toString() , Toast.LENGTH_SHORT).show();
                                }
                            });
                            recycleView.setAdapter(adapter);
                            // 设置数据后就要给RecyclerView设置点击事件

                        }else if(obj.get("statusCode").equals(101)){
                            Toast.makeText(StudentGraduationThesisMainActivity.this, msg, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(StudentGraduationThesisMainActivity.this,StudentGraduationThesisEditActivity.class);
                            startActivity(intent);
                        }else{
                            Toast.makeText(StudentGraduationThesisMainActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }

    public void initCheckData() {
        final Intent intent = getIntent();
        OkManager manager = OkManager.getInstance();
        Map<String, String> map = new HashMap<String, String>();
        map.put("sid",intent.getStringExtra("stu_id"));
        manager.post(ApiConstants.teacherApi + "/showStuGraduationProject", map,new okhttp3.Callback() {
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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(obj.get("statusCode").equals(100)){
                            JSONArray array = new JSONArray(obj.getJSONArray("data"));
                            times = array.size();
                            for (int i = 0; i < array.size(); i++) {
                                JSONObject object = array.getJSONObject(i);
                                Log.e(TAG,object.toString());
                                SharedPreferences sp=getSharedPreferences("personInfo", MODE_PRIVATE);
                                String status = object.getString("cStatus");
                                String cStatus = null;
                                Log.w(TAG,status+"喔喔");
                                if(status.equals("0")){
                                    cStatus = "审核不通过";
                                }else if(status.equals("1")){
                                    cStatus = "审核通过";
                                }else if(status.equals("2")|| status.equals("3")){
                                    cStatus = "审核中";
                                }

                                Map<String, Object> map = new HashMap<>();
                                map.put("id", object.getString("id"));

                                map.put("title",intent.getStringExtra("pName"));
                                map.put("submitDate", DateUtil.getDateFormat(object.getString("submitDate")));
                                map.put("cStatus", cStatus);
                                map.put("record_times", i+1);
                                list.add(map);
                            }
                            recycleView.setLayoutManager(new LinearLayoutManager(StudentGraduationThesisMainActivity.this,LinearLayoutManager.VERTICAL,false));
                            //设置适配器
                            MyAdapter adapter = new MyAdapter(StudentGraduationThesisMainActivity.this,list,"1003");
                            adapter.setOnItemClickListener(new MyAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(int position) {
                                    // 这里本来是跳转页面 ，我们就在这里直接让其弹toast来演示
                                    Log.w(TAG,"位置是："+position);
                                    Intent intent = new Intent(StudentGraduationThesisMainActivity.this,StudentGraduationThesisActivity.class);
                                    intent.putExtra("id",list.get(position).get("id").toString());
                                    intent.putExtra("position",position + "");
                                    Intent intent1 = getIntent();
                                    intent.putExtra("stu_id",intent1.getStringExtra("stu_id"));
                                    SharedPreferences sp=getSharedPreferences("processData", MODE_PRIVATE);
                                    SharedPreferences.Editor editor=sp.edit();
                                    editor.putString("graduation_thesis",obj.getJSONArray("data").getJSONObject(position).toString());
                                    editor.commit();
                                    startActivity(intent);
                                    Toast.makeText(StudentGraduationThesisMainActivity.this , list.get(position).get("title").toString() , Toast.LENGTH_SHORT).show();
                                }
                            });
                            recycleView.setAdapter(adapter);
                            // 设置数据后就要给RecyclerView设置点击事件

                        }else{
                            Toast.makeText(StudentGraduationThesisMainActivity.this, msg, Toast.LENGTH_SHORT).show();
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
                if(times < 3){
                    Intent intent = new Intent(StudentGraduationThesisMainActivity.this,StudentGraduationThesisEditActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(StudentGraduationThesisMainActivity.this, "提交记录不能超过10次！", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

}