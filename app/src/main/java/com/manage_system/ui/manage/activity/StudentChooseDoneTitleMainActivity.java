package com.manage_system.ui.manage.activity;

import android.content.Context;
import android.content.Intent;
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

public class StudentChooseDoneTitleMainActivity extends AppCompatActivity {

    @BindView(R.id.recycler)
    RecyclerView recycleView;
    @BindView(R.id.tool_bar)
    RelativeLayout tool_bar;
    @BindView(R.id.iv_back)
    ImageButton iv_back;

    public List<Map<String,Object>> list=new ArrayList<>();
    public List<Map<String,Object>> teacher_info=new ArrayList<>();

    private static String TAG = "选题界面";
    private String cStatus = null;

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
        return new Intent(context, StudentChooseDoneTitleMainActivity.class);
    }

    public void initData() {
        OkManager manager = OkManager.getInstance();
        Map<String, String> map = new HashMap<String, String>();

        manager.post(ApiConstants.studentApi+"/showChoosePro", map,new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "onFailure: ",e);
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();
                final JSONObject obj = JSON.parseObject(responseBody);
                Log.e(TAG,obj.toString());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(obj.get("statusCode").equals(100)) {
                            JSONArray array = new JSONArray(obj.getJSONArray("data"));
                            Log.d(TAG,array.toString()+"");
                            Log.d(TAG,array.getJSONObject(0).toString());
                            for (int i = 0; i < array.size(); i++) {
                                JSONObject object = array.getJSONObject(i);
                                JSONObject project = object.getJSONObject("project");
                                JSONObject teacher = object.getJSONObject("setRole");
                                Log.e(TAG,object.toString());
                                String status = object.getString("cStatus");
                                Log.w(TAG,status+"喔喔");
                                if(status.equals("0")){
                                    cStatus = "审核不通过";
                                }else if(status.equals("1")){
                                    cStatus = "审核通过";
                                }else if(status.equals("2")|| status.equals("3")){
                                    cStatus = "审核中";
                                }

                                Map<String, Object> map = new HashMap<>();
                                Map<String, Object> map1 = new HashMap<>();
                                map.put("id", project.getString("id"));
                                map.put("title", project.getString("title"));
                                map.put("genre", project.getString("genre"));
                                map.put("source", project.getString("source"));
                                map.put("name", teacher.getString("name"));
                                map.put("reset", project.getString("reset"));
                                map.put("cStatus", cStatus);
                                list.add(map);
                                map1.put("name", teacher.getString("name"));
                                map1.put("identifier", teacher.getString("identifier"));
                                map1.put("pro", teacher.getString("title"));
                                map1.put("email", teacher.getString("email"));
                                map1.put("bindTel", teacher.getString("bindTel"));
                                map1.put("college", teacher.getString("college"));
                                teacher_info.add(map1);
                            }
                            recycleView.setLayoutManager(new LinearLayoutManager(StudentChooseDoneTitleMainActivity.this,LinearLayoutManager.VERTICAL,false));
                            //设置适配器
                            MyAdapter adapter = new MyAdapter(StudentChooseDoneTitleMainActivity.this,list,"1002");
                            adapter.setOnItemClickListener(new MyAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(int position) {
                                    // 这里本来是跳转页面 ，我们就在这里直接让其弹toast来演示
                                    Intent intent = new Intent(StudentChooseDoneTitleMainActivity.this,StudentChooseDoneTitleActivity.class);
                                    intent.putExtra("id",list.get(position).get("id").toString());
                                    intent.putExtra("position",position+"");
                                    Bundle bundle = new Bundle();
                                    //须定义一个list用于在budnle中传递需要传递的ArrayList<Object>,这个是必须要的
                                    ArrayList bundlelist = new ArrayList();
                                    ArrayList bundlelist1 = new ArrayList();
                                    bundlelist.add(list.get(position));
                                    bundlelist1.add(teacher_info.get(position));
                                    bundle.putParcelableArrayList("list",bundlelist);
                                    bundle.putParcelableArrayList("teacher_info",bundlelist1);
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                    Toast.makeText(StudentChooseDoneTitleMainActivity.this , list.get(position).get("title").toString() , Toast.LENGTH_SHORT).show();
                                }
                            });
                            recycleView.setAdapter(adapter);
                            // 设置数据后就要给RecyclerView设置点击事件

                        }else{
                            Toast.makeText(StudentChooseDoneTitleMainActivity.this, obj.getJSONObject("data").getString("msg"), Toast.LENGTH_SHORT).show();
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