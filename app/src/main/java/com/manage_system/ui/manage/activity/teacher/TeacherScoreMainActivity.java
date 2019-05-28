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
import com.manage_system.LoginActivity;
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

public class TeacherScoreMainActivity extends AppCompatActivity {

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
        top_title.setText("答辩打分");
        initData();
    }

    /**启动这个Activity的Intent
     * @param context
     * @return
     */
    public static Intent createIntent(Context context) {
        return new Intent(context, TeacherScoreMainActivity.class);
    }

    public void initData() {
        OkManager manager = OkManager.getInstance();
        Map<String, String> map = new HashMap<String, String>();
        manager.post(ApiConstants.teacherApi + "/showDefStudents", map,new okhttp3.Callback() {
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
                            SharedPreferences sp=getSharedPreferences("processData", MODE_PRIVATE);
                            SharedPreferences.Editor editor=sp.edit();
                            editor.putString("showDefStudents",obj.getJSONObject("data").toString());
                            editor.commit();
                            String score;
                            for (int i = 0; i < array.size(); i++) {
                                JSONObject object = array.getJSONObject(i);
                                Map<String, Object> map = new HashMap<>();
                                map.put("identifier", object.getString("identifier"));
                                map.put("name", object.getString("name"));
                                map.put("pName", object.getString("pName"));
                                map.put("gt_identifier", object.getJSONObject("gt").getString("id"));
                                map.put("gt_name", object.getJSONObject("gt").getString("name"));
                                if(object.containsKey("defScore")){
                                    if(object.getJSONObject("defScore").getBooleanValue("hasScored")){
                                        score = object.getJSONObject("defScore").getString("scoreTotal");
                                    }else{
                                        score = "未打分";
                                    }
                                }else{
                                    score = "未打分";
                                }

                                map.put("scoreTotal", score);
                                list.add(map);
                            }
                            recycleView.setLayoutManager(new LinearLayoutManager(TeacherScoreMainActivity.this,LinearLayoutManager.VERTICAL,false));
//                            设置适配器
                            MyAdapter adapter = new MyAdapter(TeacherScoreMainActivity.this,list,"2005");
                            adapter.setOnItemClickListener(new MyAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(int position) {
                                    // 这里本来是跳转页面 ，我们就在这里直接让其弹toast来演示
                                    Log.w(TAG,"位置是："+position);
                                    if(array.getJSONObject(position).containsKey("defScore")){
                                        if(array.getJSONObject(position).getJSONObject("defScore").getBooleanValue("hasScored")){
                                            Intent intent = new Intent(TeacherScoreMainActivity.this,TeacherScoreActivity.class);
                                            intent.putExtra("position",position + "");
                                            startActivity(intent);
                                        }else{
                                            Intent intent = new Intent(TeacherScoreMainActivity.this,TeacherScoreDetailActivity.class);
                                            intent.putExtra("position",position + "");
                                            startActivity(intent);
                                        }
                                    }else{
                                        Intent intent = new Intent(TeacherScoreMainActivity.this,TeacherScoreDetailActivity.class);
                                        intent.putExtra("position",position + "");
                                        startActivity(intent);
                                    }


                                }
                            });
                            recycleView.setAdapter(adapter);

                        }else if(obj.get("statusCode").equals(102)){
                            Toast.makeText(TeacherScoreMainActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(TeacherScoreMainActivity.this,LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }else{
                            Toast.makeText(TeacherScoreMainActivity.this, msg, Toast.LENGTH_SHORT).show();
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