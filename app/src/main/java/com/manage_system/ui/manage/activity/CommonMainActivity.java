package com.manage_system.ui.manage.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.manage_system.R;
import com.manage_system.ui.manage.adapter.CommonAdapter;
import com.manage_system.ui.manage.adapter.MyAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CommonMainActivity extends AppCompatActivity {

    @BindView(R.id.recycler)
    RecyclerView recycleView;

    public List<Map<String,Object>> list=new ArrayList<>();

    private static String TAG = "选题界面";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_tab);
        ButterKnife.bind(this);
        initData();
    }

    /**启动这个Activity的Intent
     * @param context
     * @return
     */
    public static Intent createIntent(Context context) {
        return new Intent(context, CommonMainActivity.class);
    }

    public void initData() {
        recycleView.setLayoutManager(new LinearLayoutManager(CommonMainActivity.this,LinearLayoutManager.VERTICAL,false));
        //设置适配器
        CommonAdapter adapter = new CommonAdapter();
        adapter.setOnItemClickListener(new CommonAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                // 这里本来是跳转页面 ，我们就在这里直接让其弹toast来演示
                Intent intent = new Intent(CommonMainActivity.this,CommonActivity.class);
                startActivity(intent);
                Toast.makeText(CommonMainActivity.this , "", Toast.LENGTH_SHORT).show();
            }
        });
        recycleView.setAdapter(adapter);
        // 设置数据后就要给RecyclerView设置点击事件
    }

}