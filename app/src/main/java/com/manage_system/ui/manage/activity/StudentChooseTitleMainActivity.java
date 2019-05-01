package com.manage_system.ui.manage.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.manage_system.R;
import com.manage_system.net.ApiConstants;
import com.manage_system.ui.manage.adapter.MyAdapter;
import com.manage_system.ui.personal.GuideTeacherInfoActivity;
import com.manage_system.utils.OkManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Response;

public class StudentChooseTitleMainActivity extends AppCompatActivity {

    @BindView(R.id.recycler)
    RecyclerView recycleView;

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
        return new Intent(context, StudentChooseTitleMainActivity.class);
    }

    public void initData() {
        OkManager manager = OkManager.getInstance();
        Map<String, String> map = new HashMap<String, String>();
//        proName,           (题目名称，可空，模糊查询)
//        proType,           (题目类型，可空)
//        proSource,         (题目来源，可空)
//        tecId,             (教师编号，可空)
//        tecName,           (教师名称，可空，模糊查询)
//        offset,            ((分页参数)偏移量，可空，默认为0)
//        limit              ((分页参数)查询条数，可空，默认为4)
        map.put("proName","");
        map.put("proType","");
        map.put("proSource","");
        map.put("tecId","");
        map.put("tecName","");
        map.put("offset","");
        map.put("limit","");
        manager.post(ApiConstants.studentApi + "/showProjects", map,new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "onFailure: ",e);
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();
                final JSONObject obj = JSON.parseObject(responseBody);
                final String data = obj.toString();
                Log.e(TAG,obj.toString());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(obj.get("statusCode").equals(100)){
                            recycleView.setLayoutManager(new LinearLayoutManager(StudentChooseTitleMainActivity.this,LinearLayoutManager.VERTICAL,false));
                            //设置适配器
                            MyAdapter adapter = new MyAdapter(StudentChooseTitleMainActivity.this,data);
                            recycleView.setAdapter(adapter);
                        }else{

                        }
                    }
                });

            }
        });
    }

}