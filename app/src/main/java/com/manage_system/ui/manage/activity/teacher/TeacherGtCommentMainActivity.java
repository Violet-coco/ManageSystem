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

public class TeacherGtCommentMainActivity extends AppCompatActivity {

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
        top_title.setText("指导教师评审");
        initData();
    }

    /**启动这个Activity的Intent
     * @param context
     * @return
     */
    public static Intent createIntent(Context context) {
        return new Intent(context, TeacherGtCommentMainActivity.class);
    }

    public void initData() {
        OkManager manager = OkManager.getInstance();
        Map<String, String> map = new HashMap<String, String>();
        manager.post(ApiConstants.teacherApi + "/showGtStudentList", map,new okhttp3.Callback() {
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
                            final JSONArray array = new JSONArray(obj.getJSONArray("data"));
                            SharedPreferences sp=getSharedPreferences("processData", MODE_PRIVATE);
                            SharedPreferences.Editor editor=sp.edit();
                            editor.putString("teacher_gt_comment",obj.toString());
                            editor.commit();
                            for (int i = 0; i < array.size(); i++) {
                                JSONObject object = array.getJSONObject(i);
                                Log.e(TAG,object.toString());
                                Map<String, Object> map = new HashMap<>();
                                map.put("name", object.getString("name"));
                                map.put("identifier", object.getString("identifier"));
                                map.put("pName", object.getString("pName"));
                                if(object.getJSONObject("guidanceTeaGroup").getBoolean("hasReviewed")){
                                    map.put("score",object.getJSONObject("guidanceTeaGroup").getString("scoreTotal"));
                                }else{
                                    map.put("score","暂无分数");
                                }
                                if(object.getJSONObject("guidanceTeaGroup").getBoolean("attendDefence")){
                                    map.put("attendDefence","同意");
                                }else{
                                    map.put("attendDefence","不同意");
                                }
                                list.add(map);
                            }
                            recycleView.setLayoutManager(new LinearLayoutManager(TeacherGtCommentMainActivity.this,LinearLayoutManager.VERTICAL,false));
                            //设置适配器
                            MyAdapter adapter = new MyAdapter(TeacherGtCommentMainActivity.this,list,"2009");
                            adapter.setOnItemClickListener(new MyAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(int position) {
                                    // 这里本来是跳转页面 ，我们就在这里直接让其弹toast来演示
                                    Log.w(TAG,"位置是："+position);
//                                    Intent intent = new Intent(TeacherGtCommentMainActivity.this,TeacherGtCommentActivity.class);
//                                    intent.putExtra("position",position + "");
//                                    startActivity(intent);

                                    if(array.getJSONObject(position).getJSONObject("guidanceTeaGroup").getBoolean("hasReviewed")){
                                        Intent intent = new Intent(TeacherGtCommentMainActivity.this,TeacherGtCommentLookActivity.class);
                                        intent.putExtra("position",position + "");
                                        startActivity(intent);
                                    }else{
                                        Intent intent = new Intent(TeacherGtCommentMainActivity.this,TeacherGtCommentActivity.class);
                                        intent.putExtra("position",position + "");
                                        startActivity(intent);
                                    }
                                }
                            });
                            recycleView.setAdapter(adapter);
                            // 设置数据后就要给RecyclerView设置点击事件

                        }else{
                            Toast.makeText(TeacherGtCommentMainActivity.this, msg, Toast.LENGTH_SHORT).show();
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