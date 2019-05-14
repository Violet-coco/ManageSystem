package com.manage_system.ui.manage.activity.teacher;

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
import com.manage_system.ui.manage.adapter.GradeAdapter;
import com.manage_system.ui.manage.adapter.ListAdapter;
import com.manage_system.ui.personal.GuideTeacherInfoActivity;
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

public class TeacherListMainActivity extends AppCompatActivity {

    @BindView(R.id.recycler)
    RecyclerView recycleView;
    @BindView(R.id.tool_bar)
    RelativeLayout tool_bar;
    @BindView(R.id.iv_back)
    ImageButton iv_back;
    @BindView(R.id.top_title)
    TextView top_title;


    public List<Map<String, Object>> list = new ArrayList<>();

    private static String TAG = "已出题界面";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_tab);
        ButterKnife.bind(this);
        tool_bar.setVisibility(View.VISIBLE);
        top_title.setText("教师列表");
        initData();
    }

    /**
     * 启动这个Activity的Intent
     *
     * @param context
     * @return
     */
    public static Intent createIntent(Context context) {
        return new Intent(context, TeacherListMainActivity.class);
    }

    public void initData() {
        Intent intent = getIntent();
        JSONObject obj = JSON.parseObject(intent.getStringExtra("teachersInfo"));
        Log.w(TAG,obj.toString());
        JSONArray array = new JSONArray(obj.getJSONArray("teaGroup"));
        Log.w(TAG,array.toString());
        for(int i=0;i<array.size();i++){
            JSONObject object = array.getJSONObject(i);
            Map<String, Object> map = new HashMap<>();
            map.put("name", object.getJSONObject("teaAuth").getString("name"));
            map.put("identifier",object.getJSONObject("teaAuth").getString("id"));
            list.add(map);
        }
        recycleView.setLayoutManager(new LinearLayoutManager(TeacherListMainActivity.this, LinearLayoutManager.VERTICAL, false));
        //设置适配器
        ListAdapter adapter = new ListAdapter(TeacherListMainActivity.this, list, "2011");
        adapter.setOnItemClickListener(new ListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                // 这里本来是跳转页面 ，我们就在这里直接让其弹toast来演示
                Log.w(TAG, "位置是：" + position);
                Intent intent = new Intent(TeacherListMainActivity.this, GuideTeacherInfoActivity.class);
                intent.putExtra("tea_info", "from_reply");
                intent.putExtra("position", position + "");
                startActivity(intent);

            }
        });

        recycleView.setAdapter(adapter);
        // 设置数据后就要给RecyclerView设置点击事件

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