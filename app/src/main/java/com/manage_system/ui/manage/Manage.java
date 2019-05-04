package com.manage_system.ui.manage;

import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.manage_system.MyApp;
import com.manage_system.net.ApiConstants;
import com.manage_system.utils.OkManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

public class Manage {
    private static String TAG = "aaa";

    public static void getData() {
        OkManager manager = OkManager.getInstance();
        Map<String, String> map = new HashMap<String, String>();
        map.put("proName","");
        map.put("proType","");
        map.put("proSource","");
        map.put("tecId","");
        map.put("tecName","");
        map.put("offset","");
        map.put("limit","100000");
        manager.post(ApiConstants.studentApi + "/showProjects", map,new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "onFailure: ",e);
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();
                final JSONObject obj = JSON.parseObject(responseBody);
                SharedPreferences sp=MyApp.getAppContext().getSharedPreferences("chooseTitle", MODE_PRIVATE);
                //获取编辑器
                SharedPreferences.Editor editor=sp.edit();
                editor.putString("obj", obj.toString());
                //提交修改
                editor.commit();

            }
        });
    }
}
